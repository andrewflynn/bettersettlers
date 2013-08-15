package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nut.bettersettlers.R;

public class WelcomeDialogFragment extends DialogFragment {
	public static WelcomeDialogFragment newInstance() {
		return new WelcomeDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("Better Settlers Board Generator")
			.setMessage("Designed to make game setup for Settlers of Catan faster "
				+ "and simpler, the generator creates gameplay that is more evenly "
				+ "challenging and engaging. The result is a better game of Settlers.\n\n"
				+ "What's new in this version:\n"
				+ "- Seafarers maps!\n"
				+ "- Re-designed user interface.\n")
				.setPositiveButton("About", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						(new AboutDialogFragment()).show(getFragmentManager(), "AboutDialogFragment");
					}
				})
				.setNegativeButton("Dismiss", new OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				})
			.create();
	}
}