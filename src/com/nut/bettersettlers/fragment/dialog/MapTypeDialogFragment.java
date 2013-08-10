package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.MapFragment;

public class MapTypeDialogFragment extends DialogFragment {
	private static final String[] TYPES = {"Better Settlers", "Traditional", "Random"};
	private static final String SELECTED_KEY = "selected";
	
	public static MapTypeDialogFragment newInstance(int selected) {
		MapTypeDialogFragment f = new MapTypeDialogFragment();
		
		Bundle args = new Bundle();
		args.putInt(SELECTED_KEY, selected);
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int selected = getArguments() == null ? 0 : getArguments().getInt(SELECTED_KEY);
		
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("Map Type")
			.setSingleChoiceItems(TYPES, selected, new MapTypeDialogClickListener())
			.create();
	}
	
	private class MapTypeDialogClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			
			MapFragment mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
			switch (which) {
			case 0:
				mapFragment.betterSettlersChoice();
				break;
			case 1:
				mapFragment.traditionalChoice();
				break;
			case 2:
				mapFragment.randomChoice();
				break;
			default:
				break;
			}
		}
	}
}
