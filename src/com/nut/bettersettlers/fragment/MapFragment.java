package com.nut.bettersettlers.fragment;

import static com.nut.bettersettlers.data.MapSpecs.BOARD_RANGE_X;
import static com.nut.bettersettlers.data.MapSpecs.BOARD_RANGE_Y;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.MapSpecs.Harbor;
import com.nut.bettersettlers.data.MapSpecs.MapSize;
import com.nut.bettersettlers.data.MapSpecs.MapType;
import com.nut.bettersettlers.data.MapSpecs.Resource;
import com.nut.bettersettlers.fragment.dialog.AboutDialogFragment;
import com.nut.bettersettlers.fragment.dialog.LegalDialogFragment;
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
	private static final String STATE_PROBABILITIES = "PROBABILITIES";
	private static final String STATE_HARBORS = "HARBORS";
	private static final String STATE_PLACEMENTS = "PLACEMENTS";
	private static final String STATE_ORDERED_PLACEMENTS = "ORDERED_PLACEMENTS";
	private static final String STATE_PLACEMENT_BOOKMARK = "PLACEMENT_BOOKMARK";
	private static final String STATE_ZOOM_LEVEL = "ZOOM_LEVEL";

	private static final String SHARED_PREFS_NAME = "MapActivity";
	private static final String SHARED_PREFS_SHOWN_WHATS_NEW = "ShownWhatsNewVersion3.0";
	
	private MapView mMapView;
	private ImageView mRefreshIcon;
	private ImageView mLeftArrowIcon;
	private ImageView mRightArrowIcon;
	private DialogFragment mWelcomeDialog;
	private DialogFragment mAboutDialog;

	private MapSize mMapSize = MapSize.STANDARD;
	private MapType mMapType = MapType.FAIR;
	private Resource[][] mResourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private Harbor[][] mHarborBoard = new Harbor[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private int[][] mProbabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
	private ArrayList<Harbor> mHarborList = new ArrayList<Harbor>();
	private ArrayList<Resource> mResourceList = new ArrayList<Resource>();
	private ArrayList<Integer> mProbabilityList = new ArrayList<Integer>();
	private LinkedHashMap<Integer, List<String>> mPlacementList = new LinkedHashMap<Integer, List<String>>();
	private ArrayList<Integer> mOrderedPlacementList = new ArrayList<Integer>();
	private int mPlacementBookmark = -1;

	///////////////////////////////
	// Fragment method overrides //
	///////////////////////////////
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.map, container, false);
		mMapView = (MapView) view.findViewById(R.id.map_view);
		mRefreshIcon = (ImageView) view.findViewById(R.id.refresh_icon);
		mRefreshIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				asyncMapShuffle();
				GoogleAnalyticsTracker.getInstance().trackPageView(Consts.ANALYTICS_SHUFFLE_MAP);
			}
		});
		mLeftArrowIcon = (ImageView) view.findViewById(R.id.left_arrow);;
		mLeftArrowIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPlacementBookmark = mPlacementBookmark == 0 ? 0 : mPlacementBookmark - 1;
				refreshView();
			}
		});
		mRightArrowIcon = (ImageView) view.findViewById(R.id.right_arrow);;
		mRightArrowIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPlacementBookmark = mPlacementBookmark == mPlacementList.size() - 1 ? mPlacementBookmark : mPlacementBookmark + 1;
				refreshView();
			}
		});
		
		mWelcomeDialog = new WelcomeDialogFragment();
		mAboutDialog = new AboutDialogFragment();
		
		return view;
	}

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(STATE_MAP_SIZE, mMapSize.name());
		outState.putString(STATE_MAP_TYPE, mMapType.name());
		outState.putSerializable(STATE_RESOURCES, mResourceList);
		outState.putSerializable(STATE_PROBABILITIES, mProbabilityList);
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
		if (true) {//savedInstanceState == null && !shownWhatsNew) {
			mWelcomeDialog.show(getFragmentManager(), "WelcomeDialog");
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.putBoolean(SHARED_PREFS_SHOWN_WHATS_NEW, true);
			prefsEditor.commit();
		}
		
		if (savedInstanceState != null) {
			if (savedInstanceState.getString(STATE_MAP_SIZE) != null) {
				mMapSize = MapSize.valueOf(savedInstanceState.getString(STATE_MAP_SIZE));
			} else {
				mMapSize = MapSize.STANDARD;
			}

			if (savedInstanceState.getString(STATE_MAP_TYPE) != null) {
				mMapType = MapType.valueOf(savedInstanceState.getString(STATE_MAP_TYPE));
			} else {
				mMapType = MapType.FAIR;
			}

			if (savedInstanceState.getSerializable(STATE_RESOURCES) != null
					&& savedInstanceState.getSerializable(STATE_PROBABILITIES) != null
					&& savedInstanceState.getSerializable(STATE_HARBORS) != null) {
				mResourceList = (ArrayList<Resource>) savedInstanceState.getSerializable(STATE_RESOURCES);
				mProbabilityList = (ArrayList<Integer>) savedInstanceState.getSerializable(STATE_PROBABILITIES);
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
			asyncMapShuffle();
		}
	}

	//////////////////////////
	// Async generate tasks //
	//////////////////////////
	public void asyncMapShuffle() {
		new ShuffleMapAsyncTask().execute();
	}
	private class ShuffleMapAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			mProbabilityList = MapLogic.getProbabilities(mMapSize, mMapType);
			mResourceList = MapLogic.getResources(mMapSize, mMapType, mProbabilityList);
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
		new ShuffleProbabilitiesAsyncTask().execute();
	}
	private class ShuffleProbabilitiesAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			mProbabilityList = MapLogic.getProbabilities(mMapSize, mMapType, mResourceList);
			fillPlacements();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			refreshView();
		}
	}

	public void asyncHarborsShuffle() {
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
		if (scale != null && scale != 0f) {
			mMapView.setScale(scale);
		}
		mMapView.setMapSize(mMapSize);
		fillResourceProbabilityAndHarbors();

		mMapView.setLandAndWaterResources(mResourceBoard, mHarborBoard);
		mMapView.setProbabilities(mProbabilityBoard);
		mMapView.setHarbors(mHarborList);
		mMapView.setPlacementBookmark(mPlacementBookmark);
		mMapView.setPlacements(mPlacementList);
		mMapView.setOrderedPlacements(mOrderedPlacementList);
		mMapView.invalidate();  // Force refresh
	}

	private void fillResourceProbabilityAndHarbors() {
		mResourceBoard = new Resource[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mProbabilityBoard = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
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
	}

	private void fillPlacements() {
		mPlacementList = PlacementLogic.getBestPlacements(mMapSize, 0 /* all */, mResourceList, mProbabilityList, mHarborList);
		//Log.i(X, "mPlacementList: " + mPlacementList);
		mOrderedPlacementList.clear();
		for (int key : mPlacementList.keySet()) {
			mOrderedPlacementList.add(key);
		}
	}

	////////////////////
	// Menu functions //
	////////////////////
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Set up action items for Action Bar
			menu.findItem(R.id.map_size_item).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.findItem(R.id.map_type_item).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.findItem(R.id.placements_item).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		MenuItem item = menu.findItem(R.id.placements_item);
		if (mPlacementBookmark < 0) {
			item.setTitle(getString(R.string.placements_off));
		} else {
			item.setTitle(getString(R.string.placements_on));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		GoogleAnalyticsTracker analytics = GoogleAnalyticsTracker.getInstance();
		item.setChecked(true);
		switch (item.getItemId()) {
		// MAP TYPE
		case R.id.better_settlers_item:
			if (mMapType != MapType.FAIR) {
				mMapType = MapType.FAIR;
				asyncMapShuffle();
				analytics.trackPageView(String.format(Consts.ANALYTICS_CHANGE_MAP_TYPE_FORMAT, mMapType));
			}
			return true;
		case R.id.traditional_item:
			if (mMapType != MapType.TRADITIONAL) {
				mMapType = MapType.TRADITIONAL;
				asyncMapShuffle();
				analytics.trackPageView(String.format(Consts.ANALYTICS_CHANGE_MAP_TYPE_FORMAT, mMapType));
			}
			return true;
		case R.id.random_item:
			if (mMapType != MapType.RANDOM) {
				mMapType = MapType.RANDOM;
				asyncMapShuffle();
				analytics.trackPageView(String.format(Consts.ANALYTICS_CHANGE_MAP_TYPE_FORMAT, mMapType));
			}
			return true;
		// MAP SIZE
		case R.id.standard_item:
			if (mMapSize != MapSize.STANDARD) {
				mMapSize = MapSize.STANDARD;
				asyncMapShuffle();
				analytics.trackPageView(String.format(Consts.ANALYTICS_CHANGE_MAP_SIZE_FORMAT, mMapSize));
			}
			return true;
		case R.id.large_item:
			if (mMapSize != MapSize.LARGE) {
				mMapSize = MapSize.LARGE;
				asyncMapShuffle();
				analytics.trackPageView(String.format(Consts.ANALYTICS_CHANGE_MAP_SIZE_FORMAT, mMapSize));
			}
			return true;
		case R.id.xlarge_item:
			if (mMapSize != MapSize.XLARGE) {
				mMapSize = MapSize.XLARGE;
				asyncMapShuffle();
				analytics.trackPageView(String.format(Consts.ANALYTICS_CHANGE_MAP_SIZE_FORMAT, mMapSize));
			}
			return true;
		// Placements
		case R.id.placements_item:
			if (mPlacementBookmark < 0) {
				item.setTitle(getString(R.string.placements_on));
				mPlacementBookmark = 0;
				analytics.trackPageView(Consts.ANALYTICS_USE_PLACEMENTS);
				mLeftArrowIcon.setVisibility(View.VISIBLE);
				mRightArrowIcon.setVisibility(View.VISIBLE);
			} else {
				item.setTitle(getString(R.string.placements_off));
				mPlacementBookmark = -1;
				mLeftArrowIcon.setVisibility(View.GONE);
				mRightArrowIcon.setVisibility(View.GONE);
			}
			refreshView();
			return true;
		case R.id.shuffle_probs_item:
			asyncProbsShuffle();
			analytics.trackPageView(Consts.ANALYTICS_SHUFFLE_PROBABILITIES);
			return true;
		case R.id.shuffle_harbors_item:
			asyncHarborsShuffle();
			analytics.trackPageView(Consts.ANALYTICS_SHUFFLE_HARBORS);
			return true;
		case R.id.about_item:
			mAboutDialog.show(getFragmentManager(), "AboutDialogFragment");
			return true;
		case R.id.legal_item:
			(new LegalDialogFragment()).show(getFragmentManager(), "LegalDialogFragment");
			return true;
		default:
			return false;
		}
	}
}