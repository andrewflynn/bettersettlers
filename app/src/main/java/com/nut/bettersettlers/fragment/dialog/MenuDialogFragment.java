package com.nut.bettersettlers.fragment.dialog;

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
import com.nut.bettersettlers.data.MapType;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.util.Analytics;

public class MenuDialogFragment extends DialogFragment {
	private static final String SHARED_PREFS_NAME = "Graph";
	private static final String SHARED_PREFS_SHOWN_HELP = "Help";
	
	private ImageView betterSettlersButton;
	private ImageView traditionalButton;
	private ImageView randomButton;
	
	public static MenuDialogFragment newInstance() {
		MenuDialogFragment f = new MenuDialogFragment();
		f.setStyle(STYLE_NO_TITLE, 0);
		return f;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		((MainActivity) getActivity()).trackView(Analytics.VIEW_MORE_MENU);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu, container, false);
		
		betterSettlersButton = (ImageView) view.findViewById(R.id.better_settlers_overlay);
		betterSettlersButton.setOnClickListener(ROTATE);
		traditionalButton = (ImageView) view.findViewById(R.id.traditional_overlay);
		traditionalButton.setOnClickListener(ROTATE);
		randomButton = (ImageView) view.findViewById(R.id.random_overlay);
		randomButton.setOnClickListener(ROTATE);

		MapFragment mapFragment = ((MainActivity) getActivity()).getMapFragment();
		switch (mapFragment.getMapType()) {
		case MapType.FAIR:
			betterSettlersButton.setVisibility(View.VISIBLE);
			traditionalButton.setVisibility(View.GONE);
			randomButton.setVisibility(View.GONE);
			break;
		case MapType.TRADITIONAL:
			betterSettlersButton.setVisibility(View.GONE);
			traditionalButton.setVisibility(View.VISIBLE);
			randomButton.setVisibility(View.GONE);
			break;
		case MapType.RANDOM:
			betterSettlersButton.setVisibility(View.GONE);
			traditionalButton.setVisibility(View.GONE);
			randomButton.setVisibility(View.VISIBLE);
			break;
		}

		ImageView rollTrackerButton = (ImageView) view.findViewById(R.id.roll_tracker_item);
		rollTrackerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.showGraphFragment();
				((MainActivity) getActivity()).trackEvent(Analytics.CATEGORY_MENU_MENU,
						Analytics.ACTION_BUTTON, Analytics.USE_ROLL_TRACKER);

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
		
		ImageView shuffleProbsButton = (ImageView) view.findViewById(R.id.shuffle_probs_item);
		shuffleProbsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getMapFragment().asyncProbsShuffle();
				((MainActivity) getActivity()).trackEvent(Analytics.CATEGORY_MENU_MENU,
						Analytics.ACTION_BUTTON, Analytics.SHUFFLE_PROBABILITIES);
			}
		});
		ImageView shuffleHarborsButton = (ImageView) view.findViewById(R.id.shuffle_harbors_item);
		shuffleHarborsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getSupportFragmentManager().popBackStack();
				mainActivity.getMapFragment().asyncHarborsShuffle();
				((MainActivity) getActivity()).trackEvent(Analytics.CATEGORY_MENU_MENU,
						Analytics.ACTION_BUTTON, Analytics.SHUFFLE_HARBORS);
			}
		});
		
		getDialog().getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
		
		return view;
	}
	
	private final OnClickListener ROTATE = new OnClickListener() {
		@Override
		public void onClick(View v) {
			MapFragment mapFragment = ((MainActivity) getActivity()).getMapFragment();
			int type = mapFragment.getMapType();
			CatanMap size = mapFragment.getCatanMap();
			
			Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
			Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
			
			ImageView outButton = null;
			ImageView inButton = null;
			
			switch (type) {
			case MapType.FAIR:
				outButton = betterSettlersButton;
				
				// Traditional only for settlers boards
				if (size.name.equals("standard")
						|| size.name.equals("large")
						|| size.name.equals("xlarge")) {
					inButton = traditionalButton;
					mapFragment.typeChoice(MapType.TRADITIONAL);
				} else {
					inButton = randomButton;
					mapFragment.typeChoice(MapType.RANDOM);
				}
				break;
			case MapType.TRADITIONAL:
				outButton = traditionalButton;
				
				inButton = randomButton;
				mapFragment.typeChoice(MapType.RANDOM);
				break;
			case MapType.RANDOM:
				outButton = randomButton;
				
				inButton = betterSettlersButton;
				mapFragment.typeChoice(MapType.FAIR);
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
