package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.MapFragment;

public class MapMoreDialogFragment extends DialogFragment {
	private static final String[] CHOICES = { "Shuffle Probabilities", "Shuffle Harbors", "About" };
	
	public static MapMoreDialogFragment newInstance() {
		return new MapMoreDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("Better Settlers")
			.setItems(CHOICES, new MoreDialogClickListener())
			.create();
	}
	
	private class MoreDialogClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			
			MapFragment mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
			switch (which) {
			case 0:
				mapFragment.asyncProbsShuffle();
				break;
			case 1:
				mapFragment.asyncHarborsShuffle();
				break;
			case 2:
				AboutDialogFragment.newInstance().show(getFragmentManager(), "AboutDialogFragment");
				break;
			default:
				break;
			}
		}
	}
}