package com.nut.bettersettlers.fragment;

import static com.nut.bettersettlers.data.MapConsts.BOARD_RANGE_X;
import static com.nut.bettersettlers.data.MapConsts.BOARD_RANGE_Y;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nut.bettersettlers.R;
import com.nut.bettersettlers.activity.MainActivity;
import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.MapConsts.Harbor;
import com.nut.bettersettlers.data.MapConsts.MapType;
import com.nut.bettersettlers.data.MapConsts.Resource;
import com.nut.bettersettlers.data.MapProvider;
import com.nut.bettersettlers.data.MapProvider.MapSize;
import com.nut.bettersettlers.fragment.dialog.AboutDialogFragment;
import com.nut.bettersettlers.fragment.dialog.RateDialogFragment;
import com.nut.bettersettlers.fragment.dialog.RulesDialogFragment;
import com.nut.bettersettlers.fragment.dialog.WelcomeDialogFragment;
import com.nut.bettersettlers.logic.MapLogic;
import com.nut.bettersettlers.logic.PlacementLogic;
import com.nut.bettersettlers.misc.Consts;
import com.nut.bettersettlers.ui.MapView;

public class MapFragment extends Fragment {
	private static final String X = MapFragment.class.getSimpleName();
	
	private static final String STATE_MAP_SIZE = "MAP_SIZE";
	private static final String STATE_MAP_TYPE = "MAP_TYPE";
	private static final String STATE_RESOURCES = "MAP_RESOURCES";
	private static final String STATE_UNKNOWNS = "UNKNOWNS";
	private static final String STATE_PROBABILITIES = "PROBABILITIES";
	private static final String STATE_UNKNOWN_PROBABILITIES = "UNKNOWN_PROBABILITIES";
	private static final String STATE_HARBORS = "HARBORS";
	private static final String STATE_PLACEMENTS = "PLACEMENTS";
	private static final String STATE_ORDERED_PLACEMENTS = "ORDERED_PLACEMENTS";
	private static final String STATE_PLACEMENT_BOOKMARK = "PLACEMENT_BOOKMARK";
	private static final String STATE_ZOOM_LEVEL = "ZOOM_LEVEL";

	private static final String SHARED_PREFS_NAME = "MapActivity";
	private static final String SHARED_PREFS_SHOWN_WHATS_NEW = "ShownWhatsNewVersion13";
	
	private MapView mMapView;

	private CatanMap mMapSize;
	private MapType mMapType = MapType.FAIR;
	private Resource[][] mResourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private Resource[][] mUResourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private boolean[][] mUVisibleBoard = new boolean[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private Harbor[][] mHarborBoard = new Harbor[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private int[][] mProbabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private int[][] mUProbabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private ArrayList<Harbor> mHarborList = new ArrayList<Harbor>();
	private ArrayList<Resource> mResourceList = new ArrayList<Resource>();
	private ArrayList<Integer> mProbabilityList = new ArrayList<Integer>();
	private ArrayList<Resource> mUnknownsList = new ArrayList<Resource>();
	private ArrayList<Integer> mUnknownProbabilitiesList = new ArrayList<Integer>();
	private LinkedHashMap<Integer, List<String>> mPlacementList = new LinkedHashMap<Integer, List<String>>();
	private ArrayList<Integer> mOrderedPlacementList = new ArrayList<Integer>();
	private int mPlacementBookmark = -1;
	
	private int mRollTrackerItemId = -1;

	///////////////////////////////
	// Fragment method overrides //
	///////////////////////////////
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.map, container, false);
		mMapView = (MapView) view.findViewById(R.id.map_view);
		
		return view;
	}

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(STATE_MAP_SIZE, mMapSize.getName());
		outState.putString(STATE_MAP_TYPE, mMapType.name());
		outState.putSerializable(STATE_RESOURCES, mResourceList);
		outState.putSerializable(STATE_UNKNOWNS, mUnknownsList);
		outState.putSerializable(STATE_PROBABILITIES, mProbabilityList);
		outState.putSerializable(STATE_UNKNOWN_PROBABILITIES, mUnknownProbabilitiesList);
		outState.putSerializable(STATE_HARBORS, mHarborList);
		outState.putSerializable(STATE_PLACEMENTS, mPlacementList);
		outState.putSerializable(STATE_ORDERED_PLACEMENTS, mOrderedPlacementList);
		outState.putSerializable(STATE_PLACEMENT_BOOKMARK, mPlacementBookmark);
		outState.putSerializable(STATE_ZOOM_LEVEL, mMapView.getScale());
	}

	/** Called when the activity is going to appear. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		boolean shownWhatsNew = prefs.getBoolean(SHARED_PREFS_SHOWN_WHATS_NEW, false);
		if (savedInstanceState == null && !shownWhatsNew) {
			WelcomeDialogFragment.newInstance().show(getFragmentManager(), "WelcomeDialog");
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.putBoolean(SHARED_PREFS_SHOWN_WHATS_NEW, true);
			prefsEditor.commit();
		}
		
		if (savedInstanceState != null) {
			if (savedInstanceState.getString(STATE_MAP_SIZE) != null) {
				mMapSize = getMapProvider().getMap(savedInstanceState.getString(STATE_MAP_SIZE));
			} else {
				mMapSize = getMapProvider().getMap(MapSize.STANDARD);
			}

			if (savedInstanceState.getString(STATE_MAP_TYPE) != null) {
				mMapType = MapType.valueOf(savedInstanceState.getString(STATE_MAP_TYPE));
			} else {
				mMapType = MapType.FAIR;
			}

			if (savedInstanceState.getSerializable(STATE_RESOURCES) != null
					&& savedInstanceState.getSerializable(STATE_UNKNOWNS) != null
					&& savedInstanceState.getSerializable(STATE_PROBABILITIES) != null
					&& savedInstanceState.getSerializable(STATE_UNKNOWN_PROBABILITIES) != null
					&& savedInstanceState.getSerializable(STATE_HARBORS) != null) {
				mResourceList = (ArrayList<Resource>) savedInstanceState.getSerializable(STATE_RESOURCES);
				mUnknownsList = (ArrayList<Resource>) savedInstanceState.getSerializable(STATE_UNKNOWNS);
				mProbabilityList = (ArrayList<Integer>) savedInstanceState.getSerializable(STATE_PROBABILITIES);
				mUnknownProbabilitiesList = (ArrayList<Integer>) savedInstanceState.getSerializable(STATE_UNKNOWN_PROBABILITIES);
				mHarborList = (ArrayList<Harbor>) savedInstanceState.getSerializable(STATE_HARBORS);
				if (savedInstanceState.getSerializable(STATE_PLACEMENTS) != null
						&& savedInstanceState.getSerializable(STATE_ORDERED_PLACEMENTS) != null
						&& savedInstanceState.getSerializable(STATE_PLACEMENT_BOOKMARK) != null) {
					mPlacementList = (LinkedHashMap<Integer, List<String>>) savedInstanceState.getSerializable(STATE_PLACEMENTS);
					mOrderedPlacementList = (ArrayList<Integer>) savedInstanceState.getSerializable(STATE_ORDERED_PLACEMENTS);
					mPlacementBookmark = (Integer) savedInstanceState.getSerializable(STATE_PLACEMENT_BOOKMARK);
				}
				if (savedInstanceState.getSerializable(STATE_ZOOM_LEVEL) != null) {
					refreshView((Float) savedInstanceState.getSerializable(STATE_ZOOM_LEVEL));
				} else {
					refreshView();
				}
			}
		} else {
			mMapSize = getMapProvider().getMap(MapSize.STANDARD);
			asyncMapShuffle();
		}
	}

	//////////////////////////
	// Async generate tasks //
	//////////////////////////
	public void asyncMapShuffle() {
		if (getActivity() != null) {
			((MainActivity) getActivity()).showProgressBar();
		}
		new ShuffleMapAsyncTask().execute();
	}
	private class ShuffleMapAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			mProbabilityList = MapLogic.getProbabilities(mMapSize, mMapType);
			mUnknownProbabilitiesList = MapLogic.getUnknownProbabilities(mMapSize);
			mResourceList = MapLogic.getResources(mMapSize, mMapType, mProbabilityList);
			mUnknownsList = MapLogic.getUnknowns(mMapSize, mUnknownProbabilitiesList);
			mHarborList = MapLogic.getHarbors(mMapSize, mMapType, mResourceList, mProbabilityList);
			fillPlacements();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			refreshView();
		}
	}

	public void asyncProbsShuffle() {
		if (getActivity() != null) {
			((MainActivity) getActivity()).showProgressBar();
		}
		new ShuffleProbabilitiesAsyncTask().execute();
	}
	private class ShuffleProbabilitiesAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			mProbabilityList = MapLogic.getProbabilities(mMapSize, mMapType, mResourceList);
			mUnknownProbabilitiesList = MapLogic.getUnknownProbabilities(mMapSize);
			mUnknownsList = MapLogic.getUnknowns(mMapSize, mUnknownProbabilitiesList);
			fillPlacements();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			refreshView();
		}
	}

	public void asyncHarborsShuffle() {
		if (getActivity() != null) {
			((MainActivity) getActivity()).showProgressBar();
		}
		new ShuffleHarborsAsyncTask().execute();
	}
	private class ShuffleHarborsAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			mHarborList = MapLogic.getHarbors(mMapSize, mMapType, mResourceList, mProbabilityList);
			fillPlacements();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			refreshView();
		}
	}

	//////////////////////////
	// Map helper functions //
	//////////////////////////    
	public void refreshView() {
		refreshView(null);
	}
	public void refreshView(Float scale) {
		if (mResourceList.isEmpty() || mProbabilityList.isEmpty() || mHarborList.isEmpty()) {
			return;
		}
		
		if (scale != null && scale != 0f) {
			mMapView.setScale(scale);
		}
		mMapView.setMapSize(mMapSize);
		
		fillResourceProbabilityAndHarbors();

		mMapView.setLandAndWaterResources(mResourceBoard, mHarborBoard, mUResourceBoard);
		mMapView.setProbabilities(mProbabilityBoard, mUProbabilityBoard);
		mMapView.setVisibility(mUVisibleBoard);
		mMapView.setHarbors(mHarborList);
		mMapView.setPlacementBookmark(mPlacementBookmark);
		mMapView.setPlacements(mPlacementList);
		mMapView.setOrderedPlacements(mOrderedPlacementList);
		mMapView.invalidate();  // Force refresh
		
		if (getActivity() != null) {
			((MainActivity) getActivity()).killProgressBar();
		}
	}

	private void fillResourceProbabilityAndHarbors() {
		mResourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mUResourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mUVisibleBoard = new boolean[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mProbabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mUProbabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mHarborBoard = new Harbor[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		for (int i = 0; i < mMapSize.getLandGrid().length; i++) {
			Point point = mMapSize.getLandGrid()[i];
			mResourceBoard[point.x][point.y] = mResourceList.get(i);
			mProbabilityBoard[point.x][point.y] = mProbabilityList.get(i);
		}
		for (int i = 0; i < mMapSize.getWaterGrid().length; i++) {
			Point point = mMapSize.getWaterGrid()[i];
			mHarborBoard[point.x][point.y] = mHarborList.get(i);
		}
		for (int i = 0; i < mMapSize.getUnknownGrid().length; i++) {
			Point point = mMapSize.getUnknownGrid()[i];
			mUResourceBoard[point.x][point.y] = mUnknownsList.get(i);
			mUProbabilityBoard[point.x][point.y] = mUnknownProbabilitiesList.get(i);
		}
	}

	private void fillPlacements() {
		mPlacementList = PlacementLogic.getBestPlacements(mMapSize, 0 /* all */, mResourceList, mProbabilityList, mHarborList);
		//Log.i(X, "mPlacementList: " + mPlacementList);
		mOrderedPlacementList.clear();
		for (int key : mPlacementList.keySet()) {
			mOrderedPlacementList.add(key);
		}
	}
	
	public boolean showingPlacements() {
		return mPlacementBookmark >= 0;
	}
	
	public void nextPlacement() {
		mPlacementBookmark = mPlacementBookmark == mPlacementList.size() - 1 ? mPlacementBookmark : mPlacementBookmark + 1;
		refreshView();
	}
	
	public void prevPlacement() {
		mPlacementBookmark = mPlacementBookmark == 0 ? 0 : mPlacementBookmark - 1;
		refreshView();
	}
	
	public CatanMap getMapSize() {
		return mMapSize;
	}
	public MapType getMapType() {
		return mMapType;
	}

	////////////////////
	// Menu functions //
	////////////////////
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		if (Consts.AT_LEAST_HONEYCOMB) {
			if (showingPlacements()) {
				inflater.inflate(R.menu.map_placements_hc, menu);
			} else {
				inflater.inflate(R.menu.map_hc, menu);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		GoogleAnalyticsTracker analytics = ((MainActivity) getActivity()).getAnalytics();
		item.setChecked(true);
		
		if (item.getItemId() == mRollTrackerItemId) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.hide(getFragmentManager().findFragmentById(R.id.map_fragment));
			ft.show(getFragmentManager().findFragmentById(R.id.graph_fragment));
			ft.addToBackStack("MapToGraph");
			ft.commit();
			return true;
		}
		
		switch (item.getItemId()) {
		case R.id.refresh_item:
			asyncMapShuffle();
			analytics.trackPageView(Consts.ANALYTICS_SHUFFLE_MAP);
			return true;
		case R.id.prev_item:
			prevPlacement();
			return true;
		case R.id.next_item:
			nextPlacement();
			return true;
		// MAP TYPE
		case R.id.better_settlers_item:
			betterSettlersChoice();
			return true;
		case R.id.traditional_item:
			// Traditional only makes sense for normal maps
			if (getMapSize().getName().equals(MapProvider.MapSize.STANDARD.name)
					|| getMapSize().getName().equals(MapProvider.MapSize.LARGE.name)
					|| getMapSize().getName().equals(MapProvider.MapSize.XLARGE.name)) {
				traditionalChoice();
			}
			return true;
		case R.id.random_item:
			randomChoice();
			return true;
		// MAP SIZE
		case R.id.standard_item:
			standardChoice();
			return true;
		case R.id.large_item:
			largeChoice();
			return true;
		case R.id.xlarge_item:
			xlargeChoice();
			return true;
		case R.id.heading_for_new_shores_item:
			headingForNewShoresChoice();
			return true;
		case R.id.shuffle_probs_item:
			// We don't want to shuffle for traditional maps since it doesn't make sense
			if (getMapType() != MapType.TRADITIONAL) {
				asyncProbsShuffle();
				analytics.trackPageView(Consts.ANALYTICS_SHUFFLE_PROBABILITIES);
			}
			return true;
		case R.id.shuffle_harbors_item:
			asyncHarborsShuffle();
			analytics.trackPageView(Consts.ANALYTICS_SHUFFLE_HARBORS);
			return true;
		case R.id.rules_item:
			RulesDialogFragment.newInstance().show(getFragmentManager(), "RulesDialogFragment");
			return true;
		case R.id.rate_item:
			RateDialogFragment.newInstance().show(getFragmentManager(), "RateDialogFragment");
			return true;
		case R.id.about_item:
			AboutDialogFragment.newInstance().show(getFragmentManager(), "AboutDialogFragment");
			return true;
		default:
			return false;
		}
	}
	
	private void typeChoice(MapType type, String analyticsKey) {
		if (mMapType != type) {
			mMapType = type;
			asyncMapShuffle();
			((MainActivity) getActivity()).getAnalytics().trackPageView(String.format(analyticsKey, mMapType));
		}
	}
	
	public void betterSettlersChoice() {
		typeChoice(MapType.FAIR, Consts.ANALYTICS_CHANGE_MAP_TYPE_FORMAT);
	}
	
	public void traditionalChoice() {
		typeChoice(MapType.TRADITIONAL, Consts.ANALYTICS_CHANGE_MAP_TYPE_FORMAT);
	}
	
	public void randomChoice() {
		typeChoice(MapType.RANDOM, Consts.ANALYTICS_CHANGE_MAP_TYPE_FORMAT);
	}
	
	private void sizeChoice(CatanMap size, String analyticsKey) {
		if (mMapSize != size) {
			mMapSize = size;
			asyncMapShuffle();
			((MainActivity) getActivity()).getAnalytics().trackPageView(String.format(analyticsKey, mMapSize.getName()));
		}
	}
	
	public void standardChoice() {
		sizeChoice(getMapProvider().getMap(MapSize.STANDARD), Consts.ANALYTICS_CHANGE_MAP_SIZE_FORMAT);
	}
	
	public void largeChoice() {
		sizeChoice(getMapProvider().getMap(MapSize.LARGE), Consts.ANALYTICS_CHANGE_MAP_SIZE_FORMAT);
	}
	
	public void xlargeChoice() {
		sizeChoice(getMapProvider().getMap(MapSize.XLARGE), Consts.ANALYTICS_CHANGE_MAP_SIZE_FORMAT);
	}
	
	public void headingForNewShoresChoice() {
		sizeChoice(getMapProvider().getMap(MapSize.HEADING_FOR_NEW_SHORES), Consts.ANALYTICS_CHANGE_MAP_SIZE_FORMAT);
	}
	
	public void togglePlacements(boolean on) {
		if (on) {
			((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_USE_PLACEMENTS);
			if (mPlacementBookmark < 0) {
				mPlacementBookmark = 0;
			}
		} else {
			mPlacementBookmark = -1;
		}
		refreshView();
	}
	
	private MapProvider getMapProvider() {
		return ((MainActivity) getActivity()).getMapProvider();
	}
}