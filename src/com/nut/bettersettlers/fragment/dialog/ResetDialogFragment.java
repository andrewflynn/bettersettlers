package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.GraphFragment;

public class ResetDialogFragment extends DialogFragment {
	public static ResetDialogFragment newInstance() {
		return new ResetDialogFragment();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("Reset the Board")
			.setMessage("Are you sure you'd like to reset the board? This action cannot be undone.")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					((GraphFragment) getFragmentManager().findFragmentById(R.id.graph_fragment)).reset();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.create();
	}
}