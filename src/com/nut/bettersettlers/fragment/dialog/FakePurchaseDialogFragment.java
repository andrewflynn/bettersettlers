package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.iab.IabConsts;
import com.nut.bettersettlers.iab.Obfuscate;
import com.nut.bettersettlers.iab.util.Base64;
import com.nut.bettersettlers.misc.Consts;

public class FakePurchaseDialogFragment extends DialogFragment {
	private static final String SHARED_PREFS_THE_FOG_ISLAND_NAME = "Seafarers";
	private static final String SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP = "TheFogIsland";
	private static final String ITEM_ID_KEY = "ITEM_ID";
	
	private String itemId;
	
	private static final Spanned MESSAGE = Html.fromHtml("");
	
	public static FakePurchaseDialogFragment newInstance(String itemId) {
		FakePurchaseDialogFragment f = new FakePurchaseDialogFragment();
		
		Bundle args = new Bundle();
		args.putString(ITEM_ID_KEY, itemId);
		f.setArguments(args);
		
		return f;
	}
			
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		itemId = getArguments().getString(ITEM_ID_KEY);
		
		final MainActivity mainActivity = (MainActivity) getActivity();
		final MapFragment mapFragment = mainActivity.getMapFragment();
		
		TextView textView = new TextView(mainActivity, null, android.R.attr.textAppearanceSmallInverse);
		textView.setTextColor(Color.WHITE);
		textView.setPadding(10, 10, 10, 10);
		textView.setText(MESSAGE);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		return new AlertDialog.Builder(mainActivity)
			.setIcon(R.drawable.icon)
			.setTitle("Purchase " + itemId)
			.setView(textView)
			.setPositiveButton("Buy", new OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String secureId = Secure.getString(mainActivity.getContentResolver(), Secure.ANDROID_ID);

					SharedPreferences prefs = mainActivity.getSharedPreferences(Consts.SHARED_PREFS_SEAFARERS_KEY, Context.MODE_PRIVATE);
					SharedPreferences.Editor prefsEditor = prefs.edit();
					prefsEditor.putString(Base64.encode(itemId.getBytes()), Obfuscate.encode(secureId, itemId));
					prefsEditor.commit();
					
					mainActivity.addOwnedMap(itemId);
					if (mapFragment != null && !itemId.equals(IabConsts.BUY_ALL)) {
						mapFragment.sizeChoice(mainActivity.getMapProvider().getMapSizeByProductId(itemId));
					}
					
					if ("seafarers.the_fog_island".equals(itemId)) {
						SharedPreferences newPrefs = mainActivity.getSharedPreferences(SHARED_PREFS_THE_FOG_ISLAND_NAME, Context.MODE_PRIVATE);
						boolean shownWhatsNew = newPrefs.getBoolean(SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP, false);
						if (!shownWhatsNew) {
							FogIslandHelpDialogFragment.newInstance().show(getFragmentManager(), "TheFogIslandHelpDialog");
							SharedPreferences.Editor newPrefsEditor = newPrefs.edit();
							newPrefsEditor.putBoolean(SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP, true);
							newPrefsEditor.commit();
						}
					}
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
