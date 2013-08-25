package com.nut.bettersettlers.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.data.MapSize;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.util.Analytics;

public class SettlersDialogFragment extends DialogFragment {
	public static SettlersDialogFragment newInstance() {
		SettlersDialogFragment f = new SettlersDialogFragment();
		f.setStyle(STYLE_NO_TITLE, 0);
		return f;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		((MainActivity) getActivity()).trackView(Analytics.VIEW_SETTLERS_MENU);
	}
	
	private void setupButton(ImageView button, final MapSize mapSize) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).trackEvent(Analytics.CATEGORY_SETTLERS_MENU,
						Analytics.ACTION_BUTTON, mapSize.title);
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
				mapFragment.sizeChoice(mapSize);
				fm.popBackStack();
				fm.popBackStack();
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settlers, container, false);

		setupButton((ImageView) view.findViewById(R.id.standard_item), MapSize.STANDARD);
		setupButton((ImageView) view.findViewById(R.id.large_item), MapSize.LARGE);
		setupButton((ImageView) view.findViewById(R.id.xlarge_item), MapSize.XLARGE);
		
		getDialog().getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
		
		return view;
	}
}
