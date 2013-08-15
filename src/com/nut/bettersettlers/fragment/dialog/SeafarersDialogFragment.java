package com.nut.bettersettlers.fragment.dialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.iab.IabConsts.MapContainer;
import com.nut.bettersettlers.misc.Consts;

public class SeafarersDialogFragment extends DialogFragment {
	private static final String UNLOCKED_KEY = "unlocked";
	
	private Set<String> mUnlocked = new HashSet<String>();;
	
	public static SeafarersDialogFragment newInstance(Set<String> unlocked) {
		SeafarersDialogFragment f = new SeafarersDialogFragment();
		
		ArrayList<String> unlockedArray = new ArrayList<String>();
		for (String str : unlocked) {
			unlockedArray.add(str);
		}
		
		Bundle args = new Bundle();
		args.putStringArrayList(UNLOCKED_KEY, unlockedArray);
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getArguments() != null) {
			mUnlocked.addAll(getArguments().getStringArrayList(UNLOCKED_KEY));
		}
		
		SeafarersAdapter adapter = new SeafarersAdapter(getActivity(), R.layout.seafarers_item);
		for (MapContainer mc : MapContainer.values()) {
			adapter.add(mc);
		}
		
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.icon)
			.setTitle("Seafarers")
			.setSingleChoiceItems(adapter, 4, new MapSizeDialogClickListener())
			.create();
	}
	
	private class SeafarersAdapter extends ArrayAdapter<MapContainer> {
		public SeafarersAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.seafarers_item, null);
			}
			MapContainer mapContainer = getItem(position);
			if (mapContainer != null) {
				CheckedTextView titleTextView = (CheckedTextView) convertView.findViewById(R.id.seafarers_item_title);
				titleTextView.setText(mapContainer.name);
				if (!mUnlocked.contains(mapContainer.id)) {
					titleTextView.setCheckMarkDrawable(R.drawable.lock);
				} else {
					titleTextView.setCheckMarkDrawable(android.R.drawable.btn_radio);
				}
			}
			return convertView;
		}
	}
	
	private class MapSizeDialogClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			
			MainActivity mainActivity = (MainActivity) getActivity();
			MapFragment mapFragment = (MapFragment) mainActivity.getSupportFragmentManager().findFragmentById(R.id.map_fragment);
			MapContainer mapContainer = MapContainer.values()[which];

			// If we need to buy it, buy it
			//if (!mUnlocked.contains(mapContainer.id)) {
			//	mainActivity.purchaseItem(mapContainer.productId, mapContainer.id);
			//	return;
			//}
			
			// Else just show it
			switch (which) {
			case 0:
				mapFragment.headingForNewShoresChoice();
				break;
			default:
				break;
			}
		}
	}
}
