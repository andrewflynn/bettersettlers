package com.nut.bettersettlers.util;

import android.util.Log;

public final class BetterLog {
	private static final String TAG = "BetterSettlers";
	
	private BetterLog() {}
	
	/* VERBOSE */
	public static void v(String fmt, Object... args) {
		v(String.format(fmt, args));
	}
	public static void v(Throwable t, String fmt, Object... args) {
		v(t, String.format(fmt, args));
	}
	public static void v(String msg) {
		if (Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, msg);
	}
	public static void v(Throwable t, String msg) {
		if (Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, msg, t);
	}
	
	/* DEBUG */
	public static void d(String fmt, Object... args) {
		d(String.format(fmt, args));
	}
	public static void d(Throwable t, String fmt, Object... args) {
		d(t, String.format(fmt, args));
	}
	public static void d(String msg) {
		if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, msg);
	}
	public static void d(Throwable t, String msg) {
		if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, msg, t);
	}
	
	/* INFO */
	public static void i(String fmt, Object... args) {
		i(String.format(fmt, args));
	}
	public static void i(Throwable t, String fmt, Object... args) {
		i(t, String.format(fmt, args));
	}
	public static void i(String msg) {
		Log.i(TAG, msg);
	}
	public static void i(Throwable t, String msg) {
		Log.i(TAG, msg, t);
	}
	
	/* WARNING */
	public static void w(String fmt, Object... args) {
		w(String.format(fmt, args));
	}
	public static void w(Throwable t, String fmt, Object... args) {
		w(t, String.format(fmt, args));
	}
	public static void w(String msg) {
		Log.w(TAG, msg);
	}
	public static void w(Throwable t, String msg) {
		Log.w(TAG, msg, t);
	}
	
	/* ERROR */
	public static void e(String fmt, Object... args) {
		e(String.format(fmt, args));
	}
	public static void e(Throwable t, String fmt, Object... args) {
		e(t, String.format(fmt, args));
	}
	public static void e(String msg) {
		Log.e(TAG, msg);
	}
	public static void e(Throwable t, String msg) {
		Log.e(TAG, msg, t);
	}
}
