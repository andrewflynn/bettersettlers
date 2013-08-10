package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.MapFragment;

public class MapSizeDialogFragment extends DialogFragment {
	private static final String[] SIZES = {"Standard (3-4 people)", "Large (5 people)", "X-Large (6 people)"};
	private static final String SELECTED_KEY = "selected";
	
	public static MapSizeDialogFragment newInstance(int selected) {
		MapSizeDialogFragment f = new MapSizeDialogFragment();
		
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
			.setTitle("Map Size")
			.setSingleChoiceItems(SIZES, selected, new MapSizeDialogClickListener())
			.create();
	}
	
	private class MapSizeDialogClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			
			MapFragment mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
			switch (which) {
			case 0:
				mapFragment.standardChoice();
				break;
			case 1:
				mapFragment.largeChoice();
				break;
			case 2:
				mapFragment.xlargeChoice();
				break;
			case 3:
				mapFragment.europeChoice();
				break;
			default:
				break;
			}
		}
	}
}
