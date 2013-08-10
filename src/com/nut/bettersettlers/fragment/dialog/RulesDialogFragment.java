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

public class RulesDialogFragment extends DialogFragment {
	private static final Spanned MESSAGE = Html.fromHtml("<b>3 different sizes</b><br/>"
			+ "<i>Standard:</i> 3-4 players<br/>"
			+ "<i>Large:</i> 5 players<br/>"
			+ "<i>X-Large:</i> 6 players<br/><br/>"
			+ "<b>3 different types</b><br/>"
			+ "<i>Better Settlers:</i> A map that creates a balanced distribution of resources and probabilities in order to maximize "
				+ "fairness and create a more evenly challenging game.<br/>"
			+ "<i>Traditional:</i>  A map generated in accordance with the method outlined in the Settlers of Catan rulebook.<br/>"
			+ "<i>Random:</i> Completely. No rules.<br/><br/>"
			+ "<b>3 different ways to generate</b><br/>"
			+ "<i>Shuffle Map:</i> Refreshes the entire map.<br/>"
			+ "<i>Shuffle Probabilities:</i> Shuffles only the probabilities. Try this one for quicker setup on a second game.<br/>"
			+ "<i>Shuffle Harbors:</i> Shuffles only the harbors.");
	
	public static RulesDialogFragment newInstance() {
		return new RulesDialogFragment();
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
			.setTitle("How it works")
			.setView(textView)
			.setNegativeButton("Dismiss", new OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			})
			.create();
	}
}
