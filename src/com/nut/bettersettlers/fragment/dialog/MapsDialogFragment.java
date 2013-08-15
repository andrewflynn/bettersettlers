package com.nut.bettersettlers.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.misc.Consts;

public class MapsDialogFragment extends DialogFragment {
	private static final String SHARED_PREFS_NAME = "Maps";
	private static final String SHARED_PREFS_SHOWN_HELP = "NoSeafarers";
	
	public static MapsDialogFragment newInstance() {
		return new MapsDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context mContext = getActivity().getApplicationContext();
		LayoutInflater inflater =
			(LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout =
			inflater.inflate(R.layout.maps, (ViewGroup) getActivity().findViewById(R.id.maps_root));

		ImageView settlersButton = (ImageView) layout.findViewById(R.id.settlers_item);
		settlersButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    ft.addToBackStack(null);
			    
				SettlersDialogFragment.newInstance().show(ft, "SettlersDialogFragment");
				((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_VIEW_SETTLERS);
			}
		});
		
		ImageView seafarersButton = (ImageView) layout.findViewById(R.id.seafarers_item);
		MapFragment mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		if (mapFragment.getShowSeafarers()) {
			seafarersButton.setVisibility(View.VISIBLE);
			seafarersButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				    ft.addToBackStack(null);
				    
					SeafarersDialogFragment.newInstance().show(ft, "SeafarersDialogFragment");
					((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_VIEW_SETTLERS);
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
		
		ImageView moreButton = (ImageView) layout.findViewById(R.id.more_item);
		moreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    ft.addToBackStack(null);
			    
				MenuDialogFragment.newInstance().show(ft, "MenuDialogFragment");
				((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_VIEW_MORE);
			}
		});

		AlertDialog ret = new AlertDialog.Builder(getActivity())
			.create();
		ret.setView(layout, 0, 0, 0, 5); // Remove top padding
		ret.getWindow().getAttributes().windowAnimations = R.style.FadeDialogAnimation;
		return ret;
	}
}
