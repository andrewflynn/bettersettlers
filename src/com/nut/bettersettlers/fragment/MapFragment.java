package com.nut.bettersettlers.fragment;

import static com.nut.bettersettlers.data.MapConsts.BOARD_RANGE_X;
import static com.nut.bettersettlers.data.MapConsts.BOARD_RANGE_Y;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.nut.bettersettlers.data.MapConsts.Harbor;
import com.nut.bettersettlers.data.MapConsts.MapType;
import com.nut.bettersettlers.data.MapConsts.Resource;
import com.nut.bettersettlers.data.MapProvider;
import com.nut.bettersettlers.data.MapProvider.MapSize;
import com.nut.bettersettlers.fragment.dialog.MapsDialogFragment;
import com.nut.bettersettlers.fragment.dialog.WelcomeDialogFragment;
import com.nut.bettersettlers.logic.MapLogic;
import com.nut.bettersettlers.logic.PlacementLogic;
import com.nut.bettersettlers.misc.Consts;
import com.nut.bettersettlers.ui.MapView;

public class MapFragment extends Fragment {
	private static final String X = "BetterSettlers";
	
	private static final String STATE_MAP_SIZE = "MAP_SIZE";
	private static final String STATE_MAP_TYPE = "MAP_TYPE";
	private static final String STATE_RESOURCES = "MAP_RESOURCES";
	private static final String STATE_RESOURCES_BYTES = "MAP_RESOURCES_BYTES";
	private static final String STATE_UNKNOWNS = "UNKNOWNS";
	private static final String STATE_UNKNOWNS_BYTES = "UNKNOWNS_BTYES";
	private static final String STATE_PROBABILITIES = "PROBABILITIES";
	private static final String STATE_UNKNOWN_PROBABILITIES = "UNKNOWN_PROBABILITIES";
	private static final String STATE_HARBORS = "HARBORS";
	private static final String STATE_HARBORS_BYTES = "HARBORS_BYTES";
	private static final String STATE_PLACEMENTS = "PLACEMENTS";
	private static final String STATE_PLACEMENTS_BYTES = "PLACEMENTS_BYTES";
	private static final String STATE_ORDERED_PLACEMENTS = "ORDERED_PLACEMENTS";
	private static final String STATE_PLACEMENT_BOOKMARK = "PLACEMENT_BOOKMARK";
	private static final String STATE_ZOOM_LEVEL = "ZOOM_LEVEL";

	private static final String SHARED_PREFS_NAME = "Map";
	private static final String SHARED_PREFS_SHOWN_WHATS_NEW = "ShownWhatsNewVersion19";
	
	private MapView mMapView;
	
	private ImageView mSettingsButton;
	private ImageView mPlacementsButton;
	private ImageView mPlacementsLeftButton;
	private ImageView mPlacementsRightButton;
	private LinearLayout mPlacementsContainer;
	private ImageView mRefreshButton;
	private ImageView mRefreshDownButton;

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
	
	private boolean mShowSeafarers = false;

	///////////////////////////////
	// Fragment method overrides //
	///////////////////////////////
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.map, container, false);
		mMapView = (MapView) view.findViewById(R.id.map_view);
		
		mSettingsButton = (ImageView) view.findViewById(R.id.settings_button);
		mSettingsButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    ft.addToBackStack(null);
		    	MapsDialogFragment.newInstance().show(ft, "MapsDialogFragment");
			}
		});
		
		mPlacementsButton = (ImageView) view.findViewById(R.id.placements_button);
		mPlacementsButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
		    	if (showingPlacements()) {
		    		hidePlacements(true);
		    	} else {
		    		showPlacements(true);
					((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_USE_PLACEMENTS);
		    	}
			}
		});
		
		mPlacementsContainer = (LinearLayout) view.findViewById(R.id.placements_container);
		
		mPlacementsLeftButton = (ImageView) view.findViewById(R.id.placements_left_button);
		mPlacementsLeftButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				prevPlacement();
			}
		});
		
		mPlacementsRightButton = (ImageView) view.findViewById(R.id.placements_right_button);
		mPlacementsRightButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				nextPlacement();
			}
		});
		
		mRefreshButton = (ImageView) view.findViewById(R.id.refresh_button);
		mRefreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				asyncMapShuffle();
				((MainActivity) getActivity()).getAnalytics().trackPageView(Consts.ANALYTICS_SHUFFLE_MAP);
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

	private byte[] toBytes(Serializable resources) {
		byte[] bytes = null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(resources);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return bytes;
	}
	
	private Object fromBytes(byte[] bytes) {
		Object object = null;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			object = in.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return object;
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
		
		Bundle instanceState = savedInstanceState;
		
		Intent startIntent = getActivity().getIntent();
		if (startIntent != null && startIntent.getAction().equals(Consts.LAUNCH_MAP_ACTION)) {
			instanceState = startIntent.getExtras();
			
			// From bytes
			byte[] resourceBytes = instanceState.getByteArray(STATE_RESOURCES_BYTES);
			if (resourceBytes != null) {
			    instanceState.putSerializable(STATE_RESOURCES, (Serializable) fromBytes(resourceBytes));
			    instanceState.remove(STATE_RESOURCES_BYTES);
			}

			byte[] unknownsBytes = instanceState.getByteArray(STATE_UNKNOWNS_BYTES);
			if (unknownsBytes != null) {
    			instanceState.putSerializable(STATE_UNKNOWNS, (Serializable) fromBytes(unknownsBytes));
	    		instanceState.remove(STATE_UNKNOWNS_BYTES);
			}

			byte[] harborsBytes = instanceState.getByteArray(STATE_HARBORS_BYTES);
			if (harborsBytes != null) {
    			instanceState.putSerializable(STATE_HARBORS, (Serializable) fromBytes(harborsBytes));
		    	instanceState.remove(STATE_HARBORS_BYTES);
			}

			byte[] placementsBytes = instanceState.getByteArray(STATE_PLACEMENTS_BYTES);
			if (placementsBytes != null) {
    			instanceState.putSerializable(STATE_PLACEMENTS, (Serializable) fromBytes(placementsBytes));
    			instanceState.remove(STATE_PLACEMENTS_BYTES);
			}
		}
		
		if (instanceState != null) {
			if (instanceState.getString(STATE_MAP_SIZE) != null) {
				mMapSize = getMapProvider().getMap(instanceState.getString(STATE_MAP_SIZE));
			} else {
				mMapSize = getMapProvider().getMap(MapSize.STANDARD);
			}

			if (instanceState.getString(STATE_MAP_TYPE) != null) {
				mMapType = MapType.valueOf(instanceState.getString(STATE_MAP_TYPE));
			} else {
				mMapType = MapType.FAIR;
			}

			if (instanceState.getSerializable(STATE_RESOURCES) != null
					&& instanceState.getSerializable(STATE_UNKNOWNS) != null
					&& instanceState.getSerializable(STATE_PROBABILITIES) != null
					&& instanceState.getSerializable(STATE_UNKNOWN_PROBABILITIES) != null
					&& instanceState.getSerializable(STATE_HARBORS) != null) {
				mResourceList = (ArrayList<Resource>) instanceState.getSerializable(STATE_RESOURCES);
				mUnknownsList = (ArrayList<Resource>) instanceState.getSerializable(STATE_UNKNOWNS);
				mProbabilityList = (ArrayList<Integer>) instanceState.getSerializable(STATE_PROBABILITIES);
				mUnknownProbabilitiesList = (ArrayList<Integer>) instanceState.getSerializable(STATE_UNKNOWN_PROBABILITIES);
				mHarborList = (ArrayList<Harbor>) instanceState.getSerializable(STATE_HARBORS);
				if (instanceState.getSerializable(STATE_PLACEMENTS) != null
						&& instanceState.getSerializable(STATE_ORDERED_PLACEMENTS) != null
						&& instanceState.getSerializable(STATE_PLACEMENT_BOOKMARK) != null) {
					mPlacementList = (LinkedHashMap<Integer, List<String>>) instanceState.getSerializable(STATE_PLACEMENTS);
					mOrderedPlacementList = (ArrayList<Integer>) instanceState.getSerializable(STATE_ORDERED_PLACEMENTS);
					mPlacementBookmark = (Integer) instanceState.getSerializable(STATE_PLACEMENT_BOOKMARK);
				}
				if (instanceState.getSerializable(STATE_ZOOM_LEVEL) != null) {
					refreshView((Float) instanceState.getSerializable(STATE_ZOOM_LEVEL));
				} else {
					refreshView();
				}
			}
		} else {
			mMapSize = getMapProvider().getMap(MapSize.STANDARD);
			asyncMapShuffle();
		}
	}
    
    private void showProgressBar() {
    	if (mRefreshButton.getVisibility() == View.VISIBLE) {
    		mRefreshDownButton.setVisibility(View.VISIBLE);
    		mRefreshButton.setVisibility(View.INVISIBLE);
    	}
    }
    
    private void killProgressBar() {
    	if (mRefreshDownButton.getVisibility() == View.VISIBLE) {
    		mRefreshDownButton.setVisibility(View.INVISIBLE);
    		mRefreshButton.setVisibility(View.VISIBLE);
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
	public void asyncMapShuffle() {
		if (getActivity() != null) {
			showProgressBar();
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
			showProgressBar();
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
			showProgressBar();
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
			killProgressBar();
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
	
	public boolean getShowSeafarers() {
		return mShowSeafarers;
	}

	public void setShowSeafarers(boolean showSeafarers) {
		mShowSeafarers = showSeafarers;
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
	
	public void sizeChoice(MapSize mapSize) {
		CatanMap size = getMapProvider().getMap(mapSize);
		
		// Only Settlers maps can have traditional
		if (mMapType == MapType.TRADITIONAL
				&& (mapSize != MapSize.STANDARD && mapSize != MapSize.LARGE && mapSize != MapSize.XLARGE)) {
			mMapType = MapType.FAIR;
		}
		
		if (mMapSize != size) {
			mMapSize = size;
			asyncMapShuffle();
			MainActivity mainActivity = (MainActivity) getActivity();
			mainActivity.setTitleButtonText(mapSize.titleDrawableId);
			mainActivity.getAnalytics().trackPageView(
					String.format(Consts.ANALYTICS_CHANGE_MAP_SIZE_FORMAT, mMapSize.getName()));
		}
	}
	
	public void standardChoice() {
		sizeChoice(MapSize.STANDARD);
	}
	
	public void largeChoice() {
		sizeChoice(MapSize.LARGE);
	}
	
	public void xlargeChoice() {
		sizeChoice(MapSize.XLARGE);
	}
	
	public void headingForNewShoresChoice() {
		sizeChoice(MapSize.HEADING_FOR_NEW_SHORES);
	}
	
	public void headingForNewShoresExpChoice() {
		sizeChoice(MapSize.HEADING_FOR_NEW_SHORES_EXP);
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