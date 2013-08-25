package com.nut.bettersettlers.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;

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
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.vending.billing.IInAppBillingService;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.MapSize;
import com.nut.bettersettlers.fragment.GraphFragment;
import com.nut.bettersettlers.fragment.MapFragment;
import com.nut.bettersettlers.fragment.dialog.AboutDialogFragment;
import com.nut.bettersettlers.fragment.dialog.FogIslandHelpDialogFragment;
import com.nut.bettersettlers.iab.IabConsts;
import com.nut.bettersettlers.iab.MapContainer;
import com.nut.bettersettlers.iab.Purchase;
import com.nut.bettersettlers.iab.Security;
import com.nut.bettersettlers.iab.SkuDetails;
import com.nut.bettersettlers.util.Analytics;
import com.nut.bettersettlers.util.BetterLog;
import com.nut.bettersettlers.util.Consts;

public class MainActivity extends FragmentActivity {
	private static final String STATE_SHOW_GRAPH = "STATE_SHOW_GRAPH";
	private static final String STATE_SHOW_PLACEMENTS = "STATE_SHOW_PLACEMENTS";
	private static final String STATE_THEFT_ORDER = "STATE_THEFT_ORDER";
	private static final String STATE_EXP_THEFT_ORDER = "STATE_EXP_THEFT_ORDER";
	private static final String STATE_TITLE_ID = "STATE_TITLE_ID";
	
	private static final Bundle GET_PRICES_BUNDLE;
	static {
		ArrayList<String> priceItems = new ArrayList<String>();
		priceItems.add("new_world"); // Any normal map should do
		priceItems.add(IabConsts.BUY_ALL);
		
		GET_PRICES_BUNDLE = new Bundle();
		GET_PRICES_BUNDLE.putStringArrayList(IabConsts.GET_SKU_DETAILS_ITEM_LIST, priceItems);
	}

	public static final int BUY_INTENT_REQUEST_CODE = 101;
	
	private MapFragment mMapFragment;
	private GraphFragment mGraphFragment;

	private ImageView mTitle;
	private int mTitleId;
	private ImageView mInfoButton;
	
	private Set<String> mOwnedMaps = new HashSet<String>();
	
	// Stupid onActivityResult is called before onStart()
	// http://stackoverflow.com/q/10114324/452383
	// https://code.google.com/p/android/issues/detail?id=17787
	private boolean mShowFogIsland = false;
	
	private String mSinglePrice = null;
	private String mBuyAllPrice = null;
	
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
                int response = mService.isBillingSupported(IabConsts.API_VERSION, getPackageName(), IabConsts.ITEM_TYPE_INAPP);
                if (response == IabConsts.BILLING_RESPONSE_RESULT_OK) {
                	BetterLog.w("IAB v" + IabConsts.API_VERSION + " is supported");
    				mMapFragment.setShowSeafarers(true);
                	new InitIabTask().execute();
                } else {
                	BetterLog.w("IAB v" + IabConsts.API_VERSION + " not supported");
    				mMapFragment.setShowSeafarers(true);
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
				setPrices();
			} catch (RemoteException e) {
				BetterLog.w("RemoteException ", e);
			}
			
			return null;
		}
		
		private void restoreTransactions() throws RemoteException {
			BetterLog.d("RestoreTransactions");
	    	Bundle ownedItems = mService.getPurchases(IabConsts.API_VERSION, getPackageName(),
	    			IabConsts.ITEM_TYPE_INAPP, null);
	        
	        int response = getResponseCodeFromBundle(ownedItems);;
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
   	        	verifyAndAddPurchase(purchaseDataList.get(i), signatureList.get(i), true /* restore */);
	        }
		}
	}
	
	private void setPrices() throws RemoteException {
		Bundle details = mService.getSkuDetails(IabConsts.API_VERSION, getPackageName(),
				IabConsts.ITEM_TYPE_INAPP, GET_PRICES_BUNDLE);

        if (!details.containsKey(IabConsts.RESPONSE_GET_SKU_DETAILS_LIST)) {
        	BetterLog.d("Could not fetch prices");
        	return;
        }

        ArrayList<String> responseList = details.getStringArrayList(IabConsts.RESPONSE_GET_SKU_DETAILS_LIST);
        
        for (String response : responseList) {
        	SkuDetails item;
			try {
				item = new SkuDetails(response);
			} catch (JSONException e) {
				BetterLog.w("Could not parse JSON response for finding prices");
				return;
			}
        	
        	if (IabConsts.BUY_ALL.equals(item.sku)) {
        		mBuyAllPrice = item.price;
        	} else {
        		mSinglePrice = item.price;
        	}
        }
	}
	
	public String getSinglePrice() {
		return mSinglePrice;
	}
	
	public String getBuyAllPrice() {
		return mBuyAllPrice;
	}

	private void consumePurchase(Purchase purchase) {
		BetterLog.d("ConsumePurchase");
		try {
			mService.consumePurchase(IabConsts.API_VERSION, getPackageName(), purchase.token);
		} catch (RemoteException e) {
			BetterLog.e("Could not consume purchase");
			return;
		}
	}
	
	private void verifyAndAddPurchase(String purchaseData, String signature, boolean restore) {
		BetterLog.d("VerifyAndAddPurchase: " + restore);
		if (Security.verifyPurchase(purchaseData, signature)) {
			BetterLog.d("Passed security");
			Purchase purchase;
			try {
				purchase = new Purchase(purchaseData, signature);
			} catch (JSONException e) {
				BetterLog.e("Unable to parse returned JSON. Not adding item");
				return;
			}
			
			// If it's prod, ID=SKU
			// If we're testing, ID is stored in devPayload
    		String itemId = Consts.TEST_STATIC_IAB ? purchase.developerPayload : purchase.sku;
			BetterLog.d("Purchasing " + itemId);
    		
			mOwnedMaps.add(itemId);

    		// If we're testing IAB, consume all purchases immediately so we can test infinitely
        	if (Consts.TEST_CONSUME_ALL_PURCHASES) {
        		consumePurchase(purchase);
        	}
			
			// If we're restoring all purchases, don't do anything special
			if (restore) {
				return;
			}
			
			// Else show the map they chose, and maybe Fog Island help
			if (getMapFragment() != null && !itemId.equals(IabConsts.BUY_ALL)) {
				getMapFragment().sizeChoice(MapSize.getMapSizeByProductId(itemId));
			}
    	}
	}
	
	public void purchaseItem(MapContainer map) {
		purchaseItem(map.id);
	}
	
	public void purchaseItem(String id) {
		BetterLog.i("Buying " + id);
		
		// If it's prod, use real ID and no devPayload
		// If we're testing, use fake product id and store ID in devPayload
		String sku = Consts.TEST_STATIC_IAB ? IabConsts.FAKE_PRODUCT_ID : id;
		String devPayload = Consts.TEST_STATIC_IAB ? id : null;
		
    	Bundle buyIntentBundle;
    	try {
			buyIntentBundle = mService.getBuyIntent(IabConsts.API_VERSION, getPackageName(), sku,
					IabConsts.ITEM_TYPE_INAPP, devPayload);
		} catch (RemoteException e) {
			BetterLog.w("RemoteException", e);
			return;
		}

		int response = getResponseCodeFromBundle(buyIntentBundle);
		if (response != IabConsts.BILLING_RESPONSE_RESULT_OK) {
			BetterLog.e("Bad response: " + response);
			return;
		}
		
		PendingIntent buyIntent = buyIntentBundle.getParcelable(IabConsts.RESPONSE_BUY_INTENT);
		if (buyIntent == null) {
			BetterLog.w("Item has already been purchased");
			return;
		}
		
		try {
			startIntentSenderForResult(buyIntent.getIntentSender(), BUY_INTENT_REQUEST_CODE,
					new Intent(), 0, 0, 0);
		} catch (SendIntentException e) {
			BetterLog.e("Error sending intent", e);
			return;
		}
	}

	
	// Workaround to bug where sometimes response codes come as Long instead of Integer
	private int getResponseCodeFromBundle(Bundle b) {
        Object o = b.get(IabConsts.RESPONSE_CODE);
        if (o == null) {
            BetterLog.i("Bundle with null response code, assuming OK (known issue)");
            return IabConsts.BILLING_RESPONSE_RESULT_OK;
        } else if (o instanceof Integer) {
        	return ((Integer)o).intValue();
        } else if (o instanceof Long) {
        	return (int)((Long)o).longValue();
        } else {
        	BetterLog.e("Unexpected type for bundle response code.");
        	BetterLog.e(o.getClass().getName());
            throw new RuntimeException("Unexpected type for bundle response code: " + o.getClass().getName());
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
            	verifyAndAddPurchase(purchaseData, dataSignature, false /* restore */);
            } else if (resultCode == RESULT_CANCELED) {
            	BetterLog.w("Purchase canceled");
            } else {
            	BetterLog.w("Purchase failed");
            }
    		break;
    	}
    }

	/** Called when the activity is going to disappear. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		BetterLog.i("MainActivity.onSaveInstanceState");
		
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
		super.onSaveInstanceState(outState);
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
				trackEvent(Analytics.CATEGORY_MAIN, Analytics.ACTION_BUTTON, Analytics.INFO);
			}
		});

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

		// IAB
		bindService(new Intent(IabConsts.BIND_ACTION), mServiceConn, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// Google Analytics
	    if (Consts.TEST_FAST_ANALYTICS) {
	    	GoogleAnalytics.getInstance(this).setDebug(true);
	    	GAServiceManager.getInstance().setDispatchPeriod(5);
	    }
	}
	
	@Override
	public void onResumeFragments() {
		super.onResumeFragments();
		
		if (mShowFogIsland) {
			mShowFogIsland = false;

			SharedPreferences prefs = getSharedPreferences(Consts.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
			boolean shownWhatsNew = prefs.getBoolean(Consts.SHARED_PREFS_KEY_FOG_ISLAND_HELP, false);
			if (!shownWhatsNew) {
				FogIslandHelpDialogFragment.newInstance().show(getSupportFragmentManager(), "TheFogIslandHelpDialog");
				SharedPreferences.Editor prefsEditor = prefs.edit();
				prefsEditor.putBoolean(Consts.SHARED_PREFS_KEY_FOG_ISLAND_HELP, true);
				prefsEditor.commit();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// IAB
		if (mService != null) {
			unbindService(mServiceConn);
			mService = null;
		}
	}
    
    public void trackEvent(String category, String action, String label) {
    	GoogleAnalytics.getInstance(this).getTracker(Analytics.ID).sendEvent(category, action, label, null);
    }
    
    public void trackView(String view) {
		GoogleAnalytics.getInstance(this).getTracker(Analytics.ID).sendView(view);
    }
    
    public void setTitleButtonText(final int resId) {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
		    	mTitle.setBackgroundResource(resId);
			}
		});
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

		
		trackView(Analytics.VIEW_ROLL_TRACKER);
    }
}