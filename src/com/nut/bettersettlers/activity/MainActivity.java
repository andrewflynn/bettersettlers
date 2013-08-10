package com.nut.bettersettlers.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.GraphFragment;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.fragment.dialog.LegalDialogFragment;
import com.nut.bettersettlers.misc.Consts;
import com.nut.bettersettlers.misc.MapActivityTabListener;

public class MainActivity extends Activity {
	private static final String X = MainActivity.class.getSimpleName();
	
	private static final String STATE_TAB_CHOSEN = "STATE_TAB_CHOSEN";
	
	private PowerManager.WakeLock mWakeLock;
	
	private MapFragment mMapFragment;
	private GraphFragment mGraphFragment;

	///////////////////////////////
	// Activity method overrides //
	///////////////////////////////

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			outState.putString(STATE_TAB_CHOSEN, getActionBar().getSelectedTab().getText().toString());
		}
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
		mGraphFragment = (GraphFragment) getFragmentManager().findFragmentById(R.id.graph_fragment);
		
		GoogleAnalyticsTracker.getInstance().start(Consts.ANALYTICS_KEY, this);
		
		mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MapActivityWakeLock");

		boolean showGraph = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Set up tabs
			ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			
			if (savedInstanceState != null && savedInstanceState.getString(STATE_TAB_CHOSEN).equals(getString(R.string.roll_tracker))) {
				showGraph = true;
			}
			actionBar.addTab(actionBar.newTab()
					.setText(R.string.map)
					.setTabListener(new MapActivityTabListener(mMapFragment)), !showGraph);
			actionBar.addTab(actionBar.newTab()
					.setText(R.string.roll_tracker)
					.setTabListener(new MapActivityTabListener(mGraphFragment)), showGraph);
		} else {
			// Use v4 support
		}
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (showGraph) {
			ft.hide(mMapFragment);
			ft.show(mGraphFragment);
		} else {
			ft.show(mMapFragment);
			ft.hide(mGraphFragment);
		}
		ft.commit();
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
}