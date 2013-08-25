package com.nut.bettersettlers.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.MapSize;
import com.nut.bettersettlers.fragment.MapFragment;

public class SettlersDialogFragment extends DialogFragment {
	public static SettlersDialogFragment newInstance() {
		return new SettlersDialogFragment();
	}
	
	private void setupButton(ImageView button, final MapSize mapSize) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
				mapFragment.sizeChoice(mapSize);
				fm.popBackStack();
				fm.popBackStack();
			}
		});
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context mContext = getActivity().getApplicationContext();
		LayoutInflater inflater =
			(LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout =
			inflater.inflate(R.layout.settlers, (ViewGroup) getActivity().findViewById(R.id.settlers_root));

		setupButton((ImageView) layout.findViewById(R.id.standard_item), MapSize.STANDARD);
		setupButton((ImageView) layout.findViewById(R.id.large_item), MapSize.LARGE);
		setupButton((ImageView) layout.findViewById(R.id.xlarge_item), MapSize.XLARGE);
		
		AlertDialog ret = new AlertDialog.Builder(getActivity())
			.create();
		ret.setView(layout, 0, 0, 0, 5); // Remove top padding
		ret.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
		return ret;
	}
}
