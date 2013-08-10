package com.nut.bettersettlers.fragment.dialog;

import com.nut.bettersettlers.R;

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

public class AboutDialogFragment extends DialogFragment {
	private static final Spanned MESSAGE = Html.fromHtml("The Better Settlers Board Generator is for use with the offline board game Settlers of Catan. "
			+ "Not only does it allow for faster game setup, it generates a fair and engaging game.<br/><br/>"
			+ "We love playing Settlers. We've noticed that sometimes the game seems to be over in the first fifteen "
			+ "minutes--and no matter how fairly we try to distribute resources and probabilities during setup, natural "
			+ "bias creeps in. So we developed the Better Settlers Board Generator.<br/><br/>"
			+ "The algorithm for the generator is designed to create fair play. The result is more riveting and engaging play.<br/><br/>"
			+ "You'll have a better game of Settlers.<br/><br/>"
			+ "<a href='mailto:emailus@bettersettlers.com'>Send us feedback</a> on how we can improve Better Settlers!");
			
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
