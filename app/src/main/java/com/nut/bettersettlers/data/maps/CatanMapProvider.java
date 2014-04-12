package com.nut.bettersettlers.data.maps;

import android.content.Context;

import com.nut.bettersettlers.data.CatanMap;

public abstract class CatanMapProvider {
	public CatanMap mMap;
	public CatanMap get() {
		if (mMap == null) {
			mMap = create();
		}
		return mMap;
	}
	public void refresh(Context context) {
		mMap = create(context);
	}
	
	// Used by most maps
    protected CatanMap create() {
    	return null;
    }
    
    // Only used by NEW_WORLD
    protected CatanMap create(Context context) {
    	return null;
    }
}
