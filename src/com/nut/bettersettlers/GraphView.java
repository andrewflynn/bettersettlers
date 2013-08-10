package com.nut.bettersettlers;

import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.View;

import com.bettersettlers.R;

public class GraphView extends View {
	
	private int expectedUnit;
	private int startingX;
	private int startingY;
	private int barStartingX;
	private int barStartingY;
	private int barWidth;
	private int expectedBarWidth;
	private int incrementX;
	private int textSize;
	private int buffer;
	
	private Stack<Integer> stack;
	private int[] expected;	
	private int[] probs;
	private int[] robberProbs;
	
	private PowerManager.WakeLock dontSleep;

  public GraphView(Context context) {
  	super(context);
  	
  	initialize(context);
  }
  
  public GraphView(Context context, AttributeSet attrs) {
  	super(context, attrs);
  	
  	initialize(context);
  }
  
  public void aquireSleepLock() {
  	dontSleep.acquire();
  }
  
  public void releaseSleepLock() {
  	if (dontSleep.isHeld()) {
    	dontSleep.release();
  	}
  }
  
  private void initialize(Context context) {
  	PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
  	dontSleep = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "graph tag");
  	
  	stack = new Stack<Integer>();

  	expectedUnit = (int) context.getResources().getDimension(R.dimen.graph_expected_unit);
  	startingX = (int) context.getResources().getDimension(R.dimen.graph_starting_x);
  	startingY = (int) context.getResources().getDimension(R.dimen.graph_starting_y);
  	barStartingX = (int) context.getResources().getDimension(R.dimen.graph_bar_starting_x);
  	barStartingY = (int) context.getResources().getDimension(R.dimen.graph_bar_starting_y);
  	barWidth = (int) context.getResources().getDimension(R.dimen.graph_bar_width);
  	expectedBarWidth = (int) context.getResources().getDimension(R.dimen.graph_expected_bar_width);
  	incrementX = (int) context.getResources().getDimension(R.dimen.graph_increment_x);
  	textSize = (int) context.getResources().getDimension(R.dimen.graph_text_size);
  	buffer = (int) context.getResources().getDimension(R.dimen.probability_top_buffer);
  	
  	expected = new int[] {
  			 -1                 /* 0 */,
         -1                 /* 1 */,
         expectedUnit       /* 2 */,
         expectedUnit * 2   /* 3 */,
         expectedUnit * 3   /* 4 */,
         expectedUnit * 4   /* 5 */,
         expectedUnit * 5   /* 6 */,
         expectedUnit * 6   /* 7 */,
         expectedUnit * 5   /* 8 */,
         expectedUnit * 4   /* 9 */,
         expectedUnit * 3   /* 10 */,
         expectedUnit * 2   /* 11 */,
         expectedUnit * 1   /* 12 */};
  	
  	probs  = new int[] {
  			-1 /* 0 */,
        -1 /* 1 */,
        0  /* 2 */,
        0  /* 3 */,
        0  /* 4 */,
        0  /* 5 */,
        0  /* 6 */,
        0  /* 7 */,
        0  /* 8 */,
        0  /* 9 */,
        0  /* 10 */,
        0  /* 11 */,
        0  /* 12 */};
  	
  	robberProbs  = new int[] {
  			-1 /* 0 */,
        -1 /* 1 */,
        0  /* 2 */,
        0  /* 3 */,
        0  /* 4 */,
        0  /* 5 */,
        0  /* 6 */,
        0  /* 7 */,
        0  /* 8 */,
        0  /* 9 */,
        0  /* 10 */,
        0  /* 11 */,
        0  /* 12 */};
  }
  
  public void setProbs(int[] probs) {
  	this.probs = probs;
  }
  
  public void setRobberProbs(int[] robberProbs) {
  	this.robberProbs = robberProbs;
  }
  
  public void setStack(Stack<Integer> stack) {
  	this.stack = stack;
  }
  
	@Override
  public void onDraw(Canvas canvas) {
		Paint grayPaint = new Paint();
		grayPaint.setColor(0xFFAAAAAA);
		
		Paint redPaint = new Paint();
		redPaint.setColor(0xFFFF0000);
		
		Paint blackPaint = new Paint();
		blackPaint.setColor(0xFF000000);
		blackPaint.setTextSize(textSize);
		blackPaint.setAntiAlias(true);
		blackPaint.setTextAlign(Paint.Align.CENTER);
		blackPaint.setTypeface(Typeface.DEFAULT_BOLD);
		blackPaint.setStrokeWidth(2);
		
		Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(0xFFFFFFFF);
		canvas.drawPaint(backgroundPaint);
		
		int x = startingX;
		int y = startingY;
		for (int i = 2; i <= 12; i++) {
			canvas.drawText(Integer.toString(i), x, y, blackPaint);
			x += incrementX;
		}
		
		
		x = barStartingX;
		y = barStartingY;
		for (int num : expected) {
			if (num != -1) {
  			canvas.drawRoundRect(new RectF(x, y - num, x + expectedBarWidth, y), 5.0f, 5.0f, grayPaint);
	  		x += incrementX;
			}
		}
		
		// Find the max probability and stretch that as far to the left
		// as possible, and make this the common multiplier.
		int max = 1;
		for (int i = 0; i < probs.length; i++) {
			int candidate = robberProbs[i] + probs[i];
			if (max < candidate) {
				max = candidate;
			}
		}
		
		// Multiplier is the farthest to the right of expected values, which is 7
		int multiplier = (expected[7]) / max;

		x = barStartingX;
		y = barStartingY;
		for (int i = 0; i < probs.length; i++) {
			int robberNum = robberProbs[i];
			int num = probs[i];
			int both = robberNum + num;			
			if (robberProbs[i] != -1 && probs[i] != -1) {
				// In order to have rounded only on the bottom for one and top for other if we have both,
				// we overlap each one just short (5 pixels) of the point where they connect
				if (robberProbs[i] > 0 && probs[i] > 0) {
    			canvas.drawRoundRect(new RectF(x, y - (robberNum * multiplier) + 5, x + barWidth, y), 5.0f, 5.0f, redPaint);
    			canvas.drawRect(new RectF(x, y - (robberNum * multiplier), x + barWidth, y - 5), redPaint);
  	  		canvas.drawRoundRect(new RectF(x, y - (both * multiplier), x + barWidth, y - (robberNum * multiplier) - 5), 5.0f, 5.0f, blackPaint);
  		  	canvas.drawRect(new RectF(x, y - (both * multiplier) + 5, x + barWidth, y - (robberNum * multiplier)), blackPaint);
  			  canvas.drawText(Integer.toString(both), x + (startingX - barStartingX), y - (both * multiplier) - buffer, blackPaint);
				} else {
    			canvas.drawRoundRect(new RectF(x, y - (robberNum * multiplier), x + barWidth, y), 5.0f, 5.0f, redPaint);
  	  		canvas.drawRoundRect(new RectF(x, y - (both * multiplier), x + barWidth, y - (robberNum * multiplier)), 5.0f, 5.0f, blackPaint);
  			  canvas.drawText(Integer.toString(both), x + (startingX - barStartingX), y - (both * multiplier) - buffer, blackPaint);
				}
				x += incrementX;
			}
		}
	}
}