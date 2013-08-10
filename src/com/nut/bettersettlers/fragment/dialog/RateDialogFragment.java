package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.nut.bettersettlers.R;

public class RateDialogFragment extends DialogFragment {
	private static final Spanned MESSAGE = Html.fromHtml("Enjoy using Better Settlers? Rate us on Android Market!");
	
	public static RateDialogFragment newInstance() {
		return new RateDialogFragment();
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
			.setView(textView)
			.setPositiveButton("Sure thing!", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
							Uri.parse("http://market.android.com/details?id=com.nut.bettersettlers")));
				}
			})
			.setNegativeButton("No thanks", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			})
			.create();
	}
}
