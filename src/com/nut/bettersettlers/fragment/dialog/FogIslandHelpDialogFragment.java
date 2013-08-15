package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nut.bettersettlers.R;

public class FogIslandHelpDialogFragment extends DialogFragment {
	public static FogIslandHelpDialogFragment newInstance() {
		return new FogIslandHelpDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("The Fog Island")
			.setMessage("Tap the gray hexes to see what resources and probabilities are hidden under the fog.")
			.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			}).create();
	}
}