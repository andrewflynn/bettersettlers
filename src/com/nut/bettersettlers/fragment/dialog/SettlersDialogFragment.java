package com.nut.bettersettlers.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.MapFragment;

public class SettlersDialogFragment extends DialogFragment {
	public static SettlersDialogFragment newInstance() {
		return new SettlersDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context mContext = getActivity().getApplicationContext();
		LayoutInflater inflater =
			(LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout =
			inflater.inflate(R.layout.settlers, (ViewGroup) getActivity().findViewById(R.id.settlers_root));
		
		ImageView standardButton = (ImageView) layout.findViewById(R.id.standard_item);
		standardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MapFragment mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
				mapFragment.standardChoice();
				getActivity().getSupportFragmentManager().popBackStack();
				getActivity().getSupportFragmentManager().popBackStack();
				
			}
		});
		ImageView largeButton = (ImageView) layout.findViewById(R.id.large_item);
		largeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MapFragment mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
				mapFragment.largeChoice();
				getActivity().getSupportFragmentManager().popBackStack();
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
		ImageView xlargeButton = (ImageView) layout.findViewById(R.id.xlarge_item);
		xlargeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MapFragment mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
				mapFragment.xlargeChoice();
				getActivity().getSupportFragmentManager().popBackStack();
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
		
		AlertDialog ret = new AlertDialog.Builder(getActivity())
			.create();
		ret.setView(layout, 0, 0, 0, 5); // Remove top padding
		ret.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
		return ret;
	}
}
