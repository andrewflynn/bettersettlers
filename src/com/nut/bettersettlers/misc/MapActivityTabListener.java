package com.nut.bettersettlers.misc;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;

import com.nut.bettersettlers.R;

public class MapActivityTabListener implements TabListener {
	private Fragment mFragment;
	
	public MapActivityTabListener(Fragment fragment) {
		mFragment = fragment;
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.show(mFragment);
        //ft.replace(R.id.main_fragment_container, mFragment, null);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.hide(mFragment);
		//ft.remove(mFragment);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) { /* Do nothing */ }
}
