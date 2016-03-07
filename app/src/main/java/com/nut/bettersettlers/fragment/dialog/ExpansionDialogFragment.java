package com.nut.bettersettlers.fragment.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.data.MapSize;
import com.nut.bettersettlers.data.MapSizePair;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.util.Analytics;
import com.nut.bettersettlers.util.Consts;

public class ExpansionDialogFragment extends DialogFragment {
	private static final String SIZE_KEY = "SIZE";
	
	private MapSizePair mMapSizePair;
	
	public static ExpansionDialogFragment newInstance(MapSizePair sizePair) {
		ExpansionDialogFragment f = new ExpansionDialogFragment();
		
		Bundle args = new Bundle();
		args.putParcelable(SIZE_KEY, sizePair);
		f.setArguments(args);
		f.setStyle(STYLE_NO_TITLE, 0);
		
		return f;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        Analytics.trackView(getActivity(), Analytics.VIEW_EXPANSIONS_MENU);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expansion, container, false);
		
		mMapSizePair = getArguments().getParcelable(SIZE_KEY);

		ImageView smallButton = (ImageView) view.findViewById(R.id.expansion_small_item);
		smallButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Analytics.track(getActivity(), Analytics.CATEGORY_EXPANSION_MENU,
						Analytics.ACTION_BUTTON, Analytics.EXPANSION_SMALL);
				choice(mMapSizePair.reg);
			}
		});

		ImageView largeButton = (ImageView) view.findViewById(R.id.expansion_large_item);
		largeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Analytics.track(getActivity(), Analytics.CATEGORY_EXPANSION_MENU,
						Analytics.ACTION_BUTTON, Analytics.EXPANSION_LARGE);
				choice(mMapSizePair.exp);
			}
		});

		getDialog().getWindow().getAttributes().windowAnimations = R.style.FadeDialogAnimation;

		return view;
	}
	
	private void choice(MapSize size) {
		final MainActivity mainActivity = (MainActivity) getActivity();
		final MapFragment mapFragment = mainActivity.getMapFragment();
		
		if (size == MapSize.THE_FOG_ISLAND || size == MapSize.THE_FOG_ISLAND_EXP) {
			maybeShowFogIslandExplanation();
		}
		
		mapFragment.sizeChoice(size);

		mainActivity.getSupportFragmentManager().popBackStack();
		mainActivity.getSupportFragmentManager().popBackStack();
		mainActivity.getSupportFragmentManager().popBackStack();
	}
	
	private void maybeShowFogIslandExplanation() {
		SharedPreferences prefs = getActivity().getSharedPreferences(Consts.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		boolean shownWhatsNew = prefs.getBoolean(Consts.SHARED_PREFS_KEY_FOG_ISLAND_HELP, false);
		if (!shownWhatsNew) {
			FogIslandHelpDialogFragment.newInstance().show(getFragmentManager(), "TheFogIslandHelpDialog");
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.putBoolean(Consts.SHARED_PREFS_KEY_FOG_ISLAND_HELP, true);
			prefsEditor.apply();
		}
	}
}
