package com.nut.bettersettlers.ui;

import com.nut.bettersettlers.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class GraphView extends View {
	private static final int[] EXPECTED_FACTOR;
	static {
		EXPECTED_FACTOR = new int[13];
		EXPECTED_FACTOR[0] = -1;
		EXPECTED_FACTOR[1] = -1;
		EXPECTED_FACTOR[2] = 1;
		EXPECTED_FACTOR[3] = 2;
		EXPECTED_FACTOR[4] = 3;
		EXPECTED_FACTOR[5] = 4;
		EXPECTED_FACTOR[6] = 5;
		EXPECTED_FACTOR[7] = 6;
		EXPECTED_FACTOR[8] = 5;
		EXPECTED_FACTOR[9] = 4;
		EXPECTED_FACTOR[10] = 3;
		EXPECTED_FACTOR[11] = 2;
		EXPECTED_FACTOR[12] = 1;
	}

	private int[] mProbs;
	private int[] mRobberProbs;

	private float dBaseX;
	private float dBaseY;
	private float dBaseBuffer;
	private float dBarWidth;
	private float dShadowBarWidth;
	private float dBarBufferWidth;
	private float dPadding;
	private int dTextSize;

	public GraphView(Context context) {
		super(context);

		initialize(context);
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initialize(context);
	}

	private void initialize(Context context) {
		mProbs = new int[13];
		mRobberProbs = new int[13];
		for (int i = 0; i <= 1; i++) {
			// There are no 0 or 1 dice rolls
			mProbs[i] = -1;
			mRobberProbs[i] = -1;
		}
		for (int i = 2; i <= 12; i++) {
			// All others start with 0 rolls
			mProbs[i] = 0;
			mRobberProbs[i] = 0;
		}

		initDimens(context);
	}

	private void initDimens(Context context) {
		dPadding = (float) context.getResources().getDimension(R.dimen.graph_padding);
		dBarBufferWidth = (float) context.getResources().getDimension(R.dimen.graph_bar_buffer);
		dBaseBuffer = (float) context.getResources().getDimension(R.dimen.graph_base_buffer);
		dTextSize = (int) context.getResources().getDimension(R.dimen.graph_font_size);
	}
	
	private void initNewDimens() {
		dBaseX = dPadding;
		dBaseY = getMeasuredHeight() - dPadding;
		dShadowBarWidth = (getMeasuredWidth() - (2 * dPadding) - (10 * dBarBufferWidth)) / 11f;
		dBarWidth = dShadowBarWidth * 2/3;
	}

	public void setProbs(int[] probs) {
		mProbs = probs;
	}

	public void setRobberProbs(int[] robberProbs) {
		mRobberProbs = robberProbs;
	}

	@Override
	public void onDraw(Canvas canvas) {
		initNewDimens();
		
		Paint grayPaint = new Paint();
		grayPaint.setColor(0xFFAAAAAA);

		Paint redPaint = new Paint();
		redPaint.setColor(0xFFFF0000);

		Paint blackPaint = new Paint();
		blackPaint.setColor(0xFF000000);
		blackPaint.setTextSize(dTextSize);
		blackPaint.setAntiAlias(true);
		blackPaint.setTextAlign(Paint.Align.CENTER);
		blackPaint.setTypeface(Typeface.DEFAULT_BOLD);
		blackPaint.setStrokeWidth(2);

		Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(0xFFFFFFFF);
		canvas.drawPaint(backgroundPaint);

		float x = dBaseX + (dShadowBarWidth / 2); // Text at bottom is centered
		float y = dBaseY;
		for (int i = 2; i <= 12; i++) {
			canvas.drawText(Integer.toString(i), x, y, blackPaint);
			x += dShadowBarWidth + dBarBufferWidth;
		}


		x = dBaseX;
		y = dBaseY - dBaseBuffer;
		for (int num : EXPECTED_FACTOR) {
			if (num != -1) {
				float yHeight = (dBaseY - dPadding) / (6f / num);
				canvas.drawRoundRect(new RectF(x, y - yHeight, x + dShadowBarWidth, y), 5.0f, 5.0f, grayPaint);
				x += dShadowBarWidth + dBarBufferWidth;
			}
		}

		// Find the max probability and make this the highest bar and the common multiplier.
		int max = 1;
		for (int i = 0; i < mProbs.length; i++) {
			int candidate = mRobberProbs[i] + mProbs[i];
			if (max < candidate) {
				max = candidate;
			}
		}
		
		float multiplier = (dBaseY - dPadding - dBaseBuffer) / max;

		x = dBaseX;
		y = dBaseY - dBaseBuffer;
		for (int i = 0; i < mProbs.length; i++) {
			int robberNum = mRobberProbs[i];
			int num = mProbs[i];
			int both = robberNum + num;			
			if (robberNum != -1 && num != -1) {
				// In order to have rounded only on the bottom for one and top for other if we have both,
				// we overlap each one just short (5 pixels) of the point where they connect
				if (robberNum > 0 && num > 0) {
					canvas.drawRoundRect(new RectF(x, y - (robberNum * multiplier) + 5, x + dBarWidth, y), 5.0f, 5.0f, redPaint);
					canvas.drawRect(new RectF(x, y - (robberNum * multiplier), x + dBarWidth, y - 5), redPaint);
					canvas.drawRoundRect(new RectF(x, y - (both * multiplier), x + dBarWidth, y - (robberNum * multiplier) - 5), 5.0f, 5.0f, blackPaint);
					canvas.drawRect(new RectF(x, y - (both * multiplier) + 5, x + dBarWidth, y - (robberNum * multiplier)), blackPaint);
					canvas.drawText(Integer.toString(both), x + (dShadowBarWidth / 2), y - (both * multiplier) - dBarBufferWidth, blackPaint);
				} else {
					canvas.drawRoundRect(new RectF(x, y - (robberNum * multiplier), x + dBarWidth, y), 5.0f, 5.0f, redPaint);
					canvas.drawRoundRect(new RectF(x, y - (both * multiplier), x + dBarWidth, y - (robberNum * multiplier)), 5.0f, 5.0f, blackPaint);
					canvas.drawText(Integer.toString(both), x + (dShadowBarWidth / 2), y - (both * multiplier) - dBarBufferWidth, blackPaint);
				}
				x += dShadowBarWidth + dBarBufferWidth;
			}
		}
	}
}
