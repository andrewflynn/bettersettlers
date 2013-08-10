package com.nut.bettersettlers.ui;

import static com.nut.bettersettlers.data.MapSpecs.BOARD_RANGE_HALF_X;
import static com.nut.bettersettlers.data.MapSpecs.BOARD_RANGE_HALF_Y;
import static com.nut.bettersettlers.data.MapSpecs.BOARD_RANGE_X;
import static com.nut.bettersettlers.data.MapSpecs.BOARD_RANGE_Y;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.MapSpecs;
import com.nut.bettersettlers.data.MapSpecs.Harbor;
import com.nut.bettersettlers.data.MapSpecs.MapSize;
import com.nut.bettersettlers.data.MapSpecs.Piece;
import com.nut.bettersettlers.data.MapSpecs.Resource;
import com.nut.bettersettlers.logic.MapLogic;

public class MapView extends View {
	private static final String X = MapView.class.getSimpleName();
	
	private static final int INVALID_POINTER_ID = -1;
	
	private float dXR;
	private float dYR1;
	private float dYR2;
	private float dBorder;
	private float dFontSize;
	private float dProbDotR;
	private float dPlacementDotR;
	private float dPlacementTriangleHeight;
	private float dPlacementReasonBoxWidth;
	private float dProbTopBuffer;
	private float dHarborCircle;
	private float dMiddleX;
	private float dMiddleY;
	
	private Piece[][] mPieces;
	private int[][] mProbs;
	private List<Harbor> mHarbors;
	private MapSize mCurrentMap;
	private int mPlacementBookmark;
	private LinkedHashMap<Integer, List<String>> mPlacements;
	private ArrayList<Integer> mOrderedPlacements;

	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor;
	private float mPosX;
	private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
	
	public MapView(Context context) {
		super(context);

		init(context);
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	private void init(Context context) {
		initDimens(context);
		
		mCurrentMap = MapSize.STANDARD; // Default to standard
		mPieces = new Piece[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mProbs = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mHarbors = new ArrayList<Harbor>();
		mPlacementBookmark = -1; // No placements initially
		mPlacements = new LinkedHashMap<Integer, List<String>>();
		mOrderedPlacements = new ArrayList<Integer>();

		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
	
	private void initDimens(Context context) {		
		dXR = (float) getResources().getDimension(R.dimen.x_r);
		dYR1 = (float) getResources().getDimension(R.dimen.y_r1);
		dYR2 = (float) getResources().getDimension(R.dimen.y_r2);
		dBorder = (float) getResources().getDimension(R.dimen.border);
		dFontSize = (float) getResources().getDimension(R.dimen.font_size);
		dProbDotR = (float) getResources().getDimension(R.dimen.prob_dot_r);
		dPlacementDotR = (float) getResources().getDimension(R.dimen.placement_dot_r);
		dPlacementTriangleHeight = (float) getResources().getDimension(R.dimen.placement_triangle_height);
		dPlacementReasonBoxWidth = (float) getResources().getDimension(R.dimen.placement_reason_box_width);
		dProbTopBuffer = (float) getResources().getDimension(R.dimen.prob_top_buffer);
		dHarborCircle = (float) getResources().getDimension(R.dimen.harbor_circle);
		
		// Adjust scale for size of screen
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		float midWidth = display.getWidth() / 2f;
		float midHeight = display.getHeight() / 2f;
		dMiddleX = midWidth;
		dMiddleY = midHeight;
		float oldWidth = (dXR + dBorder) * BOARD_RANGE_X / 2f;
		float newScaleByWidth = midWidth / oldWidth;
		float oldHeight = (dYR1 + dYR2 + dBorder) * BOARD_RANGE_Y / 2f;
		float newScaleByHeight = midHeight / oldHeight;
		
		//Log.i(X, "newScaleByWidth: " + newScaleByWidth);
		//Log.i(X, "newScaleByHeight: " + newScaleByHeight);
		if (newScaleByWidth < newScaleByHeight) {
			mScaleFactor = newScaleByWidth;
		} else {
			mScaleFactor = newScaleByHeight;
		}
		
		//Log.i(X, "dMiddleX: " + dMiddleX);
		//Log.i(X, "dMiddleY: " + dMiddleY);
		//Log.i(X, "mScaleFactor: " + mScaleFactor);
	}

	public void setMapSize(MapSize currentMap) {
		mCurrentMap = currentMap;
	}

	public void setLandAndWaterResources(Resource[][] land, Harbor[][] water) {
		for (int i = 0; i < BOARD_RANGE_Y; i++) {
			for (int j = (i % 2); j < BOARD_RANGE_X; j += 2) {
				mPieces[j][i] = null;
				if (land != null && land[j][i] != null) {
					mPieces[j][i] = new Piece(j, i, land[j][i].getColor());
				} else if (water != null && water[j][i] != null) {
					mPieces[j][i] = new Piece(j, i, 0xFF1959DF);
				} else {
					mPieces[j][i] = new Piece(j, i, 0xFFFFFFFF);
				}
			}
		}
	}

	public void setProbabilities(int[][] probs) {
		mProbs = probs;
	}

	public void setHarbors(List<Harbor> harbors) {
		mHarbors = harbors;
	}
	
	public void setPlacementBookmark(int placementBookmark) {
		mPlacementBookmark = placementBookmark;
	}

	public void setPlacements(LinkedHashMap<Integer, List<String>> placements) {
		mPlacements = placements;
	}

	public void setOrderedPlacements(ArrayList<Integer> orderedPlacements) {
		mOrderedPlacements = orderedPlacements;
	}
	
	public float getScale() {
		return mScaleFactor;
	}
	
	public void setScale(float scale) {
		mScaleFactor = scale;
	}
	
	public void setScale() {
		
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

			//Log.i("SCALE", "MiddleX: " + dMiddleX);
			//Log.i("SCALE", "MiddleY: " + dMiddleY);
			//Log.i("SCALE", "Factor: " + mScaleFactor);
			invalidate();
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);
		
		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();

			mLastTouchX = x;
			mLastTouchY = y;
			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);

			// Only move if the ScaleGestureDetector isn't processing a gesture.
			if (!mScaleDetector.isInProgress()) {
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;
				
				invalidate();
			}

			mLastTouchX = x;
			mLastTouchY = y;

			break;
		}

		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			break;
		}
		}
		
		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Log.i(X, "onDraw: " + mScaleFactor);
		
		//canvas.save();
	    canvas.translate(mPosX, mPosY);
		
		Paint generalPaint = new Paint();
		generalPaint.setTextAlign(Paint.Align.CENTER);
		generalPaint.setTypeface(Typeface.DEFAULT_BOLD);
		generalPaint.setStrokeWidth(2 * mScaleFactor);
		generalPaint.setTextSize(dFontSize * mScaleFactor);
		generalPaint.setAntiAlias(true);
		for (int i = 0; i < mPieces.length; i++) {
			for (int j = 0; j < mPieces[i].length; j++) {
				if (mPieces != null && mPieces[i][j] != null) {
					Piece piece = mPieces[i][j];
					Rect rect = getAdjustedRect(piece);
					
					ShapeDrawable shape = new ShapeDrawable(new HexShape(
							dXR * mScaleFactor,
							dYR1 * mScaleFactor,
							dYR2 * mScaleFactor));
					shape.setBounds(rect);
					shape.getPaint().setColor(piece.getColor());
					shape.draw(canvas);
					
					if (mProbs != null && mProbs[i][j] != 0) {
						if (mProbs[i][j] == 6 || mProbs[i][j] == 8) {
							generalPaint.setColor(0xFFFF0000);
						} else {
							generalPaint.setColor(0xFF000000);
						}
						canvas.drawText(Integer.toString(mProbs[i][j]), rect.centerX(),
								rect.centerY() - dProbTopBuffer * mScaleFactor, generalPaint);
						drawDots(canvas, generalPaint, mProbs[i][j], rect);
					}
				}
			}
		}
		for (int i = 0; i < mHarbors.size(); i++) {
			if (mHarbors.get(i) != null) {
				Point point = mCurrentMap.getWaterGrid()[i];
				drawHarbor(canvas, generalPaint, mHarbors.get(i), getAdjustedRect(mPieces[point.x][point.y]), i);
			}
		}

		if (mPlacementBookmark >= 0) {
			int placementIndex = mOrderedPlacements.get(mPlacementBookmark);

			// Ignore any places that aren't allowed settlements on the coast
			if (mCurrentMap.getPlacementIndexes()[placementIndex].length == 2) {
				int landNeighbor = mCurrentMap.getPlacementIndexes()[placementIndex][0];
				int landDirection = mCurrentMap.getPlacementIndexes()[placementIndex][1];

				Point point = mCurrentMap.getLandGrid()[landNeighbor];
				Rect rect = getAdjustedRect(mPieces[point.x][point.y]);
				float x = rect.centerX() + calculatePlacementOffsetX(landDirection);
				float y = rect.centerY() + calculatePlacementOffsetY(landDirection);

				// Draw circle
				drawDotSuggestions(canvas, x, y);

				// Draw reasons box
				drawReasonsBox(canvas, mPlacementBookmark, mPlacements.get(placementIndex), x, y);
			}
		}
		
		//canvas.restore();
	}
	
	private Rect getAdjustedRect(Piece piece) {
		int newGridX = BOARD_RANGE_HALF_X - piece.getGridX();
		int newGridY = BOARD_RANGE_HALF_Y - piece.getGridY();
		float startX = dMiddleX - (newGridX * (dXR + dBorder) * mScaleFactor);
		float startY = dMiddleY - (newGridY * (dYR1 + dYR2 + dBorder) * mScaleFactor);

		return new Rect((int) startX, (int) startY,
				(int) (startX + (2 * dXR * mScaleFactor)),
				(int) (startY + (2 * (dYR1 + dYR2) * mScaleFactor)));
	}

	private void drawDotSuggestions(Canvas canvas, float x, float y) {
		Paint lightPaint = new Paint();
		lightPaint.setColor(0xFF000000);
		lightPaint.setAlpha(200);
		lightPaint.setAntiAlias(true);

		canvas.drawCircle(x, y, dPlacementDotR * mScaleFactor, lightPaint);
	}

	private void drawReasonsBox(Canvas canvas, int index, List<String> reasons, float x, float y) {
		Paint lightPaint = new Paint();
		lightPaint.setColor(0xFF000000);
		lightPaint.setAlpha(175);
		lightPaint.setAntiAlias(true);

		float yLevel = y - dPlacementTriangleHeight;
		canvas.drawRoundRect(new RectF(
				x - dPlacementReasonBoxWidth / 3,
				yLevel - (reasons.size() + 1) * dYR1, // +1 since words are bottom aligned
				x + dPlacementReasonBoxWidth * 2/3,
				yLevel),
				5.0f, 5.0f, lightPaint);

		// Draw bubble connector
		Path bubblePath = new Path();
		bubblePath.moveTo(x, y);
		bubblePath.lineTo(x - dXR, yLevel);
		bubblePath.lineTo(x, yLevel);
		bubblePath.lineTo(x, y);
		canvas.drawPath(bubblePath, lightPaint);

		Paint textPaint = new Paint();
		textPaint.setTextAlign(Paint.Align.LEFT);
		textPaint.setStrokeWidth(1);
		textPaint.setTextSize(dFontSize);
		textPaint.setAntiAlias(true);
		textPaint.setColor(0xFFFFFFFF);

		// +1 for zero-indexing
		canvas.drawText("#" + (index + 1),
				x - dPlacementReasonBoxWidth / 3 + dXR / 2,
				y - dPlacementTriangleHeight - reasons.size() * dYR1 / 2,
				textPaint);

		yLevel -= reasons.size() * dYR1;
		for (String reason : reasons) {
			canvas.drawText(reason, x - dPlacementReasonBoxWidth / 3 + 2 * dXR, yLevel, textPaint);
			yLevel += dYR1;
		}
	}

	private void drawDots(Canvas canvas, Paint paint, int prob, Rect rect) {
		float x = rect.centerX();
		float y = rect.centerY();
		paint.setAntiAlias(false);
		switch (MapSpecs.PROBABILITY_MAPPING[prob]) {
		case 5:
			canvas.drawCircle(x - (dXR / 2 * mScaleFactor), y, dProbDotR * mScaleFactor, paint);
			canvas.drawCircle(x + (dXR / 2 * mScaleFactor), y, dProbDotR * mScaleFactor, paint);
		case 3:
			canvas.drawCircle(x - (dXR / 4 * mScaleFactor), y, dProbDotR * mScaleFactor, paint);
			canvas.drawCircle(x + (dXR / 4 * mScaleFactor), y, dProbDotR * mScaleFactor, paint);
		case 1:
			canvas.drawCircle(x, y, dProbDotR * mScaleFactor, paint);
			break;
		case 4:
			canvas.drawCircle(x - (dXR * 3/8 * mScaleFactor), y, dProbDotR * mScaleFactor, paint);
			canvas.drawCircle(x + (dXR * 3/8 * mScaleFactor), y, dProbDotR * mScaleFactor, paint);
		case 2:
			canvas.drawCircle(x - (dXR * 1/8 * mScaleFactor), y, dProbDotR * mScaleFactor, paint);
			canvas.drawCircle(x + (dXR * 1/8 * mScaleFactor), y, dProbDotR * mScaleFactor, paint);
			break;
		case 0:
		default:
		}
		paint.setAntiAlias(true);
	}

	private void drawHarbor(Canvas canvas, Paint paint, Harbor harbor, Rect rect, int i) {
		float x = rect.centerX();
		float y = rect.centerY() - (dProbTopBuffer + (2 * dBorder)) * mScaleFactor;
		
		int dir = MapLogic.whichWayHarborFaces(mCurrentMap, harbor);
		if (harbor.getResource() == Resource.WATER) {
			// Do nothing
		} else if (harbor.getResource() == Resource.DESERT) {
			paint.setColor(0xFFFFFFFF);
			canvas.drawCircle(x, y, dHarborCircle * mScaleFactor, paint);
			drawHarborLine(canvas, paint, mCurrentMap.getHarborLines()[i][dir], x, y);
			drawHarborLine(canvas, paint, mCurrentMap.getHarborLines()[i][dir + 1], x, y);
			paint.setColor(0xFF000000);
			canvas.drawText("3", x, y + dProbTopBuffer * mScaleFactor, paint);
		} else {
			paint.setColor(harbor.getResource().getColor());
			canvas.drawCircle(x, y, dHarborCircle * mScaleFactor, paint);
			drawHarborLine(canvas, paint, mCurrentMap.getHarborLines()[i][dir], x, y);
			drawHarborLine(canvas, paint, mCurrentMap.getHarborLines()[i][dir + 1], x, y);
			paint.setColor(0xFF000000);
			canvas.drawText("2", x, y + dProbTopBuffer * mScaleFactor, paint);
		}
	}

	private void drawHarborLine(Canvas canvas, Paint paint, int dir, float x, float y) {
		switch(dir) {
		case 0:
			canvas.drawLine(x, y, x - dXR * mScaleFactor, y - (dYR1 / 2) * mScaleFactor, paint);
			break;
		case 1:
			canvas.drawLine(x, y, x, y - (dYR2 + (dYR1 / 2)) * mScaleFactor, paint);
			break;
		case 2:
			canvas.drawLine(x, y, x + dXR * mScaleFactor, y - (dYR1 / 2) * mScaleFactor, paint);
			break;
		case 3:
			canvas.drawLine(x, y, x + dXR * mScaleFactor, y + (dYR1 / 2) * mScaleFactor, paint);
			break;
		case 4:
			canvas.drawLine(x, y, x, y + (dYR2 + (dYR1 / 2)) * mScaleFactor, paint);
			break;
		case 5:
			canvas.drawLine(x, y, x - dXR * mScaleFactor, y + (dYR1 / 2) * mScaleFactor, paint);
			break;
		default:
			Log.i(X, "WARNING: Cannot draw this line: "  +  dir);
			break;
		}
	}

	private float calculatePlacementOffsetX(int num) {
		float temp;
		switch (num) {
		case 0:
			temp = -(dXR + dBorder);
			break;
		case 1:
			temp = 0;
			break;
		case 2:
			temp = dXR + dBorder;
			break;
		case 3:
			temp = dXR + dBorder;
			break;
		case 4:
			temp = 0;
			break;
		case 5:
			temp = -(dXR + dBorder);
			break;
		default:
			temp = 0;
			break;
		}
		return temp * mScaleFactor;
	}

	private float calculatePlacementOffsetY(int num) {
		float temp;
		switch (num) {
		case 0:
			temp = -dYR1/2;
			break;
		case 1:
			temp = -(dYR2 + dYR1/2);
			break;
		case 2:
			temp = -dYR1/2;
			break;
		case 3:
			temp = dYR1/2;
			break;
		case 4:
			temp = dYR2 + dYR1/2;
			break;
		case 5:
			temp = dYR1/2;
			break;
		default:
			temp = 0;
			break;
		}
		return (temp - (dProbTopBuffer + (2 * dBorder))) * mScaleFactor;
	}
}
