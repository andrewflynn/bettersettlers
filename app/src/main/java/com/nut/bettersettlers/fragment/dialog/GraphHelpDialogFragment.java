package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nut.bettersettlers.R;

public class GraphHelpDialogFragment extends DialogFragment {
	public static GraphHelpDialogFragment newInstance() {
		return new GraphHelpDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("Roll Tracker")
			.setMessage("Keep track of dice rolls during the game and see the "
					+ "probability distribution for each game.\n\nLong press for robbered rolls.")
			.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			}).create();
	}
}