package com.nut.bettersettlers.activity;

import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.ui.GraphView;

public class GraphActivity extends Activity {
	private static final String X = GraphActivity.class.getSimpleName();
	
	private static final String SHARED_PREFS_NAME = "GraphActivity";
	private static final String SHARED_PREFS_SHOWN_HELP = "ShownHelp";

	private static final String STATE_PROBS = "STATE_PROBS";
	private static final String STATE_ROBBER_PROBS = "STATE_ROBBER_PROBS";
	private static final String STATE_PROBS_STACK = "STATE_PROBS_STACK";

	private static final int DIALOG_ARE_YOU_SURE_ID = 1;
	private static final int DIALOG_EXPLANATION_ID = 2;
	private static final int DIALOG_ARE_YOU_SURE_RETURN_TO_MAP = 3;

	private PowerManager.WakeLock mWakeLock;

	private int dRollButtonSize;
	private int dRollButtonPadding;

	private GraphView mGraphView;

	private int[] mProbs;
	private int[] mRobberProbs;
	// Negative numbers will represent robber values in this stack
	private Stack<Integer> mProbsStack = new Stack<Integer>();

	///////////////////////////////
	// Activity method overrides //
	///////////////////////////////
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);
		
		mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "GraphActivityWakeLock");

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

		float width = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		float height = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
		if (width > height) {
			initLandscape();
		} else {
			initPortrait();
		}
		mGraphView = (GraphView) findViewById(R.id.graph_view);
		
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		boolean shownHelp = prefs.getBoolean(SHARED_PREFS_SHOWN_HELP, false);
		if (savedInstanceState == null && !shownHelp) {
			showDialog(DIALOG_EXPLANATION_ID);
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.putBoolean(SHARED_PREFS_SHOWN_HELP, true);
			prefsEditor.commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mWakeLock.acquire();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mWakeLock.release();
	}

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putIntArray(STATE_PROBS, mProbs);
		outState.putIntArray(STATE_ROBBER_PROBS, mRobberProbs);
		outState.putSerializable(STATE_PROBS_STACK, mProbsStack);
	}

	/** Called when the activity is going to appear. */
	@Override
	@SuppressWarnings("unchecked")
	public void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		if (savedState.getIntArray(STATE_PROBS) != null) {
			mProbs = savedState.getIntArray(STATE_PROBS);
		}
		if (savedState.getIntArray(STATE_ROBBER_PROBS) != null) {
			mRobberProbs = savedState.getIntArray(STATE_ROBBER_PROBS);
		}
		if (savedState.getSerializable(STATE_PROBS_STACK) != null) {
			mProbsStack = (Stack<Integer>) savedState.getSerializable(STATE_PROBS_STACK);
		}
		setAndInvalidate();
	}

	private void initPortrait() {
		initDimens(((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth());
		initRollButtons(2, 6);
	}

	private void initLandscape() {
		initDimens(((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight());
		initRollButtons(4, 3);
	}
	
	private void initDimens(int size) {
		int padding = (int) getResources().getDimension(R.dimen.graph_button_padding);
		dRollButtonPadding = (int) getResources().getDimension(R.dimen.graph_button_buffer);
		int width = size - (2 * padding) - (5 * dRollButtonPadding);
		dRollButtonSize = width / 6;
	}

	private void initRollButtons(int rows, int buttonsPerRow) {
		TableLayout buttons = (TableLayout) findViewById(R.id.roll_buttons);
		int counter = 2;
		for (int i = 0; i < rows; i++) {
			TableRow row = new TableRow(this);
			row.setGravity(Gravity.CENTER);
			for (int j = 0; j < buttonsPerRow; j++) {
				if (counter == 13) {
					break;
				}
				final int finalCounter = counter;
				Button button = new Button(this);
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
				Button button = new Button(this);
				button.setText("X");
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
			mProbsStack.push(n * -1);
		} else {
			mProbs[n]++;
			mProbsStack.push(n);
		}
		setAndInvalidate();
	}

	private void undo() {
		if (mProbsStack.size() > 0) {
			int popped = mProbsStack.pop();
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
		showDialog(DIALOG_ARE_YOU_SURE_ID);
	}

	private void reset() {
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
		mProbsStack.clear();
		setAndInvalidate();
	}

	private void setAndInvalidate() {
		mGraphView.setProbs(mProbs);
		mGraphView.setRobberProbs(mRobberProbs);
		mGraphView.invalidate(); // Force refresh
	}

	////////////////////
	// Dialog methods //
	////////////////////
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_ARE_YOU_SURE_ID:
			dialog = createAreYouSureDialog();
			break;
		case DIALOG_ARE_YOU_SURE_RETURN_TO_MAP:
			dialog = createAreYouSureReturnToMapDialog();
			break;
		case DIALOG_EXPLANATION_ID:
			dialog = createExplanationDialog();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	private Dialog createAreYouSureDialog() {
		return new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon)
			.setTitle("Reset the Board")
			.setMessage("Are you sure you'd like to reset the board? This action cannot be undone.")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					reset();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.create();
	}

	private Dialog createAreYouSureReturnToMapDialog() {
		return new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon)
			.setTitle("Return to map")
			.setMessage("Are you sure you'd like to return to the map generator? You will lose your saved rolls.")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.create();
	}
	
	private Dialog createExplanationDialog() {
	    return new AlertDialog.Builder(this)
	    	.setIcon(R.drawable.icon)
	    	.setTitle("Roll Tracker")
	    	.setMessage("Keep track of dice rolls during the game and see the "
	    			+ "probability distribution for each game.\n\nLong press for robbered rolls.")
	    	.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int id) {
	    			dialog.cancel();
	    		}
	    	})
	    	.create();
	}
	
	@Override
	public void onBackPressed() {
		showDialog(DIALOG_ARE_YOU_SURE_RETURN_TO_MAP);
	}

	////////////////////
	// Menu functions //
	////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.graph, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		item.setChecked(true);
		switch (item.getItemId()) {
		// MAP TYPE
		/*
		case R.id.back_to_map_item:
			showDialog(DIALOG_ARE_YOU_SURE_RETURN_TO_MAP);
			return true;
			*/
		case R.id.help_item:
			showDialog(DIALOG_EXPLANATION_ID);
		default:
			return false;
		}
	}
}
