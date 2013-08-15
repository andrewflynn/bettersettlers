package com.nut.bettersettlers.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.fragment.MapFragment;

public class MapSizeWithSeafarersDialogFragment extends DialogFragment {
	private static final String[] SIZES = { "Standard (3-4 people)", "Large (5 people)", "X-Large (6 people)",
		"Seafarers" };
	private static final String SELECTED_KEY = "selected";
	
	public static MapSizeWithSeafarersDialogFragment newInstance(int selected) {
		MapSizeWithSeafarersDialogFragment f = new MapSizeWithSeafarersDialogFragment();
		
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
			
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
			MainActivity mainActivity = (MainActivity) getActivity();
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
				SeafarersDialogFragment.newInstance(mainActivity.getOwnedMaps()).show(fragmentManager, "SeafarersDialogFragment");
			default:
				break;
			}
		}
	}
}
