package com.nut.bettersettlers.activity;

import java.util.HashSet;
import java.util.Set;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.MapConsts.MapType;
import com.nut.bettersettlers.data.MapProvider;
import com.nut.bettersettlers.fragment.GraphFragment;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.fragment.dialog.GraphMoreDialogFragment;
import com.nut.bettersettlers.fragment.dialog.HelpDialogFragment;
import com.nut.bettersettlers.fragment.dialog.MapMoreDialogFragment;
import com.nut.bettersettlers.fragment.dialog.MapSizeDialogFragment;
import com.nut.bettersettlers.fragment.dialog.MapSizeWithSeafarersDialogFragment;
import com.nut.bettersettlers.fragment.dialog.MapTypeDialogFragment;
import com.nut.bettersettlers.fragment.dialog.PlacementsMoreDialogFragment;
import com.nut.bettersettlers.iab.BillingService;
import com.nut.bettersettlers.iab.BillingService.RequestPurchase;
import com.nut.bettersettlers.iab.BillingService.RestoreTransactions;
import com.nut.bettersettlers.iab.IabConsts;
import com.nut.bettersettlers.iab.IabConsts.MapContainer;
import com.nut.bettersettlers.iab.IabConsts.PurchaseState;
import com.nut.bettersettlers.iab.IabConsts.ResponseCode;
import com.nut.bettersettlers.iab.Obfuscate;
import com.nut.bettersettlers.iab.PurchaseObserver;
import com.nut.bettersettlers.iab.ResponseHandler;
import com.nut.bettersettlers.iab.util.Base64;
import com.nut.bettersettlers.misc.Consts;

public class MainActivity extends FragmentActivity {
	private static final String X = "BetterSettlers";
	
	private static final String STATE_SHOW_GRAPH = "STATE_SHOW_GRAPH";
	private static final String STATE_SHOW_PLACEMENTS = "STATE_SHOW_PLACEMENTS";
	private static final String STATE_THEFT_ORDER = "STATE_THEFT_ORDER";
	private static final String SHARED_PREFS_IAB_STATE_CURRENT = "SHARED_PREFS_STATE_CURRENT";
	private static final int DIALOG_PROGRESS_ID = 1;
	private static final int DIALOG_IAB_NOT_SUPPORTED = 2;
	
	private MapFragment mMapFragment;
	private GraphFragment mGraphFragment;
	private MapProvider mMapProvider;
	
	private WakeLock mWakeLock;
	private GoogleAnalyticsTracker mAnalytics;
	
	private Handler mIabHandler;
	private IabPurchaseObserver mPurchaseObserver;
	private BillingService mIabService;
	private boolean mIabSupported = false;
	private Set<String> mOwnedMaps = new HashSet<String>();
	
	private class IabPurchaseObserver extends PurchaseObserver {
		public IabPurchaseObserver(Handler handler) {
			super(MainActivity.this, handler);
		}
		
		@Override
		public void onBillingSupported(boolean supported) {
			Log.i(X, "onBillingSupported: " + supported);
			if (supported) {
				restoreIabState();
				mIabSupported = true;
				mMapFragment.setShowSeafarers(mIabSupported);
				
				if (Consts.AT_LEAST_HONEYCOMB) {
					MainActivity.this.invalidateOptionsMenu();
				}
			}
		}
		
		@Override
		public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
				String developerPayload) {
			Log.i(X, "onPurcaseStateChange: ");
			Log.i(X, "  purchaseState: " + purchaseState);
			Log.i(X, "  itemId: " + itemId);
			Log.i(X, "  developerPayload: " + developerPayload);
			
			if (purchaseState == PurchaseState.PURCHASED) {
				mOwnedMaps.add(itemId);
				getMapFragment().sizeChoice(getMapProvider().getMap(itemId));
			}
		}

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
			Log.i(X, "onRequestPurchaseResponse: ");
			Log.i(X, "  request: " + request);
			Log.i(X, "  responseCode: " + responseCode);
        }

        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
			Log.i(X, "onRestoreTransactionsResponse: ");
			Log.i(X, "  request: " + request);
			Log.i(X, "  responseCode: " + responseCode);
            if (responseCode == ResponseCode.RESULT_OK) {
                Log.d(X, "completed RestoreTransactions request");
                // Update the shared preferences so that we don't perform
                // a RestoreTransactions again.
                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(SHARED_PREFS_IAB_STATE_CURRENT, true);
                edit.commit();
            } else {
                Log.d(X, "RestoreTransactions error: " + responseCode);
            }
        }
	}

	///////////////////////////////
	// Activity method overrides //
	///////////////////////////////
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_PROGRESS_ID){
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Better Settlers");
			progressDialog.setMessage("Generating");
			progressDialog.setIndeterminate(true);
			return progressDialog;
		} else if (id == DIALOG_IAB_NOT_SUPPORTED) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle(R.string.iab_not_supported_title)
	            .setIcon(android.R.drawable.stat_sys_warning)
	            .setMessage(R.string.iab_not_supported_title)
	            .setCancelable(false)
	            .setPositiveButton(android.R.string.ok, null)
	            .setNegativeButton(R.string.learn_more, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.iab_help_url)));
	                    startActivity(intent);
	                }
	            });
	        return builder.create();
		} else {
			return super.onCreateDialog(id);
		}
	}

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if (mGraphFragment.isVisible()) {
			outState.putBoolean(STATE_SHOW_GRAPH, true);
		} else if (mMapFragment.isVisible()) {
			if (mMapFragment.showingPlacements()) {
				outState.putBoolean(STATE_SHOW_PLACEMENTS, true);
			}
		}
		
		if (mMapFragment != null && mMapFragment.getMapSize().getTheftOrder() != null
				&& !mMapFragment.getMapSize().getTheftOrder().isEmpty()) {
			outState.putIntegerArrayList(STATE_THEFT_ORDER, mMapFragment.getMapSize().getTheftOrder());
		}
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		if (savedInstanceState != null && savedInstanceState.getStringArrayList(STATE_THEFT_ORDER) != null) {
			mMapProvider = new MapProvider(this, savedInstanceState.getIntegerArrayList(STATE_THEFT_ORDER));
		} else {
			mMapProvider = new MapProvider(this);
		}
		
		mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		mGraphFragment = (GraphFragment) getSupportFragmentManager().findFragmentById(R.id.graph_fragment);
		
		mAnalytics = GoogleAnalyticsTracker.getInstance();
		mAnalytics.start(Consts.ANALYTICS_KEY, Consts.ANALYTICS_INTERVAL, this);
		
		mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MapActivityWakeLock");

		boolean showGraph = false;
		boolean showPlacements = false;
		if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_SHOW_GRAPH)) {
			showGraph = true;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_SHOW_PLACEMENTS)) {
			showPlacements = true;
		}
		
		if (Consts.AT_LEAST_HONEYCOMB) {
			// Set up tabs
			ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setTitle("");

			MapActivityTabListener tabListener = new MapActivityTabListener();
			actionBar.addTab(actionBar.newTab()
					.setText(R.string.map)
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(R.string.placements)
					.setTabListener(tabListener), showPlacements);
			actionBar.addTab(actionBar.newTab()
					.setText(R.string.rolls)
					.setTabListener(tabListener), showGraph);
		} else {
			if (showGraph) {
				setupActionBarCompat(R.drawable.title_rolls, R.id.menu_rolls, new TitleClickListener());
			} else if (showPlacements) {
				setupActionBarCompat(R.drawable.title_placements, R.id.menu_placements, new TitleClickListener());
			} else {
				setupActionBarCompat(R.drawable.title_map, R.id.menu_map, new TitleClickListener());
			}
		}

		if (showGraph) {
			showGraphFragment();
		} else {
			showMapFragment();
			if (showPlacements) {
				showPlacements();
			}
		}
		
		// IAB
		mIabHandler = new Handler();
		mPurchaseObserver = new IabPurchaseObserver(mIabHandler);
		ResponseHandler.register(mPurchaseObserver);
		
		mIabService = new BillingService();
		mIabService.setContext(this);
		
		mOwnedMaps.add("heading_for_new_shores");
		
		if (!mIabService.checkBillingSupported()) {
			Log.i(X, "Could not connect to Market client");
			// No action as it should default to not showing Seafarers maps
		}
	}
    @Override
    protected void onStart() {
        super.onStart();
        ResponseHandler.register(mPurchaseObserver);
        initializeOwnedItems();
    }
    @Override
    protected void onStop() {
        super.onStop();
        ResponseHandler.unregister(mPurchaseObserver);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		mWakeLock.acquire();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mWakeLock.release();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mAnalytics.stop();
		mIabService.unbind();
	}
	
	private void restoreIabState() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		boolean current = prefs.getBoolean(SHARED_PREFS_IAB_STATE_CURRENT, false);
		if (!current) {
			mIabService.restoreTransactions();
		}
	}
	
	private void initializeOwnedItems() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				doInitializeOwnedItems();
			}
		}).start();
	}
	
	private void doInitializeOwnedItems() {
    	String id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		SharedPreferences prefs = getSharedPreferences(Consts.SHARED_PREFS_SEAFARERS_KEY, Context.MODE_PRIVATE);
        
		for (MapContainer mapContainer : MapContainer.values()) {
			String ret = prefs.getString(Base64.encode(mapContainer.id.getBytes()), null);
			
			if (ret != null) {
				String attempt = Obfuscate.decode(id, ret);
				if (attempt != null && attempt.equals(mapContainer.id)) {
					mOwnedMaps.add(attempt);
				}
			}
		}
	}
	
	public void purchaseItem(String itemId, String mapId) {
		Log.i(X, "Buying " + itemId + " for " + mapId);
        if (!mIabService.requestPurchase(itemId, mapId)) {
            showDialog(DIALOG_IAB_NOT_SUPPORTED);
        }
	}
	
	public class MapActivityTabListener implements TabListener {
		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction unused) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (tab.getText().equals(getString(R.string.map))) {
				ft.show(mMapFragment);
			}
			if (tab.getText().equals(getString(R.string.placements))) {
				ft.show(mMapFragment);
				getMapFragment().togglePlacements(true);
			}
			if (tab.getText().equals(getString(R.string.rolls))) {
				ft.show(mGraphFragment);
			}
			ft.commit();
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction unused) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (tab.getText().equals(getString(R.string.map))) {
				ft.hide(mMapFragment);
			}
			if (tab.getText().equals(getString(R.string.placements))) {
				ft.hide(mMapFragment);
				getMapFragment().togglePlacements(false);
			}
			if (tab.getText().equals(getString(R.string.rolls))) {
				ft.hide(mGraphFragment);
			}
			ft.commit();
		}

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction unused) { /* Do nothing */ }
	}
    /**
     * Returns the {@link ViewGroup} for the action bar on phones (compatibility action bar).
     * Can return null, and will return null on Honeycomb.
     */
    public ViewGroup getActionBarCompat() {
        return (ViewGroup) findViewById(R.id.actionbar_compat);
    }
    
    public GoogleAnalyticsTracker getAnalytics() {
    	return mAnalytics;
    }
    
    public void setupActionBarCompat(int iconResId, int viewId, View.OnClickListener clickListener) {
        final ViewGroup actionBarCompat = getActionBarCompat();
        if (actionBarCompat == null) {
            return;
        }
        actionBarCompat.setVisibility(View.VISIBLE);

        // Create the button
        ImageButton actionButton = new ImageButton(this, null,
                R.attr.actionbarCompatButtonStyleNoOrange);
        actionButton.setLayoutParams(new ViewGroup.LayoutParams(
        		ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        actionButton.setId(viewId);
        actionButton.setImageResource(iconResId);
        actionButton.setScaleType(ImageView.ScaleType.CENTER);
        actionButton.setOnClickListener(clickListener);
        
        actionBarCompat.addView(actionButton);

        LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.FILL_PARENT);
        springLayoutParams.weight = 1;
        
        View emptyView = new View(this, null, R.attr.actionbarCompatButtonStyle);
        emptyView.setLayoutParams(springLayoutParams);
        actionBarCompat.addView(emptyView);
    }

    /**
     * Adds an action bar button to the compatibility action bar (on phones).
     */
    public View addActionButtonCompat(int iconResId, int textResId,
            View.OnClickListener clickListener, boolean separatorAfter) {
        final ViewGroup actionBar = getActionBarCompat();
        if (actionBar == null) {
            return null;
        }

        // Create the separator
        ImageView separator = new ImageView(this, null, R.attr.actionbarCompatSeparatorStyle);
        separator.setLayoutParams(
                new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));

        // Create the button
        ImageButton actionButton = new ImageButton(this, null,
                R.attr.actionbarCompatButtonStyle);
        actionButton.setLayoutParams(new ViewGroup.LayoutParams(
                (int) getResources().getDimension(R.dimen.actionbar_compat_height),
                ViewGroup.LayoutParams.FILL_PARENT));
        actionButton.setImageResource(iconResId);
        actionButton.setScaleType(ImageView.ScaleType.CENTER);
        actionButton.setContentDescription(getResources().getString(textResId));
        actionButton.setOnClickListener(clickListener);

        // Add separator and button to the action bar in the desired order
        if (!separatorAfter) {
            actionBar.addView(separator);
        }
        
        actionBar.addView(actionButton);

        if (separatorAfter) {
            actionBar.addView(separator);
        }

        return actionButton;
    }
    
    /**
     * Removes all action bar buttons (keeps the title).
     */
    public void clearActionButtonCompats(int iconResId, int keepId) {
        final ViewGroup actionBar = getActionBarCompat();
        if (actionBar == null) {
            return;
        }
    	
        actionBar.removeAllViews();
        setupActionBarCompat(iconResId, keepId, new TitleClickListener());
    }

    /**
     * Sets the indeterminate loading state of a refresh button added with
     * {@link ActivityHelper#addActionButtonCompatFromMenuItem(android.view.MenuItem)}
     * (where the item ID was menu_refresh).
     */
    /* UNUSED?? */
    public void setRefreshActionButtonCompatState(boolean refreshing) {
        View refreshButton = findViewById(R.id.menu_refresh);
        View refreshIndicator = findViewById(R.id.menu_refresh_progress);

        if (refreshButton != null) {
            refreshButton.setVisibility(refreshing ? View.GONE : View.VISIBLE);
        }
        if (refreshIndicator != null) {
            refreshIndicator.setVisibility(refreshing ? View.VISIBLE : View.GONE);
        }
    }
    
    public void showProgressBar() {
    	showDialog(DIALOG_PROGRESS_ID);
    }
    
    public void killProgressBar() {
    	removeDialog(DIALOG_PROGRESS_ID);
    }
    
    private class TitleClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			ImageView iv = (ImageView) v;
			if (iv == null) {
				return;
			}
			
			if (iv.getId() == R.id.menu_map) {
				iv.setId(R.id.menu_placements);
				iv.setImageResource(R.drawable.title_placements);
				getMapFragment().togglePlacements(true);
				showPlacements();
			} else if (iv.getId() == R.id.menu_placements) {
				iv.setId(R.id.menu_rolls);
				iv.setImageResource(R.drawable.title_rolls);
				getMapFragment().togglePlacements(false);
				showGraphFragment();
			} else if (iv.getId() == R.id.menu_rolls) {
				iv.setId(R.id.menu_map);
				iv.setImageResource(R.drawable.title_map);
				showMapFragment();
			}
		}
    }
    
    private class RefreshClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			getMapFragment().asyncMapShuffle();
			mAnalytics.trackPageView(Consts.ANALYTICS_SHUFFLE_MAP);
		}
    }
    
    private class PrevClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			getMapFragment().prevPlacement();
		}
    }
    
    private class NextClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			getMapFragment().nextPlacement();
		}
    }
    
    private class MapSizeClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			CatanMap map = getMapFragment().getMapSize();
			int selected = 0;
			if (map.getName() == MapProvider.MapSize.STANDARD.name) {
				selected = 0;
			} else if (map.getName() == MapProvider.MapSize.LARGE.name) {
				selected = 1;
			} else if (map.getName() == MapProvider.MapSize.XLARGE.name) {
				selected = 2;
			}
			if (mIabSupported) {
				MapSizeWithSeafarersDialogFragment.newInstance(selected).show(getSupportFragmentManager(), "MapSizeWithSeafarersDialogFragment");
			} else {
				MapSizeDialogFragment.newInstance(selected).show(getSupportFragmentManager(), "MapSizeDialogFragment");
				//MapSizeWithSeafarersDialogFragment.newInstance(selected).show(getSupportFragmentManager(), "MapSizeWithSeafarersDialogFragment");
			}
		}
    }
    
    private class MapTypeClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			MapType type = getMapFragment().getMapType();
			int selected = 0;
			if (type == MapType.FAIR) {
				selected = 0;
			} else if (type == MapType.TRADITIONAL) {
				selected = 1;
			} else if (type == MapType.RANDOM) {
				selected = 2;
			}
			MapTypeDialogFragment.newInstance(selected).show(getSupportFragmentManager(), "MapTypeDialogFragment");
		}
    }
    
    private class MapMoreClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			MapMoreDialogFragment.newInstance().show(getSupportFragmentManager(), "MapMoreDialogFragment");
		}
    }
    
    private class PlacementsMoreClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			PlacementsMoreDialogFragment.newInstance().show(getSupportFragmentManager(), "PlacementsMoreDialogFragment");
		}
    }
    
    private class GraphMoreClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			GraphMoreDialogFragment.newInstance().show(getSupportFragmentManager(), "GraphMoreDialogFragment");
		}
    }
    
    private class HelpClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			HelpDialogFragment.newInstance().show(getSupportFragmentManager(), "HelpDialogFragment");
		}
    }
    
    public Set<String> getOwnedMaps() {
    	return mOwnedMaps;
    }
    
    public MapProvider getMapProvider() {
    	return mMapProvider;
    }
    
    private MapFragment getMapFragment() {
    	return (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
    }
    
    private GraphFragment getGraphFragment() {
    	return (GraphFragment) getSupportFragmentManager().findFragmentById(R.id.graph_fragment);
    }
    
    private void showMapFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.hide(mGraphFragment);
		ft.show(mMapFragment);
		ft.commit();
		
		if (!Consts.AT_LEAST_HONEYCOMB) {
			clearActionButtonCompats(R.drawable.title_map, R.id.menu_map);
			addActionButtonCompat(R.drawable.map_refresh, R.string.shuffle_map, new RefreshClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.map_size, R.string.map_size, new MapSizeClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.map_type, R.string.map_type, new MapTypeClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.menu, R.string.more, new MapMoreClickListener(), false /* separaterAfter */);
		}
    }
    
    private void showPlacements() {
		if (!Consts.AT_LEAST_HONEYCOMB) {
			clearActionButtonCompats(R.drawable.title_placements, R.id.menu_placements);
			addActionButtonCompat(R.drawable.placements_left, R.string.prev, new PrevClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.placements_right, R.string.next, new NextClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.menu, R.string.more, new PlacementsMoreClickListener(), false /* separaterAfter */);
		}
    }
    
    private void showGraphFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.hide(mMapFragment);
		ft.show(mGraphFragment);
		ft.commit();

		if (!Consts.AT_LEAST_HONEYCOMB) {
			clearActionButtonCompats(R.drawable.title_rolls, R.id.menu_rolls);
			addActionButtonCompat(R.drawable.graph_help, R.string.help, new HelpClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.menu, R.string.more, new GraphMoreClickListener(), false /* separaterAfter */);
		}
    }
}