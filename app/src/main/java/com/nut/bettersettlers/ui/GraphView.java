package com.nut.bettersettlers.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.nut.bettersettlers.R;

public class GraphView extends View {
	private static final int[] EXPECTED_FACTOR;
	private static final RectF RECT;
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
		
		RECT = new RectF();
	}

	private static final Paint GRAY_PAINT;
	private static final Paint RED_PAINT;
	private static final Paint BLACK_PAINT;
	private static final Paint BACKGROUND_PAINT;
	static {
		GRAY_PAINT = new Paint();
		GRAY_PAINT.setColor(0xFFAAAAAA);
		GRAY_PAINT.setAlpha(100);

		RED_PAINT = new Paint();
		RED_PAINT.setColor(0xFFFF0000);

		BLACK_PAINT = new Paint();
		BLACK_PAINT.setColor(0xFF000000);
		BLACK_PAINT.setAntiAlias(true);
		BLACK_PAINT.setTextAlign(Paint.Align.CENTER);
		BLACK_PAINT.setTypeface(Typeface.DEFAULT_BOLD);
		BLACK_PAINT.setStrokeWidth(2);

		BACKGROUND_PAINT = new Paint();
		BACKGROUND_PAINT.setColor(0xFFFFFFFF);
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
		dPadding = context.getResources().getDimension(R.dimen.graph_padding);
		dBarBufferWidth = context.getResources().getDimension(R.dimen.graph_bar_buffer);
		dBaseBuffer = context.getResources().getDimension(R.dimen.graph_base_buffer);
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

		BLACK_PAINT.setTextSize(dTextSize);
		
		canvas.drawPaint(BACKGROUND_PAINT);

		float x = dBaseX + (dShadowBarWidth / 2); // Text at bottom is centered
		float y = dBaseY;
		for (int i = 2; i <= 12; i++) {
			canvas.drawText(Integer.toString(i), x, y, BLACK_PAINT);
			x += dShadowBarWidth + dBarBufferWidth;
		}


		x = dBaseX;
		y = dBaseY - dBaseBuffer;
		for (int num : EXPECTED_FACTOR) {
			if (num != -1) {
				float yHeight = (dBaseY - dPadding) / (6f / num);
				RECT.set(x, y - yHeight, x + dShadowBarWidth, y);
				canvas.drawRoundRect(RECT, 5.0f, 5.0f, GRAY_PAINT);
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
					RECT.set(x, y - (robberNum * multiplier) + 5, x + dBarWidth, y);
					canvas.drawRoundRect(RECT, 5.0f, 5.0f, RED_PAINT);
					RECT.set(x, y - (robberNum * multiplier), x + dBarWidth, y - 5);
					canvas.drawRect(RECT, RED_PAINT);
					RECT.set(x, y - (both * multiplier), x + dBarWidth, y - (robberNum * multiplier) - 5);
					canvas.drawRoundRect(RECT, 5.0f, 5.0f, BLACK_PAINT);
					RECT.set(x, y - (both * multiplier) + 5, x + dBarWidth, y - (robberNum * multiplier));
					canvas.drawRect(RECT, BLACK_PAINT);
					canvas.drawText(Integer.toString(both), x + (dShadowBarWidth / 2), y - (both * multiplier) - dBarBufferWidth, BLACK_PAINT);
				} else {
					RECT.set(x, y - (robberNum * multiplier), x + dBarWidth, y);
					canvas.drawRoundRect(RECT, 5.0f, 5.0f, RED_PAINT);
					RECT.set(x, y - (both * multiplier), x + dBarWidth, y - (robberNum * multiplier));
					canvas.drawRoundRect(RECT, 5.0f, 5.0f, BLACK_PAINT);
					canvas.drawText(Integer.toString(both), x + (dShadowBarWidth / 2), y - (both * multiplier) - dBarBufferWidth, BLACK_PAINT);
				}
				x += dShadowBarWidth + dBarBufferWidth;
			}
		}
	}
}
