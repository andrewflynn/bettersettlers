package com.nut.bettersettlers.fragment.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.util.Analytics;

public class MapsDialogFragment extends DialogFragment {
	private static final String SHARED_PREFS_NAME = "Maps";
	private static final String SHARED_PREFS_SHOWN_HELP = "NoSeafarers";
	
	public static MapsDialogFragment newInstance() {
		MapsDialogFragment f = new MapsDialogFragment();
		f.setStyle(STYLE_NO_TITLE, 0);
		return f;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        Analytics.trackView(getActivity(), Analytics.VIEW_MAPS_MENU);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.maps, container, false);

		ImageView settlersButton = (ImageView) view.findViewById(R.id.settlers_item);
		settlersButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    ft.addToBackStack(null);
			    
				SettlersDialogFragment.newInstance().show(ft, "SettlersDialogFragment");
                Analytics.track(getActivity(), Analytics.CATEGORY_MAPS_MENU,
                        Analytics.ACTION_BUTTON, Analytics.SEE_SETTLERS);
			}
		});
		
		maybeShowSeafarers(view.findViewById(R.id.seafarers_item),
				(MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment));
		
		ImageView moreButton = (ImageView) view.findViewById(R.id.more_item);
		moreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    ft.addToBackStack(null);
			    
				MenuDialogFragment.newInstance().show(ft, "MenuDialogFragment");
                Analytics.track(getActivity(), Analytics.CATEGORY_MAPS_MENU,
                        Analytics.ACTION_BUTTON, Analytics.SEE_MORE);
			}
		});

		getDialog().getWindow().getAttributes().windowAnimations = R.style.FadeDialogAnimation;
		
		return view;
	}
	
	private void maybeShowSeafarers(View seafarersButton, MapFragment mapFragment) {
		if (mapFragment.getShowSeafarers()) {
			seafarersButton.setVisibility(View.VISIBLE);
			seafarersButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				    ft.addToBackStack(null);
				    
					SeafarersDialogFragment.newInstance().show(ft, "SeafarersDialogFragment");
                    Analytics.track(getActivity(), Analytics.CATEGORY_MAPS_MENU,
                            Analytics.ACTION_BUTTON, Analytics.SEE_SEAFARERS);
				}
			});
		} else {
			SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
			boolean shownWhatsNew = prefs.getBoolean(SHARED_PREFS_SHOWN_HELP, false);
			if (!shownWhatsNew) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				NoSeafarersDialogFragment.newInstance().show(ft, "NoSeafarersDialogFragment");
				SharedPreferences.Editor prefsEditor = prefs.edit();
				prefsEditor.putBoolean(SHARED_PREFS_SHOWN_HELP, true);
				prefsEditor.commit();
			}
			
			seafarersButton.setVisibility(View.GONE);
		}
	}
}
