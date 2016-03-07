package com.nut.bettersettlers.util;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.util.SparseArray;

public final class Util {
	public static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}
	
	public static int hashCode(Object... objects) {
		return Arrays.hashCode(objects);
	}
	
	public static Bundle sparseArrayArrayListToBundle(SparseArray<ArrayList<String>> array) {
		Bundle bundle = new Bundle();
		for (int i = 0; i < array.size(); i++) {
			bundle.putStringArrayList(Integer.toString(array.keyAt(i)), array.valueAt(i));
		}
		return bundle;
	}
	
	public static SparseArray<ArrayList<String>> bundleToSparseArrayArrayList(Bundle bundle) {
		SparseArray<ArrayList<String>> array = new SparseArray<>(bundle.size());
		for (String key : bundle.keySet()) {
			array.put(Integer.parseInt(key), bundle.getStringArrayList(key));
		}
		return array;
	}
}
