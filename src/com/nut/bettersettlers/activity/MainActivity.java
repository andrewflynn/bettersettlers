package com.nut.bettersettlers.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.MapSpecs.MapSize;
import com.nut.bettersettlers.data.MapSpecs.MapType;
import com.nut.bettersettlers.fragment.GraphFragment;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.fragment.dialog.GraphMoreDialogFragment;
import com.nut.bettersettlers.fragment.dialog.HelpDialogFragment;
import com.nut.bettersettlers.fragment.dialog.MapMoreDialogFragment;
import com.nut.bettersettlers.fragment.dialog.MapSizeDialogFragment;
import com.nut.bettersettlers.fragment.dialog.MapTypeDialogFragment;
import com.nut.bettersettlers.fragment.dialog.PlacementsMoreDialogFragment;
import com.nut.bettersettlers.misc.Consts;

public class MainActivity extends FragmentActivity {
	private static final String X = MainActivity.class.getSimpleName();
	
	private static final String STATE_SHOW_GRAPH = "STATE_SHOW_GRAPH";
	private static final String STATE_SHOW_PLACEMENTS = "STATE_SHOW_PLACEMENTS";
	private static final int DIALOG_PROGRESS_ID = 1;
	
	private MapFragment mMapFragment;
	private GraphFragment mGraphFragment;
	
	private WakeLock mWakeLock;

	///////////////////////////////
	// Activity method overrides //
	///////////////////////////////
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == DIALOG_PROGRESS_ID){
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Better Settlers");
			progressDialog.setMessage("Generating");
			progressDialog.setIndeterminate(true);
			return progressDialog;
		}

		return super.onCreateDialog(id);
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
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		mGraphFragment = (GraphFragment) getSupportFragmentManager().findFragmentById(R.id.graph_fragment);
		
		GoogleAnalyticsTracker.getInstance().start(Consts.ANALYTICS_KEY, this);
		
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
					.setText(R.string.roll_tracker)
					.setTabListener(tabListener), showGraph);
		} else {
			if (showGraph) {
				setupActionBarCompat(R.drawable.hdpi_menu_rolls, R.id.menu_rolls, new TitleClickListener());
			} else if (showPlacements) {
				setupActionBarCompat(R.drawable.hdpi_menu_placements, R.id.menu_placements, new TitleClickListener());
			} else {
				setupActionBarCompat(R.drawable.hdpi_menu_map, R.id.menu_map, new TitleClickListener());
			}
		}

		if (showGraph) {
			showGraphFragment();
		} else {
			showMapFragment();
		}
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
			if (tab.getText().equals(getString(R.string.roll_tracker))) {
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
			if (tab.getText().equals(getString(R.string.roll_tracker))) {
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
				iv.setImageResource(R.drawable.hdpi_menu_placements);
				getMapFragment().togglePlacements(true);
				showPlacements();
			} else if (iv.getId() == R.id.menu_placements) {
				iv.setId(R.id.menu_rolls);
				iv.setImageResource(R.drawable.hdpi_menu_rolls);
				getMapFragment().togglePlacements(false);
				showGraphFragment();
			} else if (iv.getId() == R.id.menu_rolls) {
				iv.setId(R.id.menu_map);
				iv.setImageResource(R.drawable.hdpi_menu_map);
				showMapFragment();
			}
		}
    }
    
    private class RefreshClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			getMapFragment().asyncMapShuffle();
			GoogleAnalyticsTracker.getInstance().trackPageView(Consts.ANALYTICS_SHUFFLE_MAP);
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
			MapSize size = getMapFragment().getMapSize();
			int selected = 0;
			if (size == MapSize.STANDARD) {
				selected = 0;
			} else if (size == MapSize.LARGE) {
				selected = 1;
			} else if (size == MapSize.XLARGE) {
				selected = 2;
			} else if (size == MapSize.EUROPE) {
				selected = 3;
			}
			MapSizeDialogFragment.newInstance(selected).show(getSupportFragmentManager(), "MapSizeDialogFragment");
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
			clearActionButtonCompats(R.drawable.hdpi_menu_map, R.id.menu_map);
			addActionButtonCompat(R.drawable.hdpi_refresh, R.string.shuffle_map, new RefreshClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.hdpi_map_size, R.string.map_size, new MapSizeClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.hdpi_map_type, R.string.map_type, new MapTypeClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.hdpi_menu, R.string.more, new MapMoreClickListener(), false /* separaterAfter */);
		}
    }
    
    private void showPlacements() {
		if (!Consts.AT_LEAST_HONEYCOMB) {
			clearActionButtonCompats(R.drawable.hdpi_menu_placements, R.id.menu_placements);
			addActionButtonCompat(R.drawable.hdpi_arrow_left, R.string.prev, new PrevClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.hdpi_arrow_right, R.string.next, new NextClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.hdpi_menu, R.string.more, new PlacementsMoreClickListener(), false /* separaterAfter */);
		}
    }
    
    private void showGraphFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.hide(mMapFragment);
		ft.show(mGraphFragment);
		ft.commit();

		if (!Consts.AT_LEAST_HONEYCOMB) {
			clearActionButtonCompats(R.drawable.hdpi_menu_rolls, R.id.menu_rolls);
			addActionButtonCompat(R.drawable.hdpi_help, R.string.help, new HelpClickListener(), false /* separaterAfter */);
			addActionButtonCompat(R.drawable.hdpi_menu, R.string.more, new GraphMoreClickListener(), false /* separaterAfter */);
		}
    }
}