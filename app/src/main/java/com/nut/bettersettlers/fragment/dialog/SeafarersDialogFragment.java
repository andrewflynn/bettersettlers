package com.nut.bettersettlers.fragment.dialog;

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
import com.nut.bettersettlers.util.Analytics;

import java.util.Set;

public class SeafarersDialogFragment extends DialogFragment {
	private MainActivity mMainActivity;
	private Set<String> mMaps;
	
	public static SeafarersDialogFragment newInstance() {
		SeafarersDialogFragment f = new SeafarersDialogFragment();
		f.setStyle(STYLE_NO_TITLE, 0);
		return f;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        Analytics.trackView(getActivity(), Analytics.VIEW_SEAFARERS_MENU);
	}
	
	private void setupButton(ImageView button, final MapContainer map) {
		if (contains(mMaps, map.id)) {
			button.setImageResource(map.sizePair.buttonResId);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				    ft.addToBackStack(null);

                    Analytics.track(getActivity(), Analytics.CATEGORY_SEAFARERS_MENU,
                            Analytics.ACTION_BUTTON, map.id);
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
                    Analytics.track(getActivity(), Analytics.CATEGORY_SEAFARERS_MENU,
                            Analytics.ACTION_PURCHASE_OFFER, map.id);
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
		
		setupButton((ImageView) view.findViewById(R.id.the_four_islands_item), MapContainer.THE_FOUR_ISLANDS);
		setupButton((ImageView) view.findViewById(R.id.the_fog_island_item), MapContainer.THE_FOG_ISLAND);
		setupButton((ImageView) view.findViewById(R.id.through_the_desert_item), MapContainer.THROUGH_THE_DESERT);
		setupButton((ImageView) view.findViewById(R.id.the_forgotten_tribe_item), MapContainer.THE_FORGOTTEN_TRIBE);
		setupButton((ImageView) view.findViewById(R.id.cloth_for_catan_item), MapContainer.CLOTH_FOR_CATAN);
		setupButton((ImageView) view.findViewById(R.id.the_pirate_islands_item), MapContainer.THE_PIRATE_ISLANDS);
		setupButton((ImageView) view.findViewById(R.id.the_wonders_of_catan_item), MapContainer.THE_WONDERS_OF_CATAN);
		setupButton((ImageView) view.findViewById(R.id.new_world_item), MapContainer.NEW_WORLD);
		
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
			
			// If we can figure out the prices, use those
			if (mMainActivity.getSinglePrice() != null && mMainActivity.getBuyAllPrice() != null) {
				text.setText(getString(R.string.seafarers_intro_format,
						mMainActivity.getSinglePrice(),
						mMainActivity.getBuyAllPrice()));
			}
			
			buyAllButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mMainActivity.purchaseItem(IabConsts.BUY_ALL);
					mMainActivity.getSupportFragmentManager().popBackStack();
					mMainActivity.getSupportFragmentManager().popBackStack();
                    Analytics.track(getActivity(), Analytics.CATEGORY_SEAFARERS_MENU,
							Analytics.ACTION_PURCHASE_OFFER, IabConsts.BUY_ALL);
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
