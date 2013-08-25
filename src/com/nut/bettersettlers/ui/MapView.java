package com.nut.bettersettlers.ui;

import static com.nut.bettersettlers.util.Consts.BOARD_RANGE_X;
import static com.nut.bettersettlers.util.Consts.BOARD_RANGE_Y;
import static com.nut.bettersettlers.util.Consts.PROBABILITY_MAPPING;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.Harbor;
import com.nut.bettersettlers.data.MapSize;
import com.nut.bettersettlers.data.Resource;
import com.nut.bettersettlers.logic.MapLogic;
import com.nut.bettersettlers.util.BetterLog;
import com.nut.bettersettlers.util.Consts;
import com.nut.bettersettlers.util.Util;

public class MapView extends View {
	private static final int INVALID_POINTER_ID = -1;
	
	private static final Paint RED_PAINT;
	private static final Paint GENERAL_PAINT;
	private static final Paint PROB_DOTS_PAINT;
	private static final Paint REASON_PAINT;
	private static final Path BUBBLE_PATH;
	private static final Paint TEXT_PAINT;
	private static final Paint TEST_PAINT;
	static {
		RED_PAINT = new Paint();
		RED_PAINT.setColor(Color.RED);
		RED_PAINT.setStyle(Style.STROKE);

		GENERAL_PAINT = new Paint();
		GENERAL_PAINT.setTextAlign(Paint.Align.CENTER);
		GENERAL_PAINT.setTypeface(Typeface.DEFAULT_BOLD);
		GENERAL_PAINT.setAntiAlias(true);

		PROB_DOTS_PAINT = new Paint();
		PROB_DOTS_PAINT.setColor(0xFF000000);
		PROB_DOTS_PAINT.setAlpha(200);
		PROB_DOTS_PAINT.setAntiAlias(true);

		REASON_PAINT = new Paint();
		REASON_PAINT.setColor(0xFF000000);
		REASON_PAINT.setAlpha(175);
		REASON_PAINT.setAntiAlias(true);
		
		BUBBLE_PATH = new Path();
		
		TEXT_PAINT = new Paint();
		TEXT_PAINT.setTextAlign(Paint.Align.LEFT);
		TEXT_PAINT.setStrokeWidth(1);
		TEXT_PAINT.setAntiAlias(true);
		TEXT_PAINT.setColor(0xFFFFFFFF);
		
		TEST_PAINT = new Paint();
		TEST_PAINT.setTextAlign(Paint.Align.CENTER);
		TEST_PAINT.setTypeface(Typeface.DEFAULT_BOLD);
		TEST_PAINT.setAntiAlias(true);
	}

	private static final float XR = 17f;
	private static final float XR_2 = 9f;
	private static final float YR1 = 18f;
	private static final float YR2 = 11f;
	private static final float YR2_2 = 6f;
	private static final float HEX_R = 15.59f;
	private static final float BORDER_WIDTH = 2f;
	private static final float PROBABILITY_DOT_RADIUS = 1.5f;
	private static final float PLACEMENT_DOT_RADIUS = 5f;
	private static final float PLACEMENT_TRIANGLE_HEIGHT = 25f;
	private static final float PROBABILITY_TOP_BUFFER = -2f;
	private static final float HARBOR_CIRCLE_RADIUS = 10f;
	
	private float dFontSize;
	private float dReasonFontSize;
	private float dReasonFontBufferMultiplier;
	private float dViewBuffer;
	private float dPlacementReasonBoxWidth;
	private float dPlacementReasonBoxLineHeight;
	
	private int mMaxX;
	private int mMaxY;
	
	private boolean mReady;
	
	private int[][] mPiecesX;
	private int[][] mPiecesY;
	private int[][] mPiecesColor;
	private int[][] mUPiecesX;
	private int[][] mUPiecesY;
	private int[][] mUPiecesColor;

	private ShapeDrawable mHexShapeDrawable;
	private Rect mDrawRect;
	private Rect mDotsRect;
	private RectF mReasonsRectF;
	
	private boolean[][] mUVisibility;
	private int[][] mProbs;
	private int[][] mUProbs;
	private ArrayList<Harbor> mHarbors;
	private MapSize mMapSize;
	private int mPlacementBookmark;
	private SparseArray<ArrayList<String>> mPlacements;
	private ArrayList<Integer> mOrderedPlacements;

	private ScaleGestureDetector mScaleDetector;
	private GestureDetector mTapDetector;
	private float mScaleFactor;
	private float mTextScaleFactor;
	private float mDiffX;
	private float mDiffY;
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
		BetterLog.i("INIT");
		initDimens(context);
		
		BetterLog.i("mReady=false");
		mReady = false;

		mPiecesX = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mPiecesY = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mPiecesColor = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		
		mUPiecesX = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mUPiecesY = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mUPiecesColor = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];

		mUVisibility = new boolean[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		
		mHexShapeDrawable = new ShapeDrawable(new HexShape());
		mDrawRect = new Rect();
		mDotsRect = new Rect();
		mReasonsRectF = new RectF();
		
		mProbs = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mUProbs = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
		mHarbors = new ArrayList<Harbor>();
		mPlacementBookmark = -1; // No placements initially
		mPlacements = new SparseArray<ArrayList<String>>();
		mOrderedPlacements = new ArrayList<Integer>();

		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mTapDetector = new GestureDetector(context, new TapListener());
		
		mTextScaleFactor = 1.0f;
	}
	
	private void initDimens(Context context) {
		Resources res = getResources();

		dFontSize = (float) res.getDimension(R.dimen.font_size);
		dReasonFontSize = (float) res.getDimension(R.dimen.reason_font_size);
		dReasonFontBufferMultiplier = (float) res.getDimension(R.dimen.reason_font_buffer_multiplier);
		dViewBuffer = (float) res.getDimension(R.dimen.view_buffer);
		dPlacementReasonBoxWidth = (float) res.getDimension(R.dimen.placement_reason_box_width);
		dPlacementReasonBoxLineHeight = (float) res.getDimension(R.dimen.placement_reason_box_line_height);
	}
	
	private static class SavedState extends BaseSavedState {
		private boolean ready;
		private int maxX;
		private int maxY;
		private int[][] piecesX;
		private int[][] piecesY;
		private int[][] piecesColor;
		private int[][] uPiecesX;
		private int[][] uPiecesY;
		private int[][] uPiecesColor;
		private float scaleFactor;
		private float textScaleFactor;
		private MapSize mapSize;
		private int[][] probs;
		private int[][] uProbs;
		private boolean[][] uVisibility;
		private ArrayList<Harbor> harbors;
		private int placementBookmark;
		private SparseArray<ArrayList<String>> placements;
		private ArrayList<Integer> orderedPlacements;

        private SavedState(Parcelable superState) {
            super(superState);
        }
        
		@SuppressWarnings("unchecked")
		private SavedState(Parcel in) {
            super(in);
            ready = in.readByte() == 1;
            maxX = in.readInt();
            maxY = in.readInt();
            piecesX = (int[][]) in.readSerializable();
            piecesY = (int[][]) in.readSerializable();
            piecesColor = (int[][]) in.readSerializable();
            uPiecesX = (int[][]) in.readSerializable();
            uPiecesY = (int[][]) in.readSerializable();
            uPiecesColor = (int[][]) in.readSerializable();
            scaleFactor = in.readFloat();
            textScaleFactor = in.readFloat();
            mapSize = in.readParcelable(MapView.class.getClassLoader());
            probs = (int[][]) in.readSerializable();
            uProbs = (int[][]) in.readSerializable();
            uVisibility = (boolean[][]) in.readSerializable();
            if (harbors == null) {
            	harbors = new ArrayList<Harbor>();
            }
            in.readTypedList(harbors, Harbor.CREATOR);
            placementBookmark = in.readInt();
            placements = Util.bundleToSparseArrayArrayList(in.readBundle());
            orderedPlacements = (ArrayList<Integer>) in.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (ready ? 1 : 0));
            out.writeInt(maxX);
            out.writeInt(maxY);
            out.writeSerializable(piecesX);
            out.writeSerializable(piecesY);
            out.writeSerializable(piecesColor);
            out.writeSerializable(uPiecesX);
            out.writeSerializable(uPiecesY);
            out.writeSerializable(uPiecesColor);
            out.writeFloat(scaleFactor);
            out.writeFloat(textScaleFactor);
            out.writeParcelable(mapSize, 0);
            out.writeSerializable(probs);
            out.writeSerializable(uProbs);
            out.writeSerializable(uVisibility);
            out.writeTypedList(harbors);
            out.writeInt(placementBookmark);
            out.writeBundle(Util.sparseArrayArrayListToBundle(placements));
            out.writeSerializable(orderedPlacements);
        }

        @SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
	}
	
	@Override
	protected Parcelable onSaveInstanceState () {
		BetterLog.i("MapView.onSaveInstanceState");
		Parcelable savedState = super.onSaveInstanceState();
		
		SavedState ss = new SavedState(savedState);
		ss.ready = mReady;
		ss.maxX = mMaxX;
		ss.maxY = mMaxY;
		ss.piecesX = mPiecesX;
		ss.piecesY = mPiecesY;
		ss.piecesColor = mPiecesColor;
		ss.uPiecesX = mUPiecesX;
		ss.uPiecesY = mUPiecesY;
		ss.uPiecesColor = mUPiecesColor;
		ss.scaleFactor = mScaleFactor;
		ss.textScaleFactor = mTextScaleFactor;
		ss.mapSize = mMapSize;
		ss.probs = mProbs;
		ss.uProbs = mUProbs;
		ss.uVisibility = mUVisibility;
		ss.harbors = mHarbors;
		ss.placementBookmark = mPlacementBookmark;
		ss.placements = mPlacements;
		ss.orderedPlacements = mOrderedPlacements;
		
		return ss;
	}
	
	@Override
	protected void onRestoreInstanceState (Parcelable state) {
		BetterLog.i("MapView.onRestoreInstanceState");
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		mReady = ss.ready;
		mMaxX = ss.maxX;
		mMaxY = ss.maxY;
		mPiecesX = ss.piecesX;
		mPiecesY = ss.piecesY;
		mPiecesColor = ss.piecesColor;
		mUPiecesX = ss.uPiecesX;
		mUPiecesY = ss.uPiecesY;
		mUPiecesColor = ss.uPiecesColor;
		mScaleFactor = ss.scaleFactor;
		mTextScaleFactor = ss.textScaleFactor;
		mMapSize = ss.mapSize;
		mProbs = ss.probs;
		mUProbs = ss.uProbs;
		mUVisibility = ss.uVisibility;
		mHarbors = ss.harbors;
		mPlacementBookmark = ss.placementBookmark;
		mPlacements = ss.placements;
		mOrderedPlacements = ss.orderedPlacements;
	}
	
	public void setReady() {
		BetterLog.i("ready=true");
		mReady = true;
	}

	public void setMapSize(MapSize currentMap) {
		BetterLog.i("setMapSize: " + currentMap);
		mMapSize = currentMap;
	}

	public void setLandAndWaterResources(Resource[][] land, Harbor[][] water, Resource[][] unknowns) {
		BetterLog.i("setLAWR");
		mMaxX = 0;
		mMaxY = 0;
		
		for (int i = 0; i < BOARD_RANGE_Y; i++) {
			for (int j = (i % 2); j < BOARD_RANGE_X; j += 2) {
				if (land != null && land[j][i] != null) {
					if (j > mMaxX) {
						mMaxX = j;
					}
					if (i > mMaxY) {
						mMaxY = i;
					}
					mPiecesX[j][i] = j;
					mPiecesY[j][i] = i;
					mPiecesColor[j][i] = land[j][i].color;
				} else if (water != null && water[j][i] != null) {
					if (j > mMaxX) {
						mMaxX = j;
					}
					if (i > mMaxY) {
						mMaxY = i;
					}
					mPiecesX[j][i] = j;
					mPiecesY[j][i] = i;
					mPiecesColor[j][i] = 0xFF1959DF;
				} else {
					mPiecesX[j][i] = j;
					mPiecesY[j][i] = i;
					mPiecesColor[j][i] = 0xFFFFFFFF;
					//mPiecesColor[j][i] = 0xFFCCCCCC;
				}

				if (unknowns != null && unknowns[j][i] != null) {
					if (j > mMaxX) {
						mMaxX = j;
					}
					if (i > mMaxY) {
						mMaxY = i;
					}
					mUPiecesX[j][i] = j;
					mUPiecesY[j][i] = i;
					mUPiecesColor[j][i] = unknowns[j][i].color;
				} else {
					mUPiecesX[j][i] = 0;
					mUPiecesY[j][i] = 0;
					mUPiecesColor[j][i] = 0;
				}
			}
		}
		//BetterLog.i(String.format("Max (%s,%s)", mMaxX, mMaxY));
		// Force the re-draw after finding the max X/Y
		invalidate();
	}

	public void setProbabilities(int[][] probs, int[][] unknownProbs) {
		mProbs = probs;
		mUProbs = unknownProbs;
	}
	
	public boolean[][] getUVisibility() {
		return mUVisibility;
	}
	
	public void setUVisibility(boolean[][] visibles) {
		mUVisibility = visibles;
	}

	public void setHarbors(ArrayList<Harbor> harbors) {
		mHarbors = harbors;
	}
	
	public void setPlacementBookmark(int placementBookmark) {
		mPlacementBookmark = placementBookmark;
	}

	public void setPlacements(SparseArray<ArrayList<String>> placements) {
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
	
	private double dist(float x1, float y1, float x2, float y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	private Pair<Integer, Integer> getTouchedPiece(float x, float y) {
		for (int i = 0; i < mPiecesX.length; i++) {
			for (int j = 0; j < mPiecesX[i].length; j++) {
				if (mPiecesX != null && mPiecesY != null) {
					adjustDrawRect(mPiecesX[i][j], mPiecesY[i][j]);
					
					if (dist(x, y, mDrawRect.exactCenterX(),
							mDrawRect.exactCenterY()) <= HEX_R * mScaleFactor) {
						return new Pair<Integer, Integer>(i, j);
					}
				}
			}
		}
		return null;
	}
	
	private Pair<Integer, Integer> getTouchedUnknownPiece(float x, float y) {
		for (int i = 0; i < mUPiecesX.length; i++) {
			for (int j = 0; j < mUPiecesX[i].length; j++) {
				if (mUPiecesX != null && mUPiecesY != null) {
					adjustDrawRect(mUPiecesX[i][j], mUPiecesY[i][j]);
					
					if (dist(x, y, mDrawRect.exactCenterX(),
							mDrawRect.exactCenterY()) <= HEX_R * mScaleFactor) {
						return new Pair<Integer, Integer>(i, j);
					}
				}
			}
		}
		return null;
	}

	public CatanMap getCatanMap() {
		return mMapSize.mapProvider.get();
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float newScaleFactor = mScaleFactor * detector.getScaleFactor();
			if (newScaleFactor <= 10.0f && newScaleFactor >= 0.1f) {
				mScaleFactor = newScaleFactor;
				mTextScaleFactor *= detector.getScaleFactor();
				
				invalidate();
			}
			
			/*
			mTextScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
			mTextScaleFactor = Math.max(0.1f, Math.min(mTextScaleFactor, 10.0f));
			*/

			//BetterLog.i("MiddleX: " + dMiddleX);
			//BetterLog.i("MiddleY: " + dMiddleY);
			//BetterLog.i("Factor: " + mScaleFactor);
			//invalidate();
			return true;
		}
	}
	
	private class TapListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent ev) {
			//Toast.makeText(getContext(), String.format("(%f,%f)", ev.getX(), ev.getY()), Toast.LENGTH_SHORT).show();
			//Toast.makeText(getContext(), String.format("dYR2: %f", dYR2), Toast.LENGTH_SHORT).show();
			//Toast.makeText(getContext(), String.format("mScaleFactor: %f", mScaleFactor), Toast.LENGTH_SHORT).show();
			
			// Check for touching unknowns
			Pair<Integer, Integer> pair = getTouchedUnknownPiece(ev.getX(), ev.getY());
			if (pair != null) {
				int i = pair.first;
				int j = pair.second;
				
				if (mUVisibility != null) {
					mUVisibility[i][j] = !mUVisibility[i][j];
					
					invalidate();
				}
			}
			pair = getTouchedPiece(ev.getX(), ev.getY());
			if (pair != null) {
				int i = pair.first;
				int j = pair.second;
				
				if (Consts.DEBUG_MAP) {
					int color = mPiecesColor[i][j];
					if (color == 0xFF00FF00) {
						mPiecesColor[i][j] = 0xFF0000FF;	
					} else if (color == 0xFF0000FF) {
						mPiecesColor[i][j] = 0xFFCCCCCC;	
					} else {
						mPiecesColor[i][j] = 0xFF00FF00;
					}
					invalidate();
				}
			}
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);
		
		// Let the TapDetector also inspect all events.
		mTapDetector.onTouchEvent(ev);
		
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

				mDiffX += dx;
				mDiffY += dy;
				
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
	
	public void onDrawTest(Canvas canvas) {
		TEST_PAINT.setStrokeWidth(2 * mScaleFactor);
		TEST_PAINT.setTextSize(8 * mScaleFactor);
		
		HexShape hexShape = (HexShape) mHexShapeDrawable.getShape();
		hexShape.setPath(XR * mScaleFactor,
				YR1 * mScaleFactor,
				YR2 * mScaleFactor);
		
		for (int i = 0; i < mPiecesX.length; i++) {
			for (int j = 0; j < mPiecesX[i].length; j++) {
				if (mPiecesX != null && mPiecesY != null) {
					adjustDrawRect(mPiecesX[i][j], mPiecesY[i][j]);
					int color = mPiecesColor[i][j];
					
					mHexShapeDrawable.setBounds(mDrawRect);
					if (color == 0xFF0000FF) {
						mHexShapeDrawable.getPaint().setColor(0xFF0000FF);
					} else if (color == 0xFF00FF00) {
						mHexShapeDrawable.getPaint().setColor(0xFF00FF00);
					} else {
						mHexShapeDrawable.getPaint().setColor(0xFFCCCCCC);
					}
					mHexShapeDrawable.draw(canvas);
					
					canvas.drawText(String.format("(%d,%d)", i, j), mDrawRect.centerX(),
							mDrawRect.centerY() - PROBABILITY_TOP_BUFFER * mScaleFactor, TEST_PAINT);
					//canvas.drawText("" + rect.top, rect.centerX(), rect.centerY() - dYR1 / 2f * mScaleFactor, generalPaint);
					//canvas.drawText("" + rect.bottom, rect.centerX(), rect.centerY() + dYR1 / 2f * mScaleFactor, generalPaint);
					//canvas.drawText("" + rect.left, rect.centerX() - dXR_2 * mScaleFactor, rect.centerY(), generalPaint);
					//canvas.drawText("" + rect.right, rect.centerX() + dXR_2 * mScaleFactor, rect.centerY(), generalPaint);
				}
			}
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Set up initial scale
		
		if (mScaleFactor == 0f && mMaxX != 0f && mMaxY != 0f) {
			// mMaxX gets + 1 because of the overlap of the horizontal counting hexes
			// we need to count the last half-hex hanging off the right edge
			float baseWidth = ((XR + BORDER_WIDTH) * (mMaxX + 1)) + (2 * dViewBuffer);
			float newScaleByWidth = getWidth() / baseWidth;
			float baseHeight = ((YR1 + YR2 + BORDER_WIDTH) * mMaxY) + (2 * dViewBuffer);
			float newScaleByHeight = getHeight() / baseHeight;
			
			//BetterLog.i("getWidth        : " + getWidth());
			//BetterLog.i("baseWidth       : " + baseWidth);
			//BetterLog.i("newScaleByWidth : " + newScaleByWidth);

			//BetterLog.i("getHeight       : " + getHeight());
			//BetterLog.i("baseHeight      : " + baseHeight);
			//BetterLog.i("newScaleByHeight: " + newScaleByHeight);
			if (newScaleByWidth < newScaleByHeight) {
				mScaleFactor = newScaleByWidth;
			} else {
				mScaleFactor = newScaleByHeight;
			}
			//BetterLog.i("MapView.onSizeChanged() new scale: " + mScaleFactor);
		}
		
		//canvas.save();
	    //canvas.translate(mPosX, mPosY);
		
		if (Consts.DEBUG_MAP) {
			onDrawTest(canvas);
			return;
		}
		if (Consts.DEBUG_MAP_WITH_RED) {
			for (int i = 0; i < mPiecesX.length; i++) {
				for (int j = 0; j < mPiecesX[i].length; j++) {
					if (mPiecesX != null && mPiecesY != null) {
						adjustDrawRect(mPiecesX[i][j], mPiecesY[i][j]);
						
						canvas.drawRect(mDrawRect, RED_PAINT);
					}
				}
			}	
		}
		
		if (!mReady) {
			BetterLog.i("tried to draw before ready. not drawing");
			return;
		}
		
		GENERAL_PAINT.setStrokeWidth(2 * mScaleFactor);
		GENERAL_PAINT.setTextSize(dFontSize * mTextScaleFactor);

		HexShape hexShape = (HexShape) mHexShapeDrawable.getShape();
		hexShape.setPath(XR * mScaleFactor,
				YR1 * mScaleFactor,
				YR2 * mScaleFactor);
		
		// First draw the real ones
		for (int i = 0; i < mPiecesX.length; i++) {
			for (int j = 0; j < mPiecesX[i].length; j++) {
				if (mPiecesX != null && mPiecesY != null) {
					adjustDrawRect(mPiecesX[i][j], mPiecesY[i][j]);
					
					mHexShapeDrawable.setBounds(mDrawRect);
					mHexShapeDrawable.getPaint().setColor(mPiecesColor[i][j]);
					mHexShapeDrawable.draw(canvas);
				}
			}
		}
		for (int i = 0; i < mPiecesX.length; i++) {
			for (int j = 0; j < mPiecesX[i].length; j++) {
				if (mPiecesX != null && mPiecesY != null) {
					adjustDrawRect(mPiecesX[i][j], mPiecesY[i][j]);
					
					if (mProbs != null && mProbs[i][j] != 0 && mProbs[i][j] != -1) {
						int prob = mProbs[i][j];
						if (mProbs[i][j] < 0) {
							prob *= -1;
						}
						
						if (prob > 12) {
							// Split into two
							String[] two = Integer.toString(prob).split("7");
							int oneInt = Integer.parseInt(two[0]);
							if (oneInt == 6 || oneInt == 8) {
								GENERAL_PAINT.setColor(0xFFFF0000);
							} else {
								GENERAL_PAINT.setColor(0xFF000000);
							}
							canvas.drawText(Integer.toString(oneInt), mDrawRect.centerX() - XR_2 * mScaleFactor,
									mDrawRect.centerY() + YR2_2 * mScaleFactor - PROBABILITY_TOP_BUFFER * mScaleFactor, GENERAL_PAINT);
							
							mDotsRect.set((int) (mDrawRect.left - XR_2 * mScaleFactor),
									(int) (mDrawRect.top + YR2_2 * mScaleFactor),
									(int) (mDrawRect.right - XR_2 * mScaleFactor),
									(int) (mDrawRect.bottom + YR2_2 * mScaleFactor));
							drawDots(canvas, GENERAL_PAINT, oneInt, mDotsRect);

							int twoInt = Integer.parseInt(two[1]);
							if (twoInt == 6 || twoInt == 8) {
								GENERAL_PAINT.setColor(0xFFFF0000);
							} else {
								GENERAL_PAINT.setColor(0xFF000000);
							}
							canvas.drawText(Integer.toString(twoInt), mDrawRect.centerX() + XR_2 * mScaleFactor,
									mDrawRect.centerY() - YR2_2 * mScaleFactor - PROBABILITY_TOP_BUFFER * mScaleFactor, GENERAL_PAINT);
							
							mDotsRect.set((int) (mDrawRect.left + XR_2 * mScaleFactor),
									(int) (mDrawRect.top - YR2_2 * mScaleFactor),
									(int) (mDrawRect.right + XR_2 * mScaleFactor),
									(int) (mDrawRect.bottom - YR2_2 * mScaleFactor));
							drawDots(canvas, GENERAL_PAINT, twoInt, mDotsRect);
						} else {
							if (prob == 6 || prob == 8) {
								GENERAL_PAINT.setColor(0xFFFF0000);
							} else {
								GENERAL_PAINT.setColor(0xFF000000);
							}
							canvas.drawText(Integer.toString(prob), mDrawRect.centerX(),
									mDrawRect.centerY() - PROBABILITY_TOP_BUFFER * mScaleFactor, GENERAL_PAINT);
							drawDots(canvas, GENERAL_PAINT, prob, mDrawRect);
						}
					}
				}
			}
		}
		
		// Next draw unknowns
		for (int i = 0; i < mUPiecesX.length; i++) {
			for (int j = 0; j < mUPiecesX[i].length; j++) {
				if (mUPiecesX != null && mUPiecesY != null && mUPiecesX[i][j] > 0) {
					adjustDrawRect(mUPiecesX[i][j], mUPiecesY[i][j]);

					mHexShapeDrawable.setBounds(mDrawRect);
					if (mUVisibility != null && !mUVisibility[i][j]) {
						mHexShapeDrawable.getPaint().setColor(0xFFCCCCCC);
					} else {
						mHexShapeDrawable.getPaint().setColor(mUPiecesColor[i][j]);
					}
					mHexShapeDrawable.draw(canvas);

					if (mUVisibility == null || mUVisibility[i][j]) {
						if (mUProbs != null && mUProbs[i][j] > 0) {
							if (mUProbs[i][j] == 6 || mUProbs[i][j] == 8) {
								GENERAL_PAINT.setColor(0xFFFF0000);
							} else {
								GENERAL_PAINT.setColor(0xFF000000);
							}
							canvas.drawText(Integer.toString(mUProbs[i][j]), mDrawRect.centerX(),
									mDrawRect.centerY() - PROBABILITY_TOP_BUFFER * mScaleFactor, GENERAL_PAINT);
							drawDots(canvas, GENERAL_PAINT, mUProbs[i][j], mDrawRect);
						}
					}
				}
			}
		}
		
		CatanMap map = mMapSize.mapProvider.get();
		
		for (int i = 0; i < mHarbors.size(); i++) {
			if (mHarbors.get(i) != null) {
				Point point = getCatanMap().waterGrid[i];
				
				adjustDrawRect(mPiecesX[point.x][point.y], mPiecesY[point.x][point.y]);
				
				drawHarbor(map, canvas, GENERAL_PAINT, mHarbors.get(i), mDrawRect, i);
			}
		}

		if (mPlacementBookmark >= 0) {
			int placementIndex = mOrderedPlacements.get(mPlacementBookmark);

			// Ignore any places that aren't allowed settlements on the coast
			if (map.placementIndexes[placementIndex].length == 2) {
				int landNeighbor = map.placementIndexes[placementIndex][0];
				int landDirection = map.placementIndexes[placementIndex][1];

				Point point = map.landGrid[landNeighbor];
				adjustDrawRect(mPiecesX[point.x][point.y], mPiecesY[point.x][point.y]);
				float x = mDrawRect.centerX() + calculatePlacementOffsetX(landDirection);
				float y = mDrawRect.centerY() + calculatePlacementOffsetY(landDirection);

				// Draw circle
				canvas.drawCircle(x, y, PLACEMENT_DOT_RADIUS * mScaleFactor, PROB_DOTS_PAINT);

				// Draw reasons box
				drawReasonsBox(canvas, mPlacementBookmark, mPlacements.get(placementIndex), x, y);
			}
		}
		
		//canvas.restore();
	}
	
	private void adjustDrawRect(int x, int y) {
		// mMaxX gets + 1 because of the overlap of the horizontal counting hexes
		// we need to count the last half-hex hanging off the right edge
		// not sure why mMaxY needs it too though :P
		float newGridX = (mMaxX / 2f) + 1 - x;
		float newGridY = (mMaxY / 2f) + 1 - y;
		float startX = (getWidth() / 2) - (newGridX * (XR + BORDER_WIDTH)) * mScaleFactor + mDiffX;
		float startY = (getHeight() / 2) - (newGridY * (YR1 + YR2 + BORDER_WIDTH)) * mScaleFactor + mDiffY;
		
		mDrawRect.set((int) startX, (int) startY,
				(int) (startX + (2 * XR * mScaleFactor)),
				(int) (startY + (((2 * YR2) + YR1) * mScaleFactor)));
	}

	private void drawReasonsBox(Canvas canvas, int index, List<String> reasons, float x, float y) {
		float yLevel = y - PLACEMENT_TRIANGLE_HEIGHT;
		mReasonsRectF.set(	x - dPlacementReasonBoxWidth / 3,
				yLevel - (reasons.size() + 1) * dPlacementReasonBoxLineHeight, // +1 since words are bottom aligned
				x + dPlacementReasonBoxWidth * 2/3,
				yLevel);
		canvas.drawRoundRect(mReasonsRectF, 5.0f, 5.0f, REASON_PAINT);

		// Draw bubble connector
		BUBBLE_PATH.reset();
		BUBBLE_PATH.moveTo(x, y);
		BUBBLE_PATH.lineTo(x - XR, yLevel);
		BUBBLE_PATH.lineTo(x, yLevel);
		BUBBLE_PATH.lineTo(x, y);
		canvas.drawPath(BUBBLE_PATH, REASON_PAINT);

		TEXT_PAINT.setTextSize(dReasonFontSize + 3);

		// +1 for zero-indexing
		canvas.drawText("#" + (index + 1),
				x - dPlacementReasonBoxWidth / 3 + XR / 2,
				y - PLACEMENT_TRIANGLE_HEIGHT - reasons.size() * dPlacementReasonBoxLineHeight / 2,
				TEXT_PAINT);

		yLevel -= reasons.size() * dPlacementReasonBoxLineHeight;
		for (String reason : reasons) {
			canvas.drawText(reason, x - dPlacementReasonBoxWidth / 3 + dReasonFontBufferMultiplier * XR, yLevel, TEXT_PAINT);
			yLevel += dPlacementReasonBoxLineHeight;
		}
	}

	private void drawDots(Canvas canvas, Paint paint, int prob, Rect rect) {
		float x = rect.centerX();
		float y = rect.centerY() + YR1 / 3f * mScaleFactor;
		paint.setAntiAlias(false);
		switch (PROBABILITY_MAPPING[prob]) {
		case 5:
			canvas.drawCircle(x - (XR / 2 * mScaleFactor), y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
			canvas.drawCircle(x + (XR / 2 * mScaleFactor), y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
		case 3:
			canvas.drawCircle(x - (XR / 4 * mScaleFactor), y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
			canvas.drawCircle(x + (XR / 4 * mScaleFactor), y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
		case 1:
			canvas.drawCircle(x, y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
			break;
		case 4:
			canvas.drawCircle(x - (XR * 3/8 * mScaleFactor), y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
			canvas.drawCircle(x + (XR * 3/8 * mScaleFactor), y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
		case 2:
			canvas.drawCircle(x - (XR * 1/8 * mScaleFactor), y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
			canvas.drawCircle(x + (XR * 1/8 * mScaleFactor), y, PROBABILITY_DOT_RADIUS * mScaleFactor, paint);
			break;
		case 0:
		default:
		}
		paint.setAntiAlias(true);
	}

	private void drawHarbor(CatanMap map, Canvas canvas, Paint paint, Harbor harbor, Rect rect, int i) {
		float x = rect.centerX();
		float y = rect.centerY() - (PROBABILITY_TOP_BUFFER + BORDER_WIDTH) * mScaleFactor;
		int dir = MapLogic.whichWayHarborFaces(map, harbor);

		if (harbor.resource == Resource.WATER || dir == -1) {
			return; // Do nothing
		}
		
		if (harbor.resource == Resource.DESERT) {
			paint.setColor(0xFFFFFFFF);
			canvas.drawCircle(x, y, HARBOR_CIRCLE_RADIUS * mScaleFactor, paint);
			drawHarborLine(canvas, paint, map.harborLines[i][dir], x, y);
			drawHarborLine(canvas, paint, map.harborLines[i][dir + 1], x, y);
			paint.setColor(0xFF000000);
			canvas.drawText("3", x, y - 1.5f * PROBABILITY_TOP_BUFFER * mScaleFactor, paint);
		} else {
			paint.setColor(harbor.resource.color);
			canvas.drawCircle(x, y, HARBOR_CIRCLE_RADIUS * mScaleFactor, paint);
			drawHarborLine(canvas, paint, map.harborLines[i][dir], x, y);
			drawHarborLine(canvas, paint, map.harborLines[i][dir + 1], x, y);
			paint.setColor(0xFF000000);
			canvas.drawText("2", x, y - 1.5f * PROBABILITY_TOP_BUFFER * mScaleFactor, paint);
		}
	}

	private void drawHarborLine(Canvas canvas, Paint paint, int dir, float x, float y) {
		switch(dir) {
		case 0:
			canvas.drawLine(x, y, x - XR * mScaleFactor, y - (YR1 / 2) * mScaleFactor, paint);
			break;
		case 1:
			canvas.drawLine(x, y, x, y - (YR2 + (YR1 / 2)) * mScaleFactor, paint);
			break;
		case 2:
			canvas.drawLine(x, y, x + XR * mScaleFactor, y - (YR1 / 2) * mScaleFactor, paint);
			break;
		case 3:
			canvas.drawLine(x, y, x + XR * mScaleFactor, y + (YR1 / 2) * mScaleFactor, paint);
			break;
		case 4:
			canvas.drawLine(x, y, x, y + (YR2 + (YR1 / 2)) * mScaleFactor, paint);
			break;
		case 5:
			canvas.drawLine(x, y, x - XR * mScaleFactor, y + (YR1 / 2) * mScaleFactor, paint);
			break;
		default:
			BetterLog.i("WARNING: Cannot draw this line: "  +  dir);
			break;
		}
	}

	private float calculatePlacementOffsetX(int num) {
		float temp;
		switch (num) {
		case 0:
			temp = -(XR + BORDER_WIDTH);
			break;
		case 1:
			temp = 0;
			break;
		case 2:
			temp = XR + BORDER_WIDTH;
			break;
		case 3:
			temp = XR + BORDER_WIDTH;
			break;
		case 4:
			temp = 0;
			break;
		case 5:
			temp = -(XR + BORDER_WIDTH);
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
			temp = -YR1/2;
			break;
		case 1:
			temp = -(YR2 + YR1/2);
			break;
		case 2:
			temp = -YR1/2;
			break;
		case 3:
			temp = YR1/2;
			break;
		case 4:
			temp = YR2 + YR1/2;
			break;
		case 5:
			temp = YR1/2;
			break;
		default:
			temp = 0;
			break;
		}
		return (temp - (PROBABILITY_TOP_BUFFER + (2 * BORDER_WIDTH))) * mScaleFactor;
	}
}
