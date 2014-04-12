package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.nut.bettersettlers.R;

public class NoSeafarersDialogFragment extends DialogFragment {
	private static final Spanned MESSAGE = Html.fromHtml("Sorry, it appears as though your Android device does not " +
			"support Google Play In-App Billing so we cannot offer Seafarers of Catan.<br/>" +
			"Please check that Google Play is installed properly and restart Better Settlers. " +
			"If Seafarers still does not appear, please <a href='mailto:emailus@bettersettlers.com'>let us know</a> and we can help you troubleshoot.");
	
	public static NoSeafarersDialogFragment newInstance() {
		return new NoSeafarersDialogFragment();
	}
			
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity(), null, android.R.attr.textAppearanceSmallInverse);
		textView.setTextColor(Color.WHITE);
		textView.setPadding(10, 10, 10, 10);
		textView.setText(MESSAGE);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("Warning")
			.setView(textView)
			.setNegativeButton("Dismiss", new OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			})
			.create();
	}
}
