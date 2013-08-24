package com.nut.bettersettlers.fragment.dialog;

import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.data.MapProvider;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.iab.IabConsts;
import com.nut.bettersettlers.iab.IabConsts.MapContainer;
import com.nut.bettersettlers.misc.Consts;

public class SeafarersDialogFragment extends DialogFragment {	
	public static SeafarersDialogFragment newInstance() {
		return new SeafarersDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context mContext = getActivity().getApplicationContext();
		LayoutInflater inflater =
			(LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout =
			inflater.inflate(R.layout.seafarers, (ViewGroup) getActivity().findViewById(R.id.seafarers_root), false);

		final MainActivity mainActivity = (MainActivity) getActivity();
		final MapFragment mapFragment = (MapFragment) mainActivity.getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		Set<String> maps = mainActivity.getOwnedMaps();

		ImageView headingForNewShoresButton = (ImageView) layout.findViewById(R.id.heading_for_new_shores_item);
		headingForNewShoresButton.setImageResource(R.drawable.sea_heading_for_new_shores_button);
		headingForNewShoresButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    ft.addToBackStack(null);
			    
			    ExpansionDialogFragment.newInstance(MapProvider.MapSize.HEADING_FOR_NEW_SHORES, MapProvider.MapSize.HEADING_FOR_NEW_SHORES_EXP)
			    	.show(ft, "ExpansionDialogFragment");
			}
		});
		
		TextView text = (TextView) layout.findViewById(R.id.seafarers_intro);
		FrameLayout buyAllContainer = (FrameLayout) layout.findViewById(R.id.buy_all_container);
		ImageView buyAllButton = (ImageView) buyAllContainer.findViewById(R.id.buy_all_button);
		if (containsAll(maps)) {
			// We have them all, make it invisible (and the text)
			buyAllContainer.setVisibility(View.GONE);
			text.setVisibility(View.GONE);
		} else {
			buyAllContainer.setVisibility(View.VISIBLE);
			text.setVisibility(View.VISIBLE);
			buyAllButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mainActivity.purchaseItem(IabConsts.BUY_ALL);
					mainActivity.getSupportFragmentManager().popBackStack();
					mainActivity.getSupportFragmentManager().popBackStack();
					mainActivity.getAnalytics().trackPageView(
							String.format(Consts.ANALYTICS_SEAFARERS_PURCHASE_FORMAT, IabConsts.BUY_ALL));
				}
			});
		}

		AlertDialog ret = new AlertDialog.Builder(mainActivity)
			.create();
		ret.setView(layout, 0, 0, 0, 5); // Remove top padding
		ret.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
		return ret;
	}
	
	public boolean contains(Set<String> maps, String test) {
		return containsAll(maps) || maps.contains(test);
	}
	
	public boolean containsAll(Set<String> maps) {
		return maps.contains(IabConsts.BUY_ALL) || maps.size() == MapContainer.values().length;
	}
}
