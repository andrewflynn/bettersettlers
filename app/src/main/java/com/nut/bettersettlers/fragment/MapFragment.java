package com.nut.bettersettlers.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.Harbor;
import com.nut.bettersettlers.data.MapSize;
import com.nut.bettersettlers.data.MapType;
import com.nut.bettersettlers.data.Resource;
import com.nut.bettersettlers.fragment.dialog.MapsDialogFragment;
import com.nut.bettersettlers.fragment.dialog.WelcomeDialogFragment;
import com.nut.bettersettlers.logic.MapLogic;
import com.nut.bettersettlers.logic.PlacementLogic;
import com.nut.bettersettlers.ui.MapView;
import com.nut.bettersettlers.util.Analytics;
import com.nut.bettersettlers.util.BetterLog;
import com.nut.bettersettlers.util.Consts;
import com.nut.bettersettlers.util.Util;

import java.util.ArrayList;

import static com.nut.bettersettlers.util.Consts.BOARD_RANGE_X;
import static com.nut.bettersettlers.util.Consts.BOARD_RANGE_Y;

public class MapFragment extends Fragment {
	private static final String STATE_MAP_SIZE = "MAP_SIZE";
	private static final String STATE_MAP_TYPE = "MAP_TYPE";
	private static final String STATE_RESOURCES = "MAP_RESOURCES";
	private static final String STATE_PROBABILITIES = "PROBABILITIES";
	private static final String STATE_UNKNOWNS = "UNKNOWNS";
	private static final String STATE_UNKNOWN_PROBABILITIES = "UNKNOWN_PROBABILITIES";
	private static final String STATE_HARBORS = "HARBORS";
	private static final String STATE_PLACEMENT_BOOKMARK = "PLACEMENT_BOOKMARK";
	private static final String STATE_PLACEMENT_MAX = "PLACEMENT_MAX";
	private static final String STATE_PLACEMENTS = "PLACEMENTS";
	private static final String STATE_ORDERED_PLACEMENTS = "ORDERED_PLACEMENTS";
	
	private MapView mMapView;

	private ImageView mPlacementsButton;
	private LinearLayout mPlacementsContainer;
	private ImageView mRefreshButton;
	private ImageView mRefreshDownButton;

	private MapSize mMapSize;
	private int mMapType;
	private ArrayList<Harbor> mHarborList = new ArrayList<>();
	private ArrayList<Resource> mResourceList = new ArrayList<>();
	private ArrayList<Integer> mProbabilityList = new ArrayList<>();
	private ArrayList<Resource> mUnknownsList = new ArrayList<>();
	private ArrayList<Integer> mUnknownProbabilitiesList = new ArrayList<>();
	private ArrayList<Integer> mPlacementsList = new ArrayList<>();
	private SparseArray<ArrayList<String>> mOrderedPlacementsList = new SparseArray<>();
	private int mPlacementBookmark = -1;
	private int mPlacementMax;
	
	private boolean mShowSeafarers = false;

	///////////////////////////////
	// Fragment method overrides //
	///////////////////////////////
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.map, container, false);
		mMapView = (MapView) view.findViewById(R.id.map_view);
		
		ImageView settingsButton = (ImageView) view.findViewById(R.id.settings_button);
		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    ft.addToBackStack(null);
		    	MapsDialogFragment.newInstance().show(ft, "MapsDialogFragment");
                Analytics.track(getActivity(), Analytics.CATEGORY_MAIN, Analytics.ACTION_BUTTON,
                        Analytics.SETTINGS);
			}
		});
		
		mPlacementsButton = (ImageView) view.findViewById(R.id.placements_button);
		mPlacementsButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
		    	if (showingPlacements()) {
		    		hidePlacements(true);
		    	} else {
		    		showPlacements(true);
                    Analytics.track(getActivity(), Analytics.CATEGORY_MAIN, Analytics.ACTION_BUTTON,
                            Analytics.USE_PLACEMENTS);
		    	}
			}
		});
		
		mPlacementsContainer = (LinearLayout) view.findViewById(R.id.placements_container);
		
		ImageView placementsLeftButton = (ImageView) view.findViewById(R.id.placements_left_button);
		placementsLeftButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				prevPlacement();
                Analytics.track(getActivity(), Analytics.CATEGORY_MAIN, Analytics.ACTION_BUTTON,
                        Analytics.PREV_PLACEMENTS);
			}
		});
		
		ImageView placementsRightButton = (ImageView) view.findViewById(R.id.placements_right_button);
		placementsRightButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				nextPlacement();
                Analytics.track(getActivity(), Analytics.CATEGORY_MAIN, Analytics.ACTION_BUTTON,
                        Analytics.NEXT_PLACEMENTS);
			}
		});
		
		mRefreshButton = (ImageView) view.findViewById(R.id.refresh_button);
		mRefreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				asyncMapShuffle();
                Analytics.track(getActivity(), Analytics.CATEGORY_MAIN, Analytics.ACTION_BUTTON,
                        Analytics.SHUFFLE_MAP);
			}
		});
		mRefreshDownButton = (ImageView) view.findViewById(R.id.refresh_down_button);

		// Set initial state of placement buttons
		if (showingPlacements()) {
			showPlacements(false);
		} else {
			hidePlacements(false);
		}
		
		return view;
	}

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(STATE_MAP_SIZE, mMapSize);
		outState.putInt(STATE_MAP_TYPE, mMapType);
		outState.putParcelableArrayList(STATE_RESOURCES, mResourceList);
		outState.putParcelableArrayList(STATE_UNKNOWNS, mUnknownsList);
		outState.putIntegerArrayList(STATE_PROBABILITIES, mProbabilityList);
		outState.putIntegerArrayList(STATE_UNKNOWN_PROBABILITIES, mUnknownProbabilitiesList);
		outState.putIntegerArrayList(STATE_PLACEMENTS, mPlacementsList);
		outState.putBundle(STATE_ORDERED_PLACEMENTS, Util.sparseArrayArrayListToBundle(mOrderedPlacementsList));
		outState.putParcelableArrayList(STATE_HARBORS, mHarborList);
		outState.putInt(STATE_PLACEMENT_BOOKMARK, mPlacementBookmark);
		outState.putInt(STATE_PLACEMENT_MAX, mPlacementMax);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		BetterLog.d("MapFragment.onActivityCreated() " + savedInstanceState);
		
		// If savedInstanceState == null, this is the first time the activity is created (not a rotation)
		if (savedInstanceState == null) {
			// Show what's new if we need to
			SharedPreferences prefs = getActivity().getSharedPreferences(Consts.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
			boolean shownWhatsNew = prefs.getBoolean(Consts.SHARED_PREFS_KEY_WHATS_NEW_HELP, false);
			if (!shownWhatsNew) {
				WelcomeDialogFragment.newInstance().show(getFragmentManager(), "WelcomeDialog");
				SharedPreferences.Editor prefsEditor = prefs.edit();
				prefsEditor.putBoolean(Consts.SHARED_PREFS_KEY_WHATS_NEW_HELP, true);
				prefsEditor.apply();
			}
			
			mMapSize = MapSize.STANDARD;
			mMapType = MapType.FAIR;
			asyncMapShuffle();
			recordView();
		} else { // we have state from a rotation, use it
			if (savedInstanceState.containsKey(STATE_MAP_SIZE)) {
				mMapSize = savedInstanceState.getParcelable(STATE_MAP_SIZE);
			}
			if (savedInstanceState.containsKey(STATE_MAP_TYPE)) {
				mMapType = savedInstanceState.getInt(STATE_MAP_TYPE);
			}
			if (savedInstanceState.containsKey(STATE_RESOURCES)) {
				mResourceList = savedInstanceState.getParcelableArrayList(STATE_RESOURCES);
			}
			if (savedInstanceState.containsKey(STATE_UNKNOWNS)) {
				mUnknownsList = savedInstanceState.getParcelableArrayList(STATE_UNKNOWNS);
			}
			if (savedInstanceState.containsKey(STATE_PROBABILITIES)) {
				mProbabilityList = savedInstanceState.getIntegerArrayList(STATE_PROBABILITIES);
			}
			if (savedInstanceState.containsKey(STATE_UNKNOWN_PROBABILITIES)) {
				mUnknownProbabilitiesList = savedInstanceState.getIntegerArrayList(STATE_UNKNOWN_PROBABILITIES);
			}
			if (savedInstanceState.containsKey(STATE_PLACEMENTS)) {
				mPlacementsList = savedInstanceState.getIntegerArrayList(STATE_PLACEMENTS);
			}
			if (savedInstanceState.containsKey(STATE_ORDERED_PLACEMENTS)) {
				mOrderedPlacementsList = Util.bundleToSparseArrayArrayList(savedInstanceState.getBundle(STATE_ORDERED_PLACEMENTS));
			}
			if (savedInstanceState.containsKey(STATE_HARBORS)) {
				mHarborList = savedInstanceState.getParcelableArrayList(STATE_HARBORS);
			}
			if (savedInstanceState.containsKey(STATE_PLACEMENT_BOOKMARK)) {
				mPlacementBookmark = savedInstanceState.getInt(STATE_PLACEMENT_BOOKMARK);
			}
			if (savedInstanceState.containsKey(STATE_PLACEMENT_MAX)) {
				mPlacementMax = savedInstanceState.getInt(STATE_PLACEMENT_MAX);
			}
			
			refreshView();
		}
	}
    
    private void showProgressBar() {
    	if (mRefreshButton.getVisibility() == View.VISIBLE) {
        	getActivity().runOnUiThread(new Runnable() {
    			@Override
    			public void run() {
    	    		mRefreshDownButton.setVisibility(View.VISIBLE);
    	    		mRefreshButton.setVisibility(View.INVISIBLE);
    			}
    		});
    	}
    }
    
    private void killProgressBar() {
    	if (mRefreshDownButton.getVisibility() == View.VISIBLE) {
        	getActivity().runOnUiThread(new Runnable() {
    			@Override
    			public void run() {
    	    		mRefreshDownButton.setVisibility(View.INVISIBLE);
    	    		mRefreshButton.setVisibility(View.VISIBLE);
    			}
    		});
    	}
    }
    
    public void hidePlacements(boolean animate) {
		mPlacementsButton.setImageDrawable(getResources().getDrawable(R.drawable.main_placements));
		
    	if (animate) {
    		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.placements_out);
    		animation.setAnimationListener(new AnimationListener() {
    			@Override public void onAnimationStart(Animation animation) {}
    			@Override public void onAnimationRepeat(Animation animation) {}
    			@Override public void onAnimationEnd(Animation animation) {
    				togglePlacements(false);
    				mPlacementsContainer.setVisibility(View.GONE);
    			}
    		});
    		mPlacementsContainer.startAnimation(animation);
    	} else {
			togglePlacements(false);
			mPlacementsContainer.setVisibility(View.GONE);
    	}
    }
    
    public void showPlacements(boolean animate) {
		mPlacementsButton.setImageDrawable(getResources().getDrawable(R.drawable.main_placements_down));
		mPlacementsContainer.setVisibility(View.VISIBLE);
		
    	if (animate) {
    		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.placements_in);
    		animation.setAnimationListener(new AnimationListener() {
    			@Override public void onAnimationStart(Animation animation) {}
    			@Override public void onAnimationRepeat(Animation animation) {}
    			@Override public void onAnimationEnd(Animation animation) {
    				togglePlacements(true);
    			}
    		});
    		mPlacementsContainer.startAnimation(animation);
    	} else {
    		togglePlacements(true);
    	}
    }

	//////////////////////////
	// Async generate tasks //
	//////////////////////////
    private abstract class ShuffleAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showProgressBar();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// MapLogic
			mapChanges();
			
			// PlacementLogic
			Pair<ArrayList<Integer>, SparseArray<ArrayList<String>>> placementPair =
			        PlacementLogic.getBestPlacements(getCatanMap(), mResourceList, mProbabilityList, mHarborList);
			mPlacementsList = placementPair.first;
			mOrderedPlacementsList = placementPair.second;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			refreshView();
			killProgressBar();
		}
		
		protected abstract void mapChanges();
    }
    
	private class ShuffleMapAsyncTask extends ShuffleAsyncTask {
		@Override
		protected void mapChanges() {
			// Refresh New World maps
			if (mMapSize.title.equals("new_world")) {
				mMapSize = MapSize.NEW_WORLD;
				mMapSize.mapProvider.refresh(getActivity());
			}
			if (mMapSize.title.equals("new_world_exp")) {
				mMapSize = MapSize.NEW_WORLD_EXP;
				mMapSize.mapProvider.refresh(getActivity());
			}
			mProbabilityList = MapLogic.getProbabilities(getCatanMap(), mMapType);
			mUnknownProbabilitiesList = MapLogic.getUnknownProbabilities(getCatanMap());
			mResourceList = MapLogic.getResources(getCatanMap(), mMapType, mProbabilityList);
			mUnknownsList = MapLogic.getUnknowns(getCatanMap(), mUnknownProbabilitiesList);
			mHarborList = MapLogic.getHarbors(getCatanMap(), mMapType, mResourceList, mProbabilityList);
		}
	}
    public void asyncProbsShuffle() {
    	new ShuffleProbabilitiesAsyncTask().execute();
    }

	private class ShuffleProbabilitiesAsyncTask extends ShuffleAsyncTask {
		@Override
		protected void mapChanges() {
			mProbabilityList = MapLogic.getProbabilities(getCatanMap(), mMapType, mResourceList);
			mUnknownProbabilitiesList = MapLogic.getUnknownProbabilities(getCatanMap());
			mUnknownsList = MapLogic.getUnknowns(getCatanMap(), mUnknownProbabilitiesList);
		}
	}
    public void asyncHarborsShuffle() {
    	new ShuffleHarborsAsyncTask().execute();
    }

	private class ShuffleHarborsAsyncTask extends ShuffleAsyncTask {
		@Override
		protected void mapChanges() {
			mHarborList = MapLogic.getHarbors(getCatanMap(), mMapType, mResourceList, mProbabilityList);
		}
	}
    public void asyncMapShuffle() {
    	new ShuffleMapAsyncTask().execute();
    }

	//////////////////////////
	// Map helper functions //
	//////////////////////////    
	private void refreshView() {
		if (mResourceList.isEmpty() || mProbabilityList.isEmpty() || mHarborList.isEmpty()) {
			return;
		}
		
		mMapView.setMapSize(mMapSize);
		
		fillResourceProbabilityAndHarbors();
		fillPlacements();
		mMapView.setReady();
		mMapView.invalidate();  // Force refresh
	}

	private void fillResourceProbabilityAndHarbors() {
		Resource[][] resourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		Resource[][] uResourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		int[][] probabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		int[][] uProbabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		Harbor[][] harborBoard = new Harbor[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		for (int i = 0; i < getCatanMap().landGrid.length; i++) {
			Point point = getCatanMap().landGrid[i];
			resourceBoard[point.x][point.y] = mResourceList.get(i);
			probabilityBoard[point.x][point.y] = mProbabilityList.get(i);
		}
		for (int i = 0; i < getCatanMap().waterGrid.length; i++) {
			Point point = getCatanMap().waterGrid[i];
			harborBoard[point.x][point.y] = mHarborList.get(i);
		}
		for (int i = 0; i < getCatanMap().unknownGrid.length; i++) {
			Point point = getCatanMap().unknownGrid[i];
			uResourceBoard[point.x][point.y] = mUnknownsList.get(i);
			uProbabilityBoard[point.x][point.y] = mUnknownProbabilitiesList.get(i);
		}

		mMapView.setLandAndWaterResources(resourceBoard, harborBoard, uResourceBoard);
		mMapView.setProbabilities(probabilityBoard, uProbabilityBoard);
		mMapView.setHarbors(mHarborList);
		mMapView.setPlacementBookmark(mPlacementBookmark);
	}

	private void fillPlacements() {
		mMapView.setOrderedPlacements(mPlacementsList);
		mMapView.setPlacements(mOrderedPlacementsList);
		
		mPlacementMax = mOrderedPlacementsList.size() - 1;
		//BetterLog.v("mOrderedPlacementList: " + mOrderedPlacementList);
		//BetterLog.v("mPlacementList: " + mPlacementList);
	}
	
	public boolean showingPlacements() {
		return mPlacementBookmark >= 0;
	}
	
	public void nextPlacement() {
		mPlacementBookmark = (mPlacementBookmark == mPlacementMax) ? mPlacementBookmark : mPlacementBookmark + 1;
		refreshView();
	}
	
	public void prevPlacement() {
		mPlacementBookmark = mPlacementBookmark == 0 ? 0 : mPlacementBookmark - 1;
		refreshView();
	}
	
	public CatanMap getCatanMap() {
		return mMapSize.mapProvider.get();
	}
	public int getMapType() {
		return mMapType;
	}
	
	public boolean getShowSeafarers() {
		return mShowSeafarers;
	}

	public void setShowSeafarers(boolean showSeafarers) {
		mShowSeafarers = showSeafarers;
	}
	
	private void recordView() {
        Analytics.trackView(getActivity(), String.format(
                Analytics.VIEW_MAP_FORMAT, mMapSize.title, MapType.getString(mMapType)));
	}
	
	public void typeChoice(int type) {
		if (mMapType != type) {
			mMapType = type;
			asyncMapShuffle();
			recordView();
		}
	}
	
	public void sizeChoice(MapSize mapSize) {
		// Only Settlers maps can have traditional
		if (mMapType == MapType.TRADITIONAL
				&& (mapSize != MapSize.STANDARD && mapSize != MapSize.LARGE && mapSize != MapSize.XLARGE)) {
			mMapType = MapType.FAIR;
		}
		
		if (mMapSize != mapSize) {
			mMapSize = mapSize;
			asyncMapShuffle();
			MainActivity mainActivity = (MainActivity) getActivity();
			mainActivity.setTitleButtonText(mapSize.titleDrawableId);
			recordView();
		}
	}
	
	public void togglePlacements(boolean on) {
		if (on) {
            Analytics.track(getActivity(), Analytics.CATEGORY_MAIN, Analytics.ACTION_BUTTON,
                    Analytics.USE_PLACEMENTS);
			if (mPlacementBookmark < 0) {
				mPlacementBookmark = 0;
			}
		} else {
			mPlacementBookmark = -1;
		}
		refreshView();
	}
}