package com.nut.bettersettlers.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.data.MapProvider;
import com.nut.bettersettlers.fragment.MapFragment;

public class ExpansionDialogFragment extends DialogFragment {
	private static final String SHARED_PREFS_NAME = "Seafarers";
	private static final String SHARED_PREFS_SHOWN_HELP = "TheFogIsland";
	
	private static final String SIZE_SMALL_KEY = "SIZE_SMALL";
	private static final String SIZE_LARGE_KEY = "SIZE_LARGE";
	
	private MapProvider.MapSize mSizeSmall;
	private MapProvider.MapSize mSizeLarge;
	
	private static final Spanned MESSAGE = Html.fromHtml("Choose a size");
	
	public static ExpansionDialogFragment newInstance(MapProvider.MapSize sizeSmall, MapProvider.MapSize sizeLarge) {
		ExpansionDialogFragment f = new ExpansionDialogFragment();
		
		Bundle args = new Bundle();
		args.putString(SIZE_SMALL_KEY, sizeSmall.name());
		args.putString(SIZE_LARGE_KEY, sizeLarge.name());
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context mContext = getActivity().getApplicationContext();
		LayoutInflater inflater =
			(LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout =
			inflater.inflate(R.layout.expansion, (ViewGroup) getActivity().findViewById(R.id.expansion_root));
		
		mSizeSmall = MapProvider.MapSize.valueOf(getArguments().getString(SIZE_SMALL_KEY));
		mSizeLarge = MapProvider.MapSize.valueOf(getArguments().getString(SIZE_LARGE_KEY));

		ImageView smallButton = (ImageView) layout.findViewById(R.id.expansion_small_item);
		smallButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				choice(mSizeSmall);
			}
		});

		ImageView largeButton = (ImageView) layout.findViewById(R.id.expansion_large_item);
		largeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				choice(mSizeLarge);
			}
		});

		AlertDialog ret = new AlertDialog.Builder(getActivity())
			.create();
		ret.setView(layout, 0, 0, 0, 5); // Remove top padding
		ret.getWindow().getAttributes().windowAnimations = R.style.FadeDialogAnimation;
		return ret;
	}
	
	private void choice(MapProvider.MapSize size) {
		final MainActivity mainActivity = (MainActivity) getActivity();
		final MapFragment mapFragment = mainActivity.getMapFragment();
		
		switch (size) {
		case HEADING_FOR_NEW_SHORES:
			mapFragment.headingForNewShoresChoice();
			break;
		case HEADING_FOR_NEW_SHORES_EXP:
			mapFragment.headingForNewShoresExpChoice();
			break;
		default:
			dismiss();	
		}

		mainActivity.getSupportFragmentManager().popBackStack();
		mainActivity.getSupportFragmentManager().popBackStack();
		mainActivity.getSupportFragmentManager().popBackStack();
	}
	
	private void maybeShowFogIslandExplanation() {
		SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		boolean shownWhatsNew = prefs.getBoolean(SHARED_PREFS_SHOWN_HELP, false);
		if (!shownWhatsNew) {
			FogIslandHelpDialogFragment.newInstance().show(getFragmentManager(), "TheFogIslandHelpDialog");
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.putBoolean(SHARED_PREFS_SHOWN_HELP, true);
			prefsEditor.commit();
		}
	}
}
