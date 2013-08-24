package com.nut.bettersettlers.fragment.dialog;

import java.util.Set;

import android.app.AlertDialog;
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
import com.nut.bettersettlers.data.MapSizePair;
import com.nut.bettersettlers.iab.IabConsts;
import com.nut.bettersettlers.iab.MapContainer;
import com.nut.bettersettlers.util.Consts;

public class SeafarersDialogFragment extends DialogFragment {
	private MainActivity mMainActivity;
	private Set<String> mMaps;
	
	public static SeafarersDialogFragment newInstance() {
		SeafarersDialogFragment f = new SeafarersDialogFragment();
		f.setStyle(STYLE_NO_TITLE, 0);
		return f;
	}
	
	private void setupButton(ImageView button, final MapContainer map) {
		if (contains(mMaps, map.id)) {
			button.setImageResource(map.sizePair.buttonResId);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				    ft.addToBackStack(null);
				    
				    ExpansionDialogFragment.newInstance(map.sizePair)
				            .show(ft, "ExpansionDialogFragment");
				}
			});
		} else {
			button.setImageResource(map.sizePair.bwButtonResId);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mMainActivity.purchaseItem(map);	
					mMainActivity.getSupportFragmentManager().popBackStack();
					mMainActivity.getSupportFragmentManager().popBackStack();
					mMainActivity.getAnalytics().trackPageView(
							String.format(Consts.ANALYTICS_SEAFARERS_PURCHASE_FORMAT, map.id));
				}
			});
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.seafarers, container, false);

		mMainActivity = (MainActivity) getActivity();
		mMaps = mMainActivity.getOwnedMaps();
		
		// Heading for New Shores is always purchased
		ImageView headingForNewShoresButton = (ImageView) view.findViewById(R.id.heading_for_new_shores_item);
		headingForNewShoresButton.setImageResource(R.drawable.sea_heading_for_new_shores_button);
		headingForNewShoresButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    ft.addToBackStack(null);
			    
			    ExpansionDialogFragment.newInstance(MapSizePair.HEADING_FOR_NEW_SHORES)
			    	.show(ft, "ExpansionDialogFragment");
			}
		});
		
		TextView text = (TextView) view.findViewById(R.id.seafarers_intro);
		FrameLayout buyAllContainer = (FrameLayout) view.findViewById(R.id.buy_all_container);
		ImageView buyAllButton = (ImageView) buyAllContainer.findViewById(R.id.buy_all_button);
		if (containsAll(mMaps)) {
			// We have them all, make it invisible (and the text)
			buyAllContainer.setVisibility(View.GONE);
			text.setVisibility(View.GONE);
		} else {
			buyAllContainer.setVisibility(View.VISIBLE);
			text.setVisibility(View.VISIBLE);
			buyAllButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mMainActivity.purchaseItem(IabConsts.BUY_ALL);
					mMainActivity.getSupportFragmentManager().popBackStack();
					mMainActivity.getSupportFragmentManager().popBackStack();
					mMainActivity.getAnalytics().trackPageView(
							String.format(Consts.ANALYTICS_SEAFARERS_PURCHASE_FORMAT, IabConsts.BUY_ALL));
				}
			});
		}
		
		getDialog().getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
		
		return view;
	}
	
	public boolean contains(Set<String> maps, String test) {
		return containsAll(maps) || maps.contains(test);
	}
	
	public boolean containsAll(Set<String> maps) {
		return maps.contains(IabConsts.BUY_ALL) || maps.size() == MapContainer.values().length;
	}
}
