package com.nut.bettersettlers.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.MapConsts.MapType;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.misc.Consts;

public class MenuDialogFragment extends DialogFragment {
	private static final String SHARED_PREFS_NAME = "Graph";
	private static final String SHARED_PREFS_SHOWN_HELP = "Help";
	
	private ImageView betterSettlersButton;
	private ImageView traditionalButton;
	private ImageView randomButton;
	
	public static MenuDialogFragment newInstance() {
		return new MenuDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context mContext = getActivity().getApplicationContext();
		LayoutInflater inflater =
			(LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout =
			inflater.inflate(R.layout.menu, (ViewGroup) getActivity().findViewById(R.id.menu_root));
		
		betterSettlersButton = (ImageView) layout.findViewById(R.id.better_settlers_overlay);
		betterSettlersButton.setOnClickListener(ROTATE);
		traditionalButton = (ImageView) layout.findViewById(R.id.traditional_overlay);
		traditionalButton.setOnClickListener(ROTATE);
		randomButton = (ImageView) layout.findViewById(R.id.random_overlay);
		randomButton.setOnClickListener(ROTATE);

		MapFragment mapFragment = ((MainActivity) getActivity()).getMapFragment();
		switch (mapFragment.getMapType()) {
		case FAIR:
			betterSettlersButton.setVisibility(View.VISIBLE);
			traditionalButton.setVisibility(View.GONE);
			randomButton.setVisibility(View.GONE);
			break;
		case TRADITIONAL:
			betterSettlersButton.setVisibility(View.GONE);
			traditionalButton.setVisibility(View.VISIBLE);
			randomButton.setVisibility(View.GONE);
			break;
		case RANDOM:
			betterSettlersButton.setVisibility(View.GONE);
			traditionalButton.setVisibility(View.GONE);
			randomButton.setVisibility(View.VISIBLE);
			break;
		}

		ImageView rollTrackerButton = (ImageView) layout.findViewById(R.id.roll_tracker_item);
		rollTrackerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.showGraphFragment();
				((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_USE_ROLL_TRACKER);

				SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
				boolean shownWhatsNew = prefs.getBoolean(SHARED_PREFS_SHOWN_HELP, false);
				if (!shownWhatsNew) {
					GraphHelpDialogFragment.newInstance().show(getFragmentManager(), "GraphHelpDialog");
					SharedPreferences.Editor prefsEditor = prefs.edit();
					prefsEditor.putBoolean(SHARED_PREFS_SHOWN_HELP, true);
					prefsEditor.commit();
				}
			}
		});
		
		ImageView shuffleProbsButton = (ImageView) layout.findViewById(R.id.shuffle_probs_item);
		shuffleProbsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getMapFragment().asyncProbsShuffle();
				((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_SHUFFLE_PROBABILITIES);
			}
		});
		ImageView shuffleHarborsButton = (ImageView) layout.findViewById(R.id.shuffle_harbors_item);
		shuffleHarborsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getMapFragment().asyncHarborsShuffle();
				((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_SHUFFLE_HARBORS);
			}
		});

		AlertDialog ret = new AlertDialog.Builder(getActivity())
			.create();
		ret.setView(layout, 0, 0, 0, 5); // Remove top padding
		ret.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
		return ret;
	}
	
	private final OnClickListener ROTATE = new OnClickListener() {
		@Override
		public void onClick(View v) {
			MapFragment mapFragment = ((MainActivity) getActivity()).getMapFragment();
			MapType type = mapFragment.getMapType();
			CatanMap size = mapFragment.getMapSize();
			
			Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
			Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
			
			ImageView outButton = null;
			ImageView inButton = null;
			
			switch (type) {
			case FAIR:
				outButton = betterSettlersButton;
				
				// Traditional only for settlers boards
				if (size.getName().equals("standard")
						|| size.getName().equals("large")
						|| size.getName().equals("xlarge")) {
					inButton = traditionalButton;
					mapFragment.traditionalChoice();
				} else {
					inButton = randomButton;
					mapFragment.randomChoice();
				}
				break;
			case TRADITIONAL:
				outButton = traditionalButton;
				
				inButton = randomButton;
				mapFragment.randomChoice();
				break;
			case RANDOM:
				outButton = randomButton;
				
				inButton = betterSettlersButton;
				mapFragment.betterSettlersChoice();
				break;
			}
			
			if (inButton != null && outButton != null) {
				inButton.startAnimation(fadeIn);
				outButton.startAnimation(fadeOut);
				
				inButton.setVisibility(View.VISIBLE);
				outButton.setVisibility(View.GONE);
			}
		}
	};
}
