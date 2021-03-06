package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.util.Analytics;
import com.nut.bettersettlers.util.Consts;

public class AboutDialogFragment extends DialogFragment {
	private static final Spanned MESSAGE = Html.fromHtml("The Better Settlers Board Generator is for use with the offline board game Settlers of Catan. "
			+ "Not only does it allow for faster game setup, it generates a fair and engaging game.<br/><br/>"
			+ "We love playing Settlers. We've noticed that sometimes the game seems to be over in the first fifteen "
			+ "minutes--and no matter how fairly we try to distribute resources and probabilities during setup, natural "
			+ "bias creeps in. So we developed the Better Settlers Board Generator.<br/><br/>"
			+ "The algorithm for the generator is designed to create fair play. The result is more riveting and engaging play.<br/><br/>"
			+ "You'll have a better game of Settlers.<br/><br/>"
			+ "<a href='mailto:emailus@bettersettlers.com'>Send us feedback</a> on how we can improve Better Settlers!<br/><br/>"
			+ "Copyright 2013 Better Settlers. All Rights Reserved.<br/><br/>"
			+ "This app is in no way affiliated with Mayfair Games or Klaus Teuber, of whom Settlers of Catan is a registered trademark.<br/><br/>"
			+ "This app uses Google Analytics, which uses anonymous tracking data in order to provide you with a better user experience. "
			+ "More info can be found by reading the <a href='http://www.google.com/analytics/tos.html'>Google Analytics Terms of Service</a>.");
	
	public static AboutDialogFragment newInstance() {
		return new AboutDialogFragment();
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        Analytics.trackView(getActivity(), Analytics.VIEW_INFO);
	}
			
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity(), null, android.R.attr.textAppearanceSmall);
		textView.setTextSize(getResources().getDimension(R.dimen.about_text_size));
		textView.setPadding(10, 10, 10, 10);
		textView.setText(MESSAGE);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("Better Settlers Board Generator")
			.setView(textView)
			.setPositiveButton("Rate us", new OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Consts.PLAY_STORE_URL)));
                    Analytics.track(getActivity(), Analytics.CATEGORY_ABOUT_MENU,
                            Analytics.ACTION_BUTTON, Analytics.RATE_US);
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
