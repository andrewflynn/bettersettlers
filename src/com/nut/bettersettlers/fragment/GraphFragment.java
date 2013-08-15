package com.nut.bettersettlers.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.dialog.GraphHelpDialogFragment;
import com.nut.bettersettlers.fragment.dialog.ResetDialogFragment;
import com.nut.bettersettlers.fragment.dialog.WelcomeDialogFragment;
import com.nut.bettersettlers.ui.GraphView;

public class GraphFragment extends Fragment {
	private static final String X = "BetterSettlers";

	private static final String STATE_PROBS = "STATE_PROBS";
	private static final String STATE_ROBBER_PROBS = "STATE_ROBBER_PROBS";
	private static final String STATE_PROBS_STACK = "STATE_PROBS_STACK";

	private int dRollButtonPadding;

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
	private ArrayList<Integer> mProbsList = new ArrayList<Integer>();

	///////////////////////////////
	// Fragment method overrides //
	///////////////////////////////
	/** Called when the activity is first created. */
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
			if (savedState.getIntArray(STATE_PROBS) != null) {
				mProbs = savedState.getIntArray(STATE_PROBS);
			}
			if (savedState.getIntArray(STATE_ROBBER_PROBS) != null) {
				mRobberProbs = savedState.getIntArray(STATE_ROBBER_PROBS);
			}
			if (savedState.getSerializable(STATE_PROBS_STACK) != null) {
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
		
		float width = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		float height = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
		if (width > height) {
			initDimens(((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight());
		} else {
			initDimens(((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth());
		}

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
			}
		});
		mDelButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				areYouSureReset();
				return true;
			}
		});
	}
	
	private void setUpButton(ImageView button, final int n) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				incrementGraph(n, false);
			}
		});
		button.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				incrementGraph(n, true);
				return true;
			}
		});
	}
	
	private void initDimens(int size) {
		int padding = (int) getResources().getDimension(R.dimen.graph_button_padding);
		dRollButtonPadding = (int) getResources().getDimension(R.dimen.graph_button_buffer);
		int width = size - (2 * padding) - (5 * dRollButtonPadding);
	}
	
	private void increment(View v) {
		switch (v.getId()) {
		case R.id.two_button:
			incrementGraph(2, false);
			break;
		case R.id.three_button:
			incrementGraph(3, false);
			break;
		case R.id.four_button:
			incrementGraph(4, false);
			break;
		case R.id.five_button:
			incrementGraph(5, false);
			break;
		case R.id.six_button:
			incrementGraph(6, false);
			break;
		case R.id.seven_button:
			incrementGraph(7, false);
			break;
		case R.id.eight_button:
			incrementGraph(8, false);
			break;
		case R.id.nine_button:
			incrementGraph(9, false);
			break;
		case R.id.ten_button:
			incrementGraph(10, false);
			break;
		case R.id.eleven_button:
			incrementGraph(11, false);
			break;
		case R.id.twelve_button:
			incrementGraph(12, false);
			break;
		}
	}
	
	private void longIncrement(View v) {
		switch (v.getId()) {
		case R.id.two_button:
			incrementGraph(2, true);
			break;
		case R.id.three_button:
			incrementGraph(3, true);
			break;
		case R.id.four_button:
			incrementGraph(4, true);
			break;
		case R.id.five_button:
			incrementGraph(5, true);
			break;
		case R.id.six_button:
			incrementGraph(6, true);
			break;
		case R.id.seven_button:
			incrementGraph(7, true);
			break;
		case R.id.eight_button:
			incrementGraph(8, true);
			break;
		case R.id.nine_button:
			incrementGraph(9, true);
			break;
		case R.id.ten_button:
			incrementGraph(10, true);
			break;
		case R.id.eleven_button:
			incrementGraph(11, true);
			break;
		case R.id.twelve_button:
			incrementGraph(12, true);
			break;
		}
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
	
	private void areYouSureReset() {
		ResetDialogFragment.newInstance().show(getFragmentManager(), "ResetFragmentDialog");
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
