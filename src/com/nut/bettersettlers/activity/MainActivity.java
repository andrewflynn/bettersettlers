package com.nut.bettersettlers.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.MapSize;
import com.nut.bettersettlers.fragment.GraphFragment;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.fragment.dialog.AboutDialogFragment;
import com.nut.bettersettlers.fragment.dialog.FogIslandHelpDialogFragment;
import com.nut.bettersettlers.iab.IabConsts;
import com.nut.bettersettlers.iab.MapContainer;
import com.nut.bettersettlers.iab.Security;
import com.nut.bettersettlers.util.BetterLog;
import com.nut.bettersettlers.util.Consts;

public class MainActivity extends FragmentActivity {
	private static final String STATE_SHOW_GRAPH = "STATE_SHOW_GRAPH";
	private static final String STATE_SHOW_PLACEMENTS = "STATE_SHOW_PLACEMENTS";
	private static final String STATE_THEFT_ORDER = "STATE_THEFT_ORDER";
	private static final String STATE_EXP_THEFT_ORDER = "STATE_EXP_THEFT_ORDER";
	private static final String STATE_TITLE_ID = "STATE_TITLE_ID";
	
	private static final String SHARED_PREFS_THE_FOG_ISLAND_NAME = "Seafarers";
	private static final String SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP = "TheFogIsland";

	public static final int BUY_INTENT_REQUEST_CODE = 101;
	
	private MapFragment mMapFragment;
	private GraphFragment mGraphFragment;

	private ImageView mTitle;
	private int mTitleId;
	private ImageView mInfoButton;
	
	private WakeLock mWakeLock;
	private GoogleAnalyticsTracker mAnalytics;
	
	private Set<String> mOwnedMaps = new HashSet<String>();
	
	IInAppBillingService mService;
	private ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
            try {
                int response = mService.isBillingSupported(Consts.IAB_API_VERSION, getPackageName(), IabConsts.ITEM_TYPE_INAPP);
                if (response == IabConsts.BILLING_RESPONSE_RESULT_OK) {
    				mMapFragment.setShowSeafarers(true);
                	new InitIabTask().execute();
                } else {
                	BetterLog.w("IAB v" + Consts.IAB_API_VERSION + " not supported");
    				mMapFragment.setShowSeafarers(false);
                }
            } catch (RemoteException e) {
            	BetterLog.i("Exception while querying for IABv3 support.");
            }
		}
	};
	
	private class InitIabTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				restoreTransactions();
			} catch (RemoteException e) {
				BetterLog.w("RemoteException ", e);
			}
			
			return null;
		}
		
		private void restoreTransactions() throws RemoteException {
	    	Bundle ownedItems = mService.getPurchases(Consts.IAB_API_VERSION, getPackageName(), IabConsts.ITEM_TYPE_INAPP, null);
	        
	        int response = ownedItems.getInt(IabConsts.RESPONSE_CODE);
	        BetterLog.d("Owned items response: " + String.valueOf(response));
	        if (response != IabConsts.BILLING_RESPONSE_RESULT_OK
	                || !ownedItems.containsKey(IabConsts.RESPONSE_INAPP_ITEM_LIST)
	                || !ownedItems.containsKey(IabConsts.RESPONSE_INAPP_PURCHASE_DATA_LIST)
	                || !ownedItems.containsKey(IabConsts.RESPONSE_INAPP_SIGNATURE_LIST)) {
	        	BetterLog.w("Error querying owned items. Response: " + response);
	        	return;
	        }

	        List<String> purchaseDataList = ownedItems.getStringArrayList(IabConsts.RESPONSE_INAPP_PURCHASE_DATA_LIST);
	        List<String> signatureList = ownedItems.getStringArrayList(IabConsts.RESPONSE_INAPP_SIGNATURE_LIST);
	        
	        for (int i = 0; i < purchaseDataList.size(); i++) {
	        	verifyAndAddPurchase(purchaseDataList.get(i), signatureList.get(i));
	        }
		}
	}
	
	private void verifyAndAddPurchase(String purchaseData, String signature) {
    	ArrayList<Security.VerifiedPurchase> verifiedPurchaseDataList =
    		Security.verifyPurchase(purchaseData, signature);
    	for (Security.VerifiedPurchase verifiedPurchaseData : verifiedPurchaseDataList) {
    		String itemId = verifiedPurchaseData.productId;
    		
			mOwnedMaps.add(itemId);
			if (getMapFragment() != null && !itemId.equals(IabConsts.BUY_ALL)) {
				getMapFragment().sizeChoice(MapSize.getMapSizeByProductId(itemId));
			}
			
			if ("seafarers.the_fog_island".equals(itemId)) {
				SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_THE_FOG_ISLAND_NAME, Context.MODE_PRIVATE);
				boolean shownWhatsNew = prefs.getBoolean(SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP, false);
				if (!shownWhatsNew) {
					FogIslandHelpDialogFragment.newInstance().show(getSupportFragmentManager(), "TheFogIslandHelpDialog");
					SharedPreferences.Editor prefsEditor = prefs.edit();
					prefsEditor.putBoolean(SHARED_PREFS_SHOWN_THE_FOG_ISLAND_HELP, true);
					prefsEditor.commit();
				}
			}
    	}
	}

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if (mGraphFragment.isVisible()) {
			outState.putBoolean(STATE_SHOW_GRAPH, true);
		} else if (mMapFragment.isVisible()) {
			if (mMapFragment.showingPlacements()) {
				outState.putBoolean(STATE_SHOW_PLACEMENTS, true);
			}
		}
		
		if (mMapFragment != null && mMapFragment.getCatanMap().theftOrder != null
				&& !mMapFragment.getCatanMap().theftOrder.isEmpty()) {
			if (mMapFragment.getCatanMap().name.equals("new_world")) {
				outState.putIntegerArrayList(STATE_THEFT_ORDER, mMapFragment.getCatanMap().theftOrder);
			}
			if (mMapFragment.getCatanMap().name.equals("new_world_exp")) {
				outState.putIntegerArrayList(STATE_EXP_THEFT_ORDER, mMapFragment.getCatanMap().theftOrder);
			}
		}
		
		outState.putInt(STATE_TITLE_ID, mTitleId);
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		mGraphFragment = (GraphFragment) getSupportFragmentManager().findFragmentById(R.id.graph_fragment);

		mTitle = (ImageView) findViewById(R.id.title_text);
		if (savedInstanceState != null && savedInstanceState.getInt(STATE_TITLE_ID) != 0) {
			setTitleButtonText(savedInstanceState.getInt(STATE_TITLE_ID));
		} else {
			setTitleButtonText(MapSize.STANDARD.titleDrawableId);
		}
		
		mInfoButton = (ImageView) findViewById(R.id.info_button);
		mInfoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutDialogFragment.newInstance().show(getSupportFragmentManager(), "AboutDialog");
			}
		});
		
		mAnalytics = GoogleAnalyticsTracker.getInstance();
		mAnalytics.start(Consts.ANALYTICS_KEY, Consts.ANALYTICS_INTERVAL, this);
		
		mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MapActivityWakeLock");

		boolean showGraph = false;
		boolean showPlacements = false;
		if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_SHOW_GRAPH)) {
			showGraph = true;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_SHOW_PLACEMENTS)) {
			showPlacements = true;
		}

		if (showGraph) {
			showGraphFragment();
		} else {
			showMapFragment();
			if (showPlacements) {
				getMapFragment().showPlacements(false);
			} else {
				getMapFragment().hidePlacements(false);
			}
		}
		
		mOwnedMaps.add(MapContainer.HEADING_FOR_NEW_SHORES.id);
	}
	
	@Override
	public void onStart() {
		super.onStart();

		// IAB
		bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
				mServiceConn, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		// IAB
		if (mService != null) {
			unbindService(mServiceConn);
			mService = null;
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mAnalytics.stop();
	}
	
	public void purchaseItem(MapContainer map) {
		purchaseItem(map.id);
	}
	
	public void purchaseItem(String id) {
		BetterLog.i("Buying " + id);
		if (Consts.TEST){
			mOwnedMaps.add(id);
			// TODO(flynn): Fake purchase
        } else {
        	Bundle buyIntentBundle;
        	try {
				buyIntentBundle = mService.getBuyIntent(Consts.IAB_API_VERSION, getPackageName(), id,
						IabConsts.ITEM_TYPE_INAPP, null);
			} catch (RemoteException e) {
				BetterLog.w("RemoteException", e);
				return;
			}
			
			long response = buyIntentBundle.getLong(IabConsts.RESPONSE_CODE);
			if (response != IabConsts.BILLING_RESPONSE_RESULT_OK) {
				BetterLog.e("Bad response: " + response);
				return;
			}
			
			PendingIntent buyIntent = buyIntentBundle.getParcelable(IabConsts.RESPONSE_BUY_INTENT);
			try {
				startIntentSenderForResult(buyIntent.getIntentSender(), BUY_INTENT_REQUEST_CODE,
						new Intent(), 0, 0, 0);
			} catch (SendIntentException e) {
				BetterLog.e("Error sending intent", e);
				return;
			}
        }
	}
	

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    	case BUY_INTENT_REQUEST_CODE:
    		// Verify/consume the purchase
    		if (data == null) {
    			BetterLog.w("Null data. Returning");
    			return;
    		}
        	
            String purchaseData = data.getStringExtra(IabConsts.RESPONSE_INAPP_PURCHASE_DATA);
            String dataSignature = data.getStringExtra(IabConsts.RESPONSE_INAPP_SIGNATURE);
            
            if (resultCode == RESULT_OK) {
            	BetterLog.d("Successful return code from purchase activity.");
            	verifyAndAddPurchase(purchaseData, dataSignature);
            } else if (resultCode == RESULT_CANCELED) {
            	BetterLog.w("Purchase canceled");
            } else {
            	BetterLog.w("Purchase failed");
            }
    		break;
    	}
    }
    
    public GoogleAnalyticsTracker getAnalytics() {
    	return mAnalytics;
    }
    
    public void setTitleButtonText(int resId) {
    	mTitle.setBackgroundResource(resId);
    	mTitleId = resId;
    }
    
    public Set<String> getOwnedMaps() {
    	return mOwnedMaps;
    }
    
    public MapFragment getMapFragment() {
    	return (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
    }
    
    public GraphFragment getGraphFragment() {
    	return (GraphFragment) getSupportFragmentManager().findFragmentById(R.id.graph_fragment);
    }
    
    public void showMapFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//ft.setCustomAnimations(R.anim.long_slide_in_left, R.anim.long_slide_out_right);
		ft.hide(mGraphFragment);
		ft.show(mMapFragment);
		ft.commit();
    }
    
    public void showGraphFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//ft.setCustomAnimations(R.anim.long_slide_in_right, R.anim.long_slide_out_left);
		ft.hide(mMapFragment);
		ft.show(mGraphFragment);
		ft.addToBackStack("GraphFragment");
		ft.commit();
    }
}