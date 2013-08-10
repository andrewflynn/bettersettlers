package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.nut.bettersettlers.R;

public class LegalDialogFragment extends DialogFragment {
	private static final Spanned MESSAGE = Html.fromHtml("Copyright 2011 Better Settlers. All Rights Reserved.<br/><br/>"
			+ "This app is in no way affiliated with Mayfair Games or Klaus Teuber, of whom Settlers of Catan is a registered trademark.<br/><br/>"
			+ "This app uses Google Analytics, which uses anonymous tracking data in order to provide you with a better user experience. "
			+ "More info can be found by reading the <a href='http://www.google.com/analytics/tos.html'>Google Analytics Terms of Service</a>.");
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity(), null, android.R.attr.textAppearanceMedium);
		textView.setText(MESSAGE);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		return new AlertDialog.Builder(getActivity())
		.setIcon(R.drawable.icon)
		.setTitle("Better Settlers Board Generator")
		.setView(textView)
		.setNegativeButton("Dismiss", new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		})
		.create();
	}
}
