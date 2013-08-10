package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nut.bettersettlers.R;

public class GraphMoreDialogFragment extends DialogFragment {
	private static final String[] CHOICES = { "About" };
	
	public static GraphMoreDialogFragment newInstance() {
		return new GraphMoreDialogFragment();
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
			
			switch (which) {
			case 0:
				AboutDialogFragment.newInstance().show(getFragmentManager(), "AboutDialogFragment");
				break;
			default:
				break;
			}
		}
	}
}
