package com.nut.bettersettlers;

import static com.nut.bettersettlers.MapSpecs.BOARD_RANGE_X;
import static com.nut.bettersettlers.MapSpecs.BOARD_RANGE_Y;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bettersettlers.R;
import com.nut.bettersettlers.MapSpecs.Harbor;
import com.nut.bettersettlers.MapSpecs.MapSize;
import com.nut.bettersettlers.MapSpecs.MapType;
import com.nut.bettersettlers.MapSpecs.Resource;

public class BetterSettlers extends Activity {	
	private static final int DIALOG_SIZE_ID = 0;
	private static final int DIALOG_TYPE_ID = 1;
	private static final int DIALOG_WELCOME_ID = 2;
	private static final int DIALOG_ARE_YOU_SURE_ID = 3;
	private static final int DIALOG_GRAPH_ID = 4;
	
	// TODO(flynn): Change the to use strings.xml
	private static final String[] types = {"Better Settlers", "Traditional", "Random"};
	private static final String[] sizes = {"Standard (3-4 people)", "Large (5 people)", "X-Large (6 people)"};
  private static int shuffleTypeId = R.drawable.shuffle_map;
	
	//private ProgressBar progressBar;
	private MapView mapView;
	private GraphView graphView;
	private TextView lastRollsText;
	
	private ImageView graphButton2;
	private ImageView graphButton3;
	private ImageView graphButton4;
	private ImageView graphButton5;
	private ImageView graphButton6;
	private ImageView graphButton7;
	private ImageView graphButton8;
	private ImageView graphButton9;
	private ImageView graphButton10;
	private ImageView graphButton11;
	private ImageView graphButton12;
	
	private boolean shownGraphAlertAlready = false;
  
  private Handler handler = new Handler() {
  	@Override
  	public void handleMessage(Message msg) {
  		refreshView();
  		stopThinking();
  	}
  };

	// The current type of map that needs to be generated/displayed.  Defaults to standard
	protected MapSize currentMap = MapSize.STANDARD;
	protected MapType currentType = MapType.FAIR;
	
	private ArrayList<Resource> orderedResources = new ArrayList<Resource>();
	private ArrayList<Integer> orderedProbs = new ArrayList<Integer>();
	private ArrayList<Harbor> harbors = new ArrayList<Harbor>();
	private LinkedHashMap<Integer, List<String>> placements = new LinkedHashMap<Integer, List<String>>();
	private ArrayList<Integer> orderedPlacements = new ArrayList<Integer>();
	//private int placementIndex = -1;
	private int placementBookmark = -1;

	private Resource[][] resourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private int[][] probabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private Harbor[][] harborBoard = new Harbor[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	
	// Negative numbers will represent robber values in this stack
	private Stack<Integer> graphStack = new Stack<Integer>();
	private int[] graphProbs = new int[] {
			-1 /* 0 */,
      -1 /* 1 */,
      0  /* 2 */,
      0  /* 3 */,
      0  /* 4 */,
      0  /* 5 */,
      0  /* 6 */,
      0  /* 7 */,
      0  /* 8 */,
      0  /* 9 */,
      0  /* 10 */,
      0  /* 11 */,
      0  /* 12 */};
	private int[] graphRobberProbs = new int[] {
			-1 /* 0 */,
      -1 /* 1 */,
      0  /* 2 */,
      0  /* 3 */,
      0  /* 4 */,
      0  /* 5 */,
      0  /* 6 */,
      0  /* 7 */,
      0  /* 8 */,
      0  /* 9 */,
      0  /* 10 */,
      0  /* 11 */,
      0  /* 12 */};
	
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    
    setContentView(R.layout.main);
  	mapView = (MapView) findViewById(R.id.map_view);
  	graphView = (GraphView) findViewById(R.id.graph_view);

		resetProgressBar();
    
  	// First time around
  	if (savedState == null) {
    	welcome();
      refreshMap();
  	}
  }
  
  /** Called when the activity is going to disappear. */
  @Override
  public void onSaveInstanceState(Bundle outState) {
  	outState.putString("mapSize", currentMap.name());
    outState.putString("mapType", currentType.name());
    outState.putSerializable("resources", orderedResources);
    outState.putSerializable("probs", orderedProbs);
    outState.putSerializable("harbors", harbors);
    outState.putSerializable("placements", placements);
    outState.putSerializable("orderedPlacements", orderedPlacements);
    outState.putSerializable("placementBookmark", placementBookmark);
  	super.onSaveInstanceState(outState);
  }
  
  /** Called when the activity is going to disappear. */
  @Override
  public void onRestoreInstanceState(Bundle savedState) {
  	super.onRestoreInstanceState(savedState);
  	if (savedState.getString("mapSize") != null) {
    	currentMap = MapSize.valueOf(savedState.getString("mapSize"));
  	} else {
  		currentMap = MapSize.STANDARD;
  	}
  	
  	if (savedState.getString("mapType") != null) {
    	currentType = MapType.valueOf(savedState.getString("mapType"));
  	} else {
  		currentType = MapType.TRADITIONAL;
  	}
  	
  	if (savedState.getSerializable("resources") != null
  			&& savedState.getSerializable("probs") != null
  			&& savedState.getSerializable("harbors") != null) {
  		orderedResources = (ArrayList<Resource>) savedState.getSerializable("resources");
  		orderedProbs = (ArrayList<Integer>) savedState.getSerializable("probs");
  		harbors = (ArrayList<Harbor>) savedState.getSerializable("harbors");
  		placements = (LinkedHashMap<Integer, List<String>>) savedState.getSerializable("placements");
  		orderedPlacements = (ArrayList<Integer>) savedState.getSerializable("orderedPlacements");
  		placementBookmark = (Integer) savedState.getSerializable("placementBookmark");
  		refreshView();
  	} else {
      refreshMap();
  	}

		resetProgressBar();
  }
  
  @Override
  protected Dialog onCreateDialog(int id) {
  	Dialog dialog;
  	switch(id) {
  	  case DIALOG_SIZE_ID:
  	  	dialog = createSizePicker();
  		  break;
  	  case DIALOG_TYPE_ID:
  	  	dialog = createTypePicker();
  	  	break;
  	  case DIALOG_WELCOME_ID:
  	  	dialog = createWelcomeDialog();
  	  	break;
  	  case DIALOG_ARE_YOU_SURE_ID:
  	  	dialog = createAreYouSureDialog();
  	  	break;
  	  case DIALOG_GRAPH_ID:
  	  	dialog = createGraphDialog();
  	  	break;
  	  default:
  			dialog = null;
  	}
  	return dialog;
  }
  
  public boolean onPrepareOptionsMenu(Menu menu) {
  	setContentView(R.layout.menu);
  	if (graphView != null) {
  		graphView.releaseSleepLock();
  	}
  	return true;
  }
  
  public void rollTrack(View v) {
		showGraphView();
		if (!shownGraphAlertAlready) {
		  showDialog(DIALOG_GRAPH_ID);
		  shownGraphAlertAlready = true;
		}
  }
  
  public void about(View v) {
  	setContentView(R.layout.welcome);
  }
  
  public void shuffle(View v) {
  	if (shuffleTypeId == R.drawable.shuffle_map) {
			startThinking();
			refreshMap();
  	} else if (shuffleTypeId == R.drawable.shuffle_odds) {
  		if (currentType != MapType.TRADITIONAL) {
  			shuffleProbabilities();
  		}
  	} else if (shuffleTypeId == R.drawable.shuffle_harbors) {
    	shuffleHarbors();
  	} else { // Prev
  		updateReverseShuffle(v);
  	}
  }
  
  public void shuffle2(View v) {
  	if (shuffleTypeId == R.drawable.shuffle_map) {
			startThinking();
			refreshMap();
  	} else if (shuffleTypeId == R.drawable.shuffle_odds) {
  		if (currentType != MapType.TRADITIONAL) {
  			shuffleProbabilities();
  		}
  	} else if (shuffleTypeId == R.drawable.shuffle_harbors) {
    	shuffleHarbors();
  	} else { // Next
  		updateShuffle(v);
  	}
  }
  
  public void updateShuffle(View v) {
  	ImageView shuffleType = (ImageView) findViewById(R.id.shuffleType);
  	if (shuffleTypeId == R.drawable.shuffle_map) {
  		shuffleTypeId = R.drawable.shuffle_odds;
  		shuffleType.setImageResource(R.drawable.shuffle_odds);
  	} else if (shuffleTypeId == R.drawable.shuffle_odds) {
  		shuffleTypeId = R.drawable.shuffle_harbors;
  		shuffleType.setImageResource(R.drawable.shuffle_harbors);
  	} else if (shuffleTypeId == R.drawable.shuffle_harbors) {
  		shuffleTypeId = R.drawable.shuffle_map;
  		shuffleType.setImageResource(R.drawable.shuffle_map);
  	} else { // Prev/Next
  		placementBookmark = placementBookmark == placements.size() - 1 ? placementBookmark : placementBookmark + 1;
      refreshView();
  	}
  }
  
  public void updateReverseShuffle(View v) {
  	ImageView shuffleType = (ImageView) findViewById(R.id.shuffleType);
  	if (shuffleTypeId == R.drawable.shuffle_map) {
  		shuffleTypeId = R.drawable.shuffle_harbors;
  		shuffleType.setImageResource(R.drawable.shuffle_harbors);
  	} else if (shuffleTypeId == R.drawable.shuffle_harbors) {
  		shuffleTypeId = R.drawable.shuffle_odds;
  		shuffleType.setImageResource(R.drawable.shuffle_odds);
  	} else if (shuffleTypeId == R.drawable.shuffle_odds) {
  		shuffleTypeId = R.drawable.shuffle_map;
  		shuffleType.setImageResource(R.drawable.shuffle_map);
  	} else { // Prev/Next
  		placementBookmark = placementBookmark == 0 ? 0 : placementBookmark - 1;
      refreshView();
  	}
  }
  
  public void addGraphButtons() {  	
  	// Add long click abilities to graph buttons
  	graphButton2 = (ImageView) findViewById(R.id.graph_button_2);
  	graphButton2.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(2, true);
				return true;
			}
		});
  	graphButton3 = (ImageView) findViewById(R.id.graph_button_3);
  	graphButton3.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(3, true);
				return true;
			}
		});
  	graphButton4 = (ImageView) findViewById(R.id.graph_button_4);
  	graphButton4.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(4, true);
				return true;
			}
		});
  	graphButton5 = (ImageView) findViewById(R.id.graph_button_5);
  	graphButton5.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(5, true);
				return true;
			}
		});
  	graphButton6 = (ImageView) findViewById(R.id.graph_button_6);
  	graphButton6.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(6, true);
				return true;
			}
		});
  	graphButton7 = (ImageView) findViewById(R.id.graph_button_7);
  	graphButton7.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(7, true);
				return true;
			}
		});
  	graphButton8 = (ImageView) findViewById(R.id.graph_button_8);
  	graphButton8.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(8, true);
				return true;
			}
		});
  	graphButton9 = (ImageView) findViewById(R.id.graph_button_9);
  	graphButton9.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(9, true);
				return true;
			}
		});
  	graphButton10 = (ImageView) findViewById(R.id.graph_button_10);
  	graphButton10.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(10, true);
				return true;
			}
		});
  	graphButton11 = (ImageView) findViewById(R.id.graph_button_11);
  	graphButton11.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(11, true);
				return true;
			}
		});
  	graphButton12 = (ImageView) findViewById(R.id.graph_button_12);
  	graphButton12.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				incrementGraph(12, true);
				return true;
			}
		});
  }
  
  private String getLastRolls() {
  	StringBuilder builder = new StringBuilder();
		Stack<Integer> temp = new Stack<Integer>();
  	for (int i = 0; i < 4; i++) {
  		if (!graphStack.isEmpty()) {
  			int num = graphStack.pop();
  			builder.append(Math.abs(num) + " ");
  		  temp.push(num);
  		}
  	}
  	while (!temp.isEmpty()) {
  		graphStack.push(temp.pop());
  	}
  	return builder.toString();
  }
  
  private void resetProgressBar() {  	
  	/*
  	progressBar = (ProgressBar) findViewById(R.id.progress_bar);
  	progressBar.setVisibility(View.INVISIBLE);
  	*/
  }
  
  public void placementChoice(View v) {
  	setContentView(R.layout.main);
  	mapView = (MapView) findViewById(R.id.map_view);
		resetProgressBar();

		shuffleTypeId = R.drawable.prev;
  	ImageView shuffleType = (ImageView) findViewById(R.id.shuffleType);
		shuffleType.setImageResource(R.drawable.prev);
  	ImageView secondShuffleType = (ImageView) findViewById(R.id.secondShuffleType);
		secondShuffleType.setImageResource(R.drawable.next);
		secondShuffleType.setVisibility(View.VISIBLE);
  	
		if (placementBookmark < 0) {
			placementBookmark = 0;
		}
    refreshView();  	
  }
  
  public void resetContentViewNoRefresh(View v) {
  	setContentView(R.layout.main);
  	mapView = (MapView) findViewById(R.id.map_view);
		resetProgressBar();

  	ImageView shuffleType = (ImageView) findViewById(R.id.shuffleType);
  	ImageView secondShuffleType = (ImageView) findViewById(R.id.secondShuffleType);
  	if (shuffleTypeId == R.drawable.prev) {
  		shuffleTypeId = R.drawable.shuffle_map;
	  	shuffleType.setImageResource(R.drawable.shuffle_map);
	  	secondShuffleType.setVisibility(View.INVISIBLE);
  	}
		placementBookmark = -1;
    refreshView();
  }
  
  public void resetContentView(View v) {
  	setContentView(R.layout.main);
  	mapView = (MapView) findViewById(R.id.map_view);
		resetProgressBar();
    refreshMap();
  }
  
  public void undoGraph(View v) {
  	if (graphStack.size() > 0) {
    	int popped = graphStack.pop();
    	if (popped > 0) {
      	if (graphProbs[popped] > 0) {
      		graphProbs[popped]--;
      	}
    	} else { // Negative
    		popped *= -1;
    		if (graphRobberProbs[popped] > 0) {
    			graphRobberProbs[popped]--;
    		}
    	}
  	}
  	
  	graphView = (GraphView) findViewById(R.id.graph_view);
  	graphView.setProbs(graphProbs);
  	graphView.setRobberProbs(graphRobberProbs);
  	graphView.setStack(graphStack);
  	lastRollsText = (TextView) findViewById(R.id.last_rolls_text);
		lastRollsText.setText(getLastRolls());
  	graphView.postInvalidate(); // Force refresh
  }
  
  public void areYouSure(View v) {
  	showDialog(DIALOG_ARE_YOU_SURE_ID);
  }
  
  public void options(View v) {
  	setContentView(R.layout.menu);
  }
  
  public void showGraphView() {
		setContentView(R.layout.graph);
		addGraphButtons();
		graphView = (GraphView) findViewById(R.id.graph_view);
  	graphView.setProbs(graphProbs);
  	graphView.setRobberProbs(graphRobberProbs);
  	graphView.setStack(graphStack);
  	lastRollsText = (TextView) findViewById(R.id.last_rolls_text);
		lastRollsText.setText(getLastRolls());
		graphView.aquireSleepLock();
  	graphView.postInvalidate(); // Force refresh
  }
  
  public void resetGraph() {
  	for (int i = 0; i < graphProbs.length; i++) {
  		if (graphProbs[i] != -1) {
  			graphProbs[i] = 0;
  		}
  	}
  	for (int i = 0; i < graphRobberProbs.length; i++) {
  		if (graphRobberProbs[i] != -1) {
  			graphRobberProbs[i] = 0;
  		}
  	}
  	graphStack.clear();
  	
  	graphView = (GraphView) findViewById(R.id.graph_view);
  	graphView.setProbs(graphProbs);
  	graphView.setRobberProbs(graphRobberProbs);
  	graphView.setStack(graphStack);
  	lastRollsText = (TextView) findViewById(R.id.last_rolls_text);
		lastRollsText.setText(getLastRolls());
  	graphView.postInvalidate(); // Force refresh
  }
  
  private void incrementGraph(int n, boolean longClick) {
  	if (longClick) {
  		graphRobberProbs[n]++;
  		graphStack.push(n * -1);
  	} else {
    	graphProbs[n]++;
    	graphStack.push(n);
  	}
  	
  	graphView = (GraphView) findViewById(R.id.graph_view);
  	graphView.setProbs(graphProbs);
  	graphView.setRobberProbs(graphRobberProbs);
  	graphView.setStack(graphStack);
  	lastRollsText = (TextView) findViewById(R.id.last_rolls_text);
		lastRollsText.setText(getLastRolls());
  	graphView.postInvalidate(); // Force refresh
  }
  
  private void incrementGraph(int n) {
  	incrementGraph(n, false);
  }
  
  public void incrementGraph(View v) {
  	if (v.equals(findViewById(R.id.graph_button_2))) {
  		incrementGraph(2);
  	} else if (v.equals(findViewById(R.id.graph_button_3))) {
  		incrementGraph(3);
  	} else if (v.equals(findViewById(R.id.graph_button_4))) {
  		incrementGraph(4);
  	} else if (v.equals(findViewById(R.id.graph_button_5))) {
  		incrementGraph(5);
  	} else if (v.equals(findViewById(R.id.graph_button_6))) {
  		incrementGraph(6);
  	} else if (v.equals(findViewById(R.id.graph_button_7))) {
  		incrementGraph(7);
  	} else if (v.equals(findViewById(R.id.graph_button_8))) {
  		incrementGraph(8);
  	} else if (v.equals(findViewById(R.id.graph_button_9))) {
  		incrementGraph(9);
  	} else if (v.equals(findViewById(R.id.graph_button_10))) {
  		incrementGraph(10);
  	} else if (v.equals(findViewById(R.id.graph_button_11))) {
  		incrementGraph(11);
  	} else if (v.equals(findViewById(R.id.graph_button_12))) {
  		incrementGraph(12);
  	}
  }
  
  private Dialog createAreYouSureDialog() {
  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	builder.setIcon(R.drawable.icon);
  	builder.setTitle("Reset the Board");
  	builder.setMessage("Are you sure you'd like to reset the board? This action "
  			+ "cannot be undone.");
  	builder.setPositiveButton("Yes", new OnClickListener() {
  		public void onClick(DialogInterface dialog, int id) {
  			dialog.cancel();
  			resetGraph();
  		}
  	});
  	builder.setNegativeButton("No", new OnClickListener() {
  		public void onClick(DialogInterface dialog, int id) {
  			dialog.cancel();
  		}
  	});
  	return builder.create();  	
  }
  
  private Dialog createWelcomeDialog() {
  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	builder.setIcon(R.drawable.icon);
  	builder.setTitle("Better Settlers\nBoard Generator");
  	builder.setMessage("Designed to make game setup for Settlers of Catan faster "
  			+ "and simpler, the generator creates gameplay that is more evenly "
  			+ "challenging and engaging. The result is a better game of Settlers.");
  	builder.setPositiveButton("About", new OnClickListener() {
  		public void onClick(DialogInterface dialog, int id) {
  			dialog.cancel();
        setContentView(R.layout.welcome);
  		}
  	});
  	builder.setNegativeButton("Dismiss", new OnClickListener() {
  		public void onClick(DialogInterface dialog, int id) {
  			dialog.cancel();
  		}
  	});
  	return builder.create();
  }
  
  private Dialog createGraphDialog() {
  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	builder.setIcon(R.drawable.icon);
  	builder.setTitle("Better Settlers\nRoll Tracker");
  	builder.setMessage("Keep track of dice rolls during the game and see the "
  			+ "probability distribution for each game.\n\nLong press for robbered rolls.");
  	builder.setPositiveButton("Dismiss", new OnClickListener() {
  		public void onClick(DialogInterface dialog, int id) {
  			dialog.cancel();
  		}
  	});
  	return builder.create();
  }
  
  private Dialog createTypePicker() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Pick a type");
  	builder.setIcon(R.drawable.icon);
    // Default to choice "1"
    builder.setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {
        	// If user clicked same button, just dismiss
        	boolean doRefresh = true;
        	switch(item) {
        	  case 0:
        	  	if (currentType == MapType.FAIR) {
        	  		doRefresh = false;
        	  		break;
        	  	}
        	  	currentType = MapType.FAIR;
        	    break;
        	  case 1:
        	  	if (currentType == MapType.TRADITIONAL) {
        	  		doRefresh = false;
        	  		break;
        	  	}
        	  	currentType = MapType.TRADITIONAL;
        	    break;
        	  case 2:
        	  	if (currentType == MapType.RANDOM) {
        	  		doRefresh = false;
        	  		break;
        	  	}
        	  	currentType = MapType.RANDOM;
        	  	break;
        	  default:
        	  	break;
        	}
        	if (doRefresh) {
      	  	resetContentView(mapView);
        	} else {
      	  	resetContentViewNoRefresh(mapView);
        	}
        	dismissDialog(DIALOG_TYPE_ID);
        }
    });
    return builder.create();	
  }
  
  private Dialog createSizePicker() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Pick a size");
  	builder.setIcon(R.drawable.icon);
    builder.setSingleChoiceItems(sizes, 0, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {
        	boolean doRefresh = true;
        	switch(item) {
        	  case 0:
        	  	if (currentMap == MapSize.STANDARD) {
        	  		doRefresh = false;
        	  		break;
        	  	}
        	  	currentMap = MapSize.STANDARD;
        	    break;
        	  case 1:
        	  	if (currentMap == MapSize.LARGE) {
        	  		doRefresh = false;
        	  		break;
        	  	}
        	  	currentMap = MapSize.LARGE;
        	    break;
        	  case 2:
        	  	if (currentMap == MapSize.XLARGE) {
        	  		doRefresh = false;
        	  		break;
        	  	}
        	  	currentMap = MapSize.XLARGE;
        	  default:
        	  	break;
        	}
        	if (doRefresh) {
      	  	resetContentView(mapView);
        	} else {
      	  	resetContentViewNoRefresh(mapView);
        	}
        	dismissDialog(DIALOG_SIZE_ID);
        }
    });
    return builder.create();	
  }
  
  public void pickSize(View v) {
  	ImageView shuffleType = (ImageView) findViewById(R.id.shuffleType);
  	if (shuffleType != null) {
  		shuffleTypeId = R.drawable.shuffle_map;
	  	shuffleType.setImageResource(R.drawable.shuffle_map);
  	}
		placementBookmark = -1;
  	showDialog(DIALOG_SIZE_ID);
  }
  
  public void pickType(View v) {
  	ImageView shuffleType = (ImageView) findViewById(R.id.shuffleType);
  	if (shuffleType != null) {
  		shuffleTypeId = R.drawable.shuffle_map;
	  	shuffleType.setImageResource(R.drawable.shuffle_map);
  	}
		placementBookmark = -1;
  	showDialog(DIALOG_TYPE_ID);
  }
  
  private void welcome() {
  	showDialog(DIALOG_WELCOME_ID);
  }
  
  private void startThinking() {
  	if (mapView != null) {
    	ImageView progressImage = (ImageView) findViewById(R.id.progress_image);
  		progressImage.setImageResource(R.drawable.loading);
    	progressImage.setVisibility(View.VISIBLE);

    	mapView.setVisibility(View.INVISIBLE);
  	}
  	//progressBar.setVisibility(View.VISIBLE);
  }
  
  private void stopThinking() {
  	if (mapView != null) {
    	mapView.setVisibility(View.VISIBLE);

    	ImageView progressImage = (ImageView) findViewById(R.id.progress_image);
  		progressImage.setImageResource(0);
    	progressImage.setVisibility(View.INVISIBLE);
  	}
  	//progressBar.setVisibility(View.INVISIBLE);
  }
  
  private void refreshMap() {
  	Log.i("BS", "START");
  	startThinking();
  	new Thread(new Runnable() {
  		public void run() {  	  	
  			resourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
  			probabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
  			harborBoard = new Harbor[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
  	    
  			orderedProbs = MapLogic.getProbabilities(currentMap, currentType);
  			orderedResources = MapLogic.getResources(currentMap, currentType, orderedProbs);
  	  	harbors = MapLogic.getHarbors(currentMap, currentType, orderedResources, orderedProbs);
  	  	placements = PlacementLogic.getBestPlacements(currentMap, 0 /* all */, orderedResources, orderedProbs, harbors);
        orderedPlacements.clear();
        for (int key : placements.keySet()) {
        	orderedPlacements.add(key);
        }  	  	

  	  	handler.sendEmptyMessage(0);
  		}
    }).start();
  }
  
  private void shuffleProbabilities() {
  	startThinking();
  	new Thread(new Runnable() {
  		public void run() {
  	  	probabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
  	    orderedProbs = MapLogic.getProbabilities(currentMap, currentType, orderedResources);
  	  	placements = PlacementLogic.getBestPlacements(currentMap, 0 /* all */, orderedResources, orderedProbs, harbors);
        orderedPlacements.clear();
  	  	for (int key : placements.keySet()) {
        	orderedPlacements.add(key);
        }

  	  	handler.sendEmptyMessage(0);
  		}
    }).start();
  }
  
  private void shuffleHarbors() {
  	startThinking();
  	new Thread(new Runnable() {
  		public void run() {
  	  	harborBoard = new Harbor[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
  	  	harbors = MapLogic.getHarbors(currentMap, currentType, orderedResources, orderedProbs);
  	  	placements = PlacementLogic.getBestPlacements(currentMap, 0 /* all */, orderedResources, orderedProbs, harbors);
        orderedPlacements.clear();
        for (int key : placements.keySet()) {
        	orderedPlacements.add(key);
        }

  	  	handler.sendEmptyMessage(0);
  		}
    }).start();
  }
  
  private void refreshView() {
  	mapView.setMapType(currentMap);
  	
  	fillResourceProbabilityAndHarbors();
  	
    mapView.setLandAndWaterResources(resourceBoard, harborBoard);
    mapView.setProbabilities(probabilityBoard);
    mapView.setHarbors(harbors);
    mapView.setPlacementBookmark(placementBookmark);
    mapView.setPlacements(placements);
    Log.i("OP", orderedPlacements.toString());
    Log.i("BK", Integer.toString(placementBookmark));
    Log.i("3", placements.toString());
    mapView.setOrderedPlacements(orderedPlacements);
		mapView.postInvalidate();  // Force refresh
  }
  
  private void fillResourceProbabilityAndHarbors() {
    for (int i = 0; i < currentMap.getLandGrid().length; i++) {
    	Point point = currentMap.getLandGrid()[i];
    	resourceBoard[point.x][point.y] = orderedResources.get(i);
    	probabilityBoard[point.x][point.y] = orderedProbs.get(i);
    }
    for (int i = 0; i < currentMap.getWaterGrid().length; i++) {
    	Point point = currentMap.getWaterGrid()[i];
    	harborBoard[point.x][point.y] = harbors.get(i); 
    }
  }
}