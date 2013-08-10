package com.nut.bettersettlers.misc;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.nut.bettersettlers.fragment.GraphFragment;
import com.nut.bettersettlers.fragment.MapFragment;

public class MapActivityTabListener implements TabListener {
	private final FragmentManager mFragmentManager;
	private final String mMapTitle;
	private final MapFragment mMapFragment;
	private final String mGraphTitle;
	private final GraphFragment mGraphFragment;
	private boolean mShowGraph = false;
	
	public MapActivityTabListener(FragmentManager fragmentManager, String mapTitle, MapFragment mapFragment,
			String graphTitle, GraphFragment graphFragment) {
		mFragmentManager = fragmentManager;
		mMapTitle = mapTitle;
		mMapFragment = mapFragment;
		mGraphTitle = graphTitle;
		mGraphFragment = graphFragment;
	}
	
	public boolean showGraph() {
		return mShowGraph;
	}
	
	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction unused) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		if (tab.getText().equals(mMapTitle)) {
			ft.show(mMapFragment);
			mShowGraph = false;
		}
		if (tab.getText().equals(mGraphTitle)) {
			ft.show(mGraphFragment);
			mShowGraph = true;
		}
		ft.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction unused) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		if (tab.getText().equals(mMapTitle)) {
			ft.hide(mMapFragment);
		}
		if (tab.getText().equals(mGraphTitle)) {
			ft.hide(mGraphFragment);
		}
		ft.commit();
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction unused) { /* Do nothing */ }
}