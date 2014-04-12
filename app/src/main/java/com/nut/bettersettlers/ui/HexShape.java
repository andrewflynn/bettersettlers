package com.nut.bettersettlers.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

public class HexShape extends Shape {
	private final Path mPath;
	
	public HexShape() {
		mPath = new Path();
	}
	
	public void setPath(float x, float y1, float y2) {
		mPath.reset();
		mPath.moveTo(x, 0);
		mPath.lineTo(2 * x, y2);
		mPath.lineTo(2 * x, y1 + y2);
		mPath.lineTo(x, y2 + y1 + y2);
		mPath.lineTo(0, y1 + y2);
		mPath.lineTo(0, y2);
		mPath.lineTo(x, 0);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawPath(mPath, paint);
	}
}
