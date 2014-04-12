package com.nut.bettersettlers.data.maps;

import android.content.Context;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.CatanMap;

public class NewWorld extends CatanMapProvider {
    @Override
    public CatanMap create() {
    	// New world needs context to initialize
    	return null;
    }
    
    @Override
    public CatanMap create(Context context) {
    	return CatanMapGenerator.generateFromJson(context.getResources().openRawResource(R.raw.new_world));
    }
}
