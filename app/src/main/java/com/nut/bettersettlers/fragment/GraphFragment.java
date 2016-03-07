package com.nut.bettersettlers.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.dialog.ResetDialogFragment;
import com.nut.bettersettlers.ui.GraphView;
import com.nut.bettersettlers.util.Analytics;

import java.util.ArrayList;

public class GraphFragment extends Fragment {
	private static final String STATE_PROBS = "STATE_PROBS";
	private static final String STATE_ROBBER_PROBS = "STATE_ROBBER_PROBS";
	private static final String STATE_PROBS_STACK = "STATE_PROBS_STACK";

	private GraphView mGraphView;

	private ImageView mTwoButton;
	private ImageView mThreeButton;
	private ImageView mFourButton;
	private ImageView mFiveButton;
	private ImageView mSixButton;
	private ImageView mSevenButton;
	private ImageView mEightButton;
	private ImageView mNineButton;
	private ImageView mTenButton;
	private ImageView mElevenButton;
	private ImageView mTwelveButton;
	private ImageView mDelButton;

	private int[] mProbs;
	private int[] mRobberProbs;
	// Negative numbers will represent robber values in this stack
	private ArrayList<Integer> mProbsList = new ArrayList<>();

	///////////////////////////////
	// Fragment method overrides //
	///////////////////////////////
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);

		mProbs = new int[13];
		mRobberProbs = new int[13];
		for (int i = 0; i <= 1; i++) {
			// There are no 0 or 1 dice rolls
			mProbs[i] = -1;
			mRobberProbs[i] = -1;
		}
		for (int i = 2; i <= 12; i++) {
			// All others start with 0 rolls
			mProbs[i] = 0;
			mRobberProbs[i] = 0;
		}
		
		if (savedState != null) {
			if (savedState.containsKey(STATE_PROBS)) {
				mProbs = savedState.getIntArray(STATE_PROBS);
			}
			if (savedState.containsKey(STATE_ROBBER_PROBS)) {
				mRobberProbs = savedState.getIntArray(STATE_ROBBER_PROBS);
			}
			if (savedState.containsKey(STATE_PROBS_STACK)) {
				mProbsList = savedState.getIntegerArrayList(STATE_PROBS_STACK);
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.graph, container, false);
		mGraphView = (GraphView) view.findViewById(R.id.graph_view);

		mTwoButton = (ImageView) view.findViewById(R.id.two_button);
		mThreeButton = (ImageView) view.findViewById(R.id.three_button);
		mFourButton = (ImageView) view.findViewById(R.id.four_button);
		mFiveButton = (ImageView) view.findViewById(R.id.five_button);
		mSixButton = (ImageView) view.findViewById(R.id.six_button);
		mSevenButton = (ImageView) view.findViewById(R.id.seven_button);
		mEightButton = (ImageView) view.findViewById(R.id.eight_button);
		mNineButton = (ImageView) view.findViewById(R.id.nine_button);
		mTenButton = (ImageView) view.findViewById(R.id.ten_button);
		mElevenButton = (ImageView) view.findViewById(R.id.eleven_button);
		mTwelveButton = (ImageView) view.findViewById(R.id.twelve_button);
		mDelButton = (ImageView) view.findViewById(R.id.del_button);
		
		setUpButtons();
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();

		setAndInvalidate();
	}

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putIntArray(STATE_PROBS, mProbs);
		outState.putIntArray(STATE_ROBBER_PROBS, mRobberProbs);
		outState.putIntegerArrayList(STATE_PROBS_STACK, mProbsList);
	}
	
	private void setUpButtons() {
		setUpButton(mTwoButton, 2);
		setUpButton(mThreeButton, 3);
		setUpButton(mFourButton, 4);
		setUpButton(mFiveButton, 5);
		setUpButton(mSixButton, 6);
		setUpButton(mSevenButton, 7);
		setUpButton(mEightButton, 8);
		setUpButton(mNineButton, 9);
		setUpButton(mTenButton, 10);
		setUpButton(mElevenButton, 11);
		setUpButton(mTwelveButton, 12);

		mDelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				undo();
                Analytics.track(getActivity(), Analytics.CATEGORY_ROLL_TRACKER,
                        Analytics.ACTION_BUTTON, Analytics.DELETE);
			}
		});
		mDelButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ResetDialogFragment.newInstance().show(getFragmentManager(), "ResetFragmentDialog");
				return true;
			}
		});
	}
	
	private void setUpButton(ImageView button, final int n) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				incrementGraph(n, false);
                Analytics.track(getActivity(), Analytics.CATEGORY_ROLL_TRACKER,
                        Analytics.ACTION_BUTTON, "" + n);
			}
		});
		button.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				incrementGraph(n, true);
                Analytics.track(getActivity(), Analytics.CATEGORY_ROLL_TRACKER,
                        Analytics.ACTION_LONG_PRESS_BUTTON, "" + n);
				
				return true;
			}
		});
	}

	private void incrementGraph(int n, boolean longClick) {
		if (longClick) {
			mRobberProbs[n]++;
			mProbsList.add(n * -1);
		} else {
			mProbs[n]++;
			mProbsList.add(n);
		}
		setAndInvalidate();
	}

	private void undo() {
		if (mProbsList.size() > 0) {
			int popped = mProbsList.remove(mProbsList.size() - 1);
			if (popped > 0) {
				if (mProbs[popped] > 0) {
					mProbs[popped]--;
				}
			} else { // Negative
				popped *= -1;
				if (mRobberProbs[popped] > 0) {
					mRobberProbs[popped]--;
				}
			}
		}
		setAndInvalidate();
	}

	public void reset() {
		for (int i = 0; i < mProbs.length; i++) {
			if (mProbs[i] != -1) {
				mProbs[i] = 0;
			}
		}
		for (int i = 0; i < mRobberProbs.length; i++) {
			if (mRobberProbs[i] != -1) {
				mRobberProbs[i] = 0;
			}
		}
		mProbsList.clear();
		setAndInvalidate();
	}

	private void setAndInvalidate() {
		mGraphView.setProbs(mProbs);
		mGraphView.setRobberProbs(mRobberProbs);
		mGraphView.invalidate(); // Force refresh
	}
}
