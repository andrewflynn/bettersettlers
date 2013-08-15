package com.nut.bettersettlers.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.fragment.dialog.AboutDialogFragment;
import com.nut.bettersettlers.fragment.dialog.HelpDialogFragment;
import com.nut.bettersettlers.fragment.dialog.RateDialogFragment;
import com.nut.bettersettlers.fragment.dialog.ResetDialogFragment;
import com.nut.bettersettlers.fragment.dialog.RulesDialogFragment;
import com.nut.bettersettlers.misc.Consts;
import com.nut.bettersettlers.ui.GraphView;

public class GraphFragment extends Fragment {
	private static final String X = "BetterSettlers";

	private static final String STATE_PROBS = "STATE_PROBS";
	private static final String STATE_ROBBER_PROBS = "STATE_ROBBER_PROBS";
	private static final String STATE_PROBS_STACK = "STATE_PROBS_STACK";

	private int dRollButtonSize;
	private int dRollButtonPadding;

	private GraphView mGraphView;

	private int[] mProbs;
	private int[] mRobberProbs;
	// Negative numbers will represent robber values in this stack
	private ArrayList<Integer> mProbsList = new ArrayList<Integer>();
	
	private int mMapItemId = -1;

	///////////////////////////////
	// Fragment method overrides //
	///////////////////////////////
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		
		setHasOptionsMenu(true);

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
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		float width = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		float height = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
		if (width > height) {
			initLandscape();
		} else {
			initPortrait();
		}

		setAndInvalidate();
	}

	private void initPortrait() {
		initDimens(((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth());
		initRollButtons(2, 6);
	}

	private void initLandscape() {
		initDimens(((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight());
		initRollButtons(4, 3);
	}

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putIntArray(STATE_PROBS, mProbs);
		outState.putIntArray(STATE_ROBBER_PROBS, mRobberProbs);
		outState.putIntegerArrayList(STATE_PROBS_STACK, mProbsList);
	}
	
	private void initDimens(int size) {
		int padding = (int) getResources().getDimension(R.dimen.graph_button_padding);
		dRollButtonPadding = (int) getResources().getDimension(R.dimen.graph_button_buffer);
		int width = size - (2 * padding) - (5 * dRollButtonPadding);
		dRollButtonSize = width / 6;
	}

	private void initRollButtons(int rows, int buttonsPerRow) {
		TableLayout buttons = (TableLayout) getView().findViewById(R.id.roll_buttons);
		
		// For some reason, sometimes we init twice
		if (buttons.getChildCount() > 0) {
			return;
		}
		
		int counter = 2;
		for (int i = 0; i < rows; i++) {
			TableRow row = new TableRow(getActivity());
			row.setGravity(Gravity.CENTER);
			for (int j = 0; j < buttonsPerRow; j++) {
				if (counter == 13) {
					break;
				}
				final int finalCounter = counter;
				Button button = (Button) getActivity().getLayoutInflater().inflate(R.layout.graph_button, null);
				button.setText(Integer.toString(finalCounter));
				button.setWidth(dRollButtonSize);
				button.setHeight(dRollButtonSize);
				button.setPadding(dRollButtonPadding, dRollButtonPadding, dRollButtonPadding, dRollButtonPadding);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						incrementGraph(finalCounter, false);
					}
				});
				button.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						incrementGraph(finalCounter, true);
						return true;
					}
				});
				row.addView(button);
				counter++;
			}
			if (counter == 13) {
				// Also add a backspace button
				Button button = (Button) getActivity().getLayoutInflater().inflate(R.layout.graph_button, null);
				button.setText("DEL");
				button.setWidth(dRollButtonSize);
				button.setHeight(dRollButtonSize);
				button.setPadding(dRollButtonPadding, dRollButtonPadding, dRollButtonPadding, dRollButtonPadding);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						undo();
					}
				});
				button.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						areYouSureReset();
						return true;
					}
				});
				row.addView(button);
			}
			buttons.addView(row);
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

	////////////////////
	// Menu functions //
	////////////////////
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.graph_hc, menu);
		if (Consts.AT_LEAST_HONEYCOMB) {
			// Set up action items for Action Bar
			menu.findItem(R.id.help_item).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		item.setChecked(true);
		
		if (item.getItemId() == mMapItemId) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.hide(getFragmentManager().findFragmentById(R.id.graph_fragment));
			ft.show(getFragmentManager().findFragmentById(R.id.map_fragment));
			ft.commit();
			return true;
		}
		
		switch (item.getItemId()) {
		case R.id.help_item:
			HelpDialogFragment.newInstance().show(getFragmentManager(), "HelpDialogFragment");
			return true;
		case R.id.rules_item:
			RulesDialogFragment.newInstance().show(getFragmentManager(), "RulesDialogFragment");
			return true;
		case R.id.about_item:
			AboutDialogFragment.newInstance().show(getFragmentManager(), "AboutDialogFragment");
			return true;
		case R.id.rate_item:
			RateDialogFragment.newInstance().show(getFragmentManager(), "RateDialogFragment");
			return true;
		default:
			return false;
		}
	}
}
