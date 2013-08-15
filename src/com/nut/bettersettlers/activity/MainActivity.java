package com.nut.bettersettlers.activity;

import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ImageView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.MapProvider;
import com.nut.bettersettlers.data.MapProvider.MapSize;
import com.nut.bettersettlers.fragment.GraphFragment;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.fragment.dialog.AboutDialogFragment;
import com.nut.bettersettlers.fragment.dialog.FogIslandHelpDialogFragment;
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
	private static final String STATE_TITLE_ID = "STATE_TITLE_ID";
	
	private static final String SHARED_PREFS_IAB_STATE_CURRENT = "SHARED_PREFS_STATE_CURRENT";
	private static final String SHARED_PREFS_THE_FOG_ISLAND_NAME = "Seafarers";
	private static final String SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP = "TheFogIsland";
	private static final int DIALOG_IAB_NOT_SUPPORTED = 1;
	
	private MapFragment mMapFragment;
	private GraphFragment mGraphFragment;
	private MapProvider mMapProvider;

	private ImageView mTitle;
	private int mTitleId;
	private ImageView mInfoButton;
	
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
			//Log.i(X, "onBillingSupported: " + supported);
			if (supported) {
				restoreIabState();
				mIabSupported = true;
				mMapFragment.setShowSeafarers(mIabSupported);
			}
		}
		
		@Override
		public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
				String developerPayload) {
			//Log.i(X, "onPurcaseStateChange: ");
			//Log.i(X, "  purchaseState: " + purchaseState);
			//Log.i(X, "  itemId: " + itemId);
			//Log.i(X, "  developerPayload: " + developerPayload);
			
			if (purchaseState == PurchaseState.PURCHASED) {
				mOwnedMaps.add(itemId);
				if (getMapFragment() != null && !itemId.equals(IabConsts.BUY_ALL)) {
					getMapFragment().sizeChoice(getMapProvider().getMapSizeByProductId(itemId));
				}
				
				if ("seafarers.the_fog_island".equals(itemId)) {
					SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_THE_FOG_ISLAND_NAME, Context.MODE_PRIVATE);
					boolean shownWhatsNew = prefs.getBoolean(SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP, false);
					if (!shownWhatsNew) {
						FogIslandHelpDialogFragment.newInstance().show(getSupportFragmentManager(), "TheFogIslandHelpDialog");
						SharedPreferences.Editor prefsEditor = prefs.edit();
						prefsEditor.putBoolean(SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP, true);
						prefsEditor.commit();
					}
				}
			}
		}

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
			//Log.i(X, "onRequestPurchaseResponse: ");
			//Log.i(X, "  request: " + request);
			//Log.i(X, "  responseCode: " + responseCode);
        }

        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
			//Log.i(X, "onRestoreTransactionsResponse: ");
			//Log.i(X, "  request: " + request);
			//Log.i(X, "  responseCode: " + responseCode);
            if (responseCode == ResponseCode.RESULT_OK) {
                //Log.d(X, "completed RestoreTransactions request");
                // Update the shared preferences so that we don't perform
                // a RestoreTransactions again.
                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(SHARED_PREFS_IAB_STATE_CURRENT, true);
                edit.commit();
            } else {
                //Log.d(X, "RestoreTransactions error: " + responseCode);
            }
        }
	}

	///////////////////////////////
	// Activity method overrides //
	///////////////////////////////
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_IAB_NOT_SUPPORTED) {
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
		
		outState.putInt(STATE_TITLE_ID, mTitleId);
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

		mTitle = (ImageView) findViewById(R.id.title_button);
		if (savedInstanceState != null && savedInstanceState.getInt(STATE_TITLE_ID) != 0) {
			setTitleButtonText(savedInstanceState.getInt(STATE_TITLE_ID));
		} else {
			setTitleButtonText(MapSize.STANDARD.titleDrawableId);
		}
		
		mInfoButton = (ImageView) findViewById(R.id.info_button);
		mInfoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutDialogFragment.newInstance().show(getSupportFragmentManager(), "AboutDialog");
			}
		});
		
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

		if (showGraph) {
			showGraphFragment();
		} else {
			showMapFragment();
			if (showPlacements) {
				getMapFragment().showPlacements(false);
			} else {
				getMapFragment().hidePlacements(false);
			}
		}
		
		// IAB
		mIabHandler = new Handler();
		mPurchaseObserver = new IabPurchaseObserver(mIabHandler);
		ResponseHandler.register(mPurchaseObserver);
		
		mIabService = new BillingService();
		mIabService.setContext(this);
		
		mOwnedMaps.add(MapContainer.HEADING_FOR_NEW_SHORES.id);
		
		if (!mIabService.checkBillingSupported()) {
			//Log.i(X, "Could not connect to Market client");
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
		
		String buyAllRet = prefs.getString(Base64.encode(IabConsts.BUY_ALL.getBytes()), null);
		
		if (buyAllRet != null) {
			String attempt = Obfuscate.decode(id, buyAllRet);
			if (attempt != null && attempt.equals(IabConsts.BUY_ALL)) {
				mOwnedMaps.add(attempt);
			}
		}
	}
	
	public void purchaseItem(MapContainer map) {
		//Log.i(X, "Buying " + map.id);
		//FakePurchaseDialogFragment.newInstance(map.id).show(getSupportFragmentManager(), "FakePurchase");
		/*
		 * if (!mIabService.requestPurchase(IabConsts.FAKE_PRODUCT_ID, null)) {
			showDialog(DIALOG_IAB_NOT_SUPPORTED);
		}
		*/
        if (!mIabService.requestPurchase(map.id, null)) {
            showDialog(DIALOG_IAB_NOT_SUPPORTED);
        }
	}
	
	public void purchaseItem(String id) {
		//Log.i(X, "Buying " + id);
		//FakePurchaseDialogFragment.newInstance(id).show(getSupportFragmentManager(), "FakePurchase");
        if (!mIabService.requestPurchase(id, null)) {
            showDialog(DIALOG_IAB_NOT_SUPPORTED);
        }
	}
    
    public GoogleAnalyticsTracker getAnalytics() {
    	return mAnalytics;
    }
    
    public void setTitleButtonText(int resId) {
    	mTitle.setBackgroundResource(resId);
    	mTitleId = resId;
    }
    
    public Set<String> getOwnedMaps() {
    	return mOwnedMaps;
    }
    
    public void addOwnedMap(String map) {
    	mOwnedMaps.add(map);
    }
    
    public MapProvider getMapProvider() {
    	return mMapProvider;
    }
    
    public MapFragment getMapFragment() {
    	return (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
    }
    
    public GraphFragment getGraphFragment() {
    	return (GraphFragment) getSupportFragmentManager().findFragmentById(R.id.graph_fragment);
    }
    
    public void showMapFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//ft.setCustomAnimations(R.anim.long_slide_in_left, R.anim.long_slide_out_right);
		ft.hide(mGraphFragment);
		ft.show(mMapFragment);
		ft.commit();
    }
    
    public void showGraphFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//ft.setCustomAnimations(R.anim.long_slide_in_right, R.anim.long_slide_out_left);
		ft.hide(mMapFragment);
		ft.show(mGraphFragment);
		ft.addToBackStack("GraphFragment");
		ft.commit();
    }
}