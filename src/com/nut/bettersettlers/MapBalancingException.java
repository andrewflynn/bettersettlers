package com.nut.bettersettlers;

public class MapBalancingException extends Exception {
	public MapBalancingException() {}
	public MapBalancingException(String msg) { super(msg); }
	public MapBalancingException(Throwable e) { super(e); }
	public MapBalancingException(String msg, Throwable e) { super(msg, e); }
}
