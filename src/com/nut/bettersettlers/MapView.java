package com.nut.bettersettlers;

import static com.nut.bettersettlers.MapSpecs.BOARD_RANGE_X;
import static com.nut.bettersettlers.MapSpecs.BOARD_RANGE_Y;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.View;

import com.bettersettlers.R;
import com.nut.bettersettlers.MapSpecs.Harbor;
import com.nut.bettersettlers.MapSpecs.MapSize;
import com.nut.bettersettlers.MapSpecs.Resource;

public class MapView extends View {
	private int starting_x;
	private int starting_y;
	private int standard_starting_x;
	private int y_diff;
	private int xd;
	private int xdt;
	private int yd1;
	private int yd2;
	private int ydt;
	private int hex_size;
	private int harbor_circle;
	private int probability_top_buffer;
	private int probability_dot;
	private int placement_dot;
	private int font_size;
	private int border_width;
	
	private ShapeDrawable[][] pieces;
	private int[][] probs;
	private List<Harbor> harbors;
	private MapSize currentMap;
	
	private int placementBookmark;
	//private int placementIndex;
	private LinkedHashMap<Integer, List<String>> placements;
	private ArrayList<Integer> orderedPlacements;
  
  public MapView(Context context) {
  	super(context);

  	initialize(context);
  }
  
  public MapView(Context context, AttributeSet attrs) {
  	super(context, attrs);
  	
  	initialize(context);
  }
  
  public void initialize(Context context) {
  	starting_x = (int) context.getResources().getDimension(R.dimen.starting_x);
  	starting_y = (int) context.getResources().getDimension(R.dimen.starting_y);
  	xd = (int) context.getResources().getDimension(R.dimen.xd);
  	y_diff = starting_x;
  	standard_starting_x = starting_x + xd;
  	xdt = (int) context.getResources().getDimension(R.dimen.xdt);
  	yd1 = (int) context.getResources().getDimension(R.dimen.yd1);
  	yd2 = (int) context.getResources().getDimension(R.dimen.yd2);
  	ydt = (int) context.getResources().getDimension(R.dimen.ydt);
  	hex_size = (int) context.getResources().getDimension(R.dimen.hex_size);
    harbor_circle = (int) context.getResources().getDimension(R.dimen.harbor_circle);
    probability_top_buffer = (int) context.getResources().getDimension(R.dimen.probability_top_buffer);
    probability_dot = (int) context.getResources().getDimension(R.dimen.probability_dot);
    placement_dot = (int) context.getResources().getDimension(R.dimen.placement_dot);
    font_size = (int) context.getResources().getDimension(R.dimen.font_size);
    border_width = (int) context.getResources().getDimensionPixelOffset(R.dimen.border_width);
  	
  	pieces = new ShapeDrawable[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
  	probs = new int[BOARD_RANGE_X + 1][BOARD_RANGE_Y + 1];
  	harbors = new ArrayList<Harbor>();
  	
  	currentMap = MapSize.STANDARD; // Default to standard
  	placementBookmark = -1; // Don't show placements initially
  	//placementIndex = -1; // Don't show placements initially
  	placements = new LinkedHashMap<Integer, List<String>>();
  	orderedPlacements = new ArrayList<Integer>();
  }
  
  public void setMapType(MapSize currentMap) {
  	this.currentMap = currentMap;
  }

	public void setLandAndWaterResources(Resource[][] land, Harbor[][] water) {
		int this_x = (currentMap.equals(MapSize.STANDARD)) ? standard_starting_x : starting_x - 2;
		
  	Path hexPath = new Path();
  	hexPath.moveTo(this_x, starting_y);
		hexPath.lineTo(this_x + xd, starting_y + yd1);
    hexPath.lineTo(this_x + xd, starting_y + yd1 + yd2);
    hexPath.lineTo(this_x, starting_y + yd1 + yd2 + yd1);
    hexPath.lineTo(this_x - xd, starting_y + yd1 + yd2);
		hexPath.lineTo(this_x - xd, starting_y + yd1);
  	hexPath.lineTo(this_x, starting_y);  	
  	
  	for (int i = 0; i < BOARD_RANGE_Y; i++) {
  		for (int j = (i % 2); j < BOARD_RANGE_X; j += 2) {
  			pieces[j][i] = new ShapeDrawable(new PathShape(hexPath, hex_size, hex_size));
  			if (land != null && land[j][i] != null) {
    			pieces[j][i].getPaint().setColor(land[j][i].getColor());
    			pieces[j][i].setBounds(j * xd + border_width, i * ydt + border_width,
    					j * xd + hex_size - border_width, i * ydt + hex_size - border_width);
  			} else if (water != null && water[j][i] != null) {
  				pieces[j][i].getPaint().setColor(0xFF0000FF);
    			pieces[j][i].setBounds(j * xd + border_width, i * ydt + border_width,
    					j * xd + hex_size - border_width, i * ydt + hex_size - border_width);
  			} else {
  				pieces[j][i].getPaint().setColor(0xFFFFFFFF);
    			pieces[j][i].setBounds(j * xd + border_width, i * ydt + border_width,
    					j * xd + hex_size - border_width, i * ydt + hex_size - border_width);
  			}
  		}
  	}
  }
  
  public void setProbabilities(int[][] probs) {
    this.probs = probs;
  }
  
  public void setHarbors(List<Harbor> harbors) {
  	this.harbors = harbors;
  }
  
  /*
  public void setPlacementIndex(int placementIndex) {
  	this.placementIndex = placementIndex;
  }
  */
  
  public void setPlacementBookmark(int placementBookmark) {
  	this.placementBookmark = placementBookmark;
  }
  
  public void setPlacements(LinkedHashMap<Integer, List<String>> placements) {
  	this.placements = placements;
  }
  
  public void setOrderedPlacements(ArrayList<Integer> orderedPlacements) {
  	this.orderedPlacements = orderedPlacements;
  }
  
  private void drawDotSuggestions(Canvas canvas, int x, int y) {
  	Paint lightPaint = new Paint();
		lightPaint.setColor(0xFF000000);
		lightPaint.setAlpha(200);
		lightPaint.setAntiAlias(true);
		
		canvas.drawCircle(x, y, placement_dot, lightPaint);
  }
  
  private void drawReasonsBox(Canvas canvas, int index, List<String> reasons, int x, int y) {

  	Paint lightPaint = new Paint();
		lightPaint.setColor(0xFF000000);
		lightPaint.setAlpha(175);
		lightPaint.setAntiAlias(true);

		int yLevel = 21/2*ydt - reasons.size()*yd2;
		canvas.drawRoundRect(
				new RectF(standard_starting_x - xd, yLevel,
						standard_starting_x + 6*xdt + xd, 21/2*ydt),
						5.0f, 5.0f, lightPaint);

		// Draw bubble connector
		Path bubblePath = new Path();
		bubblePath.moveTo(x, y);
		if (x < (standard_starting_x + 6*xdt + xd) / 2) {
			bubblePath.lineTo(x + xd, yLevel);
			bubblePath.lineTo(x + 2*xd, yLevel);			
		} else {
			bubblePath.lineTo(x - 2*xd, yLevel);
			bubblePath.lineTo(x - xd, yLevel);
		}
		bubblePath.lineTo(x, y);
		canvas.drawPath(bubblePath, lightPaint);

		Paint textPaint = new Paint();
		textPaint.setTextAlign(Paint.Align.LEFT);
		textPaint.setStrokeWidth(1);
		textPaint.setTextSize(font_size);
		textPaint.setAntiAlias(true);
		textPaint.setColor(0xFFFFFFFF);

		// +1 for zero-indexing
		canvas.drawText("#" + (index + 1), standard_starting_x - xd + 5, yLevel + reasons.size() * yd2*2/3, textPaint);

		yLevel += yd2*2/3;
		for (String reason : reasons) {
			canvas.drawText(reason, standard_starting_x - xd + 40, yLevel, textPaint);
			yLevel += yd2;
		}
  }
  
  private void drawDots(Canvas canvas, Paint paint, int prob, int x, int y) {
		paint.setAntiAlias(false);
  	switch (MapSpecs.PROBABILITY_MAPPING[prob]) {
  	case 5:
  		canvas.drawCircle(x-(xd/2), y, probability_dot, paint);
  		canvas.drawCircle(x+(xd/2), y, probability_dot, paint);
  	case 3:
  		canvas.drawCircle(x-(xd/4), y, probability_dot, paint);
  		canvas.drawCircle(x+(xd/4), y, probability_dot, paint);
  	case 1:
  		canvas.drawCircle(x, y, probability_dot, paint);
  		break;
  	case 4:
  		canvas.drawCircle(x-(xd*3/8), y, probability_dot, paint);
  		canvas.drawCircle(x+(xd*3/8), y, probability_dot, paint);
  	case 2:
  		canvas.drawCircle(x-(xd*1/8), y, probability_dot, paint);
  		canvas.drawCircle(x+(xd*1/8), y, probability_dot, paint);
  		break;
  	case 0:
    default:
  	}
		paint.setAntiAlias(true);
  }
  
  private void drawHarbor(Canvas canvas, Paint paint, Harbor harbor, int x, int y, int i) {
  	int dir = MapLogic.whichWayHarborFaces(currentMap, harbor);
		if (harbor.getResource() == Resource.WATER) {
			// Do nothing
		} else if (harbor.getResource() == Resource.DESERT) {
			paint.setColor(0xFFFFFFFF);
			canvas.drawCircle(x+border_width, y+border_width, harbor_circle, paint);
			drawHarborLine(canvas, paint, currentMap.getHarborLines()[i][dir], x+border_width, y+border_width);
			drawHarborLine(canvas, paint, currentMap.getHarborLines()[i][dir+1], x+border_width, y+border_width);
			paint.setColor(0xFF000000);
			canvas.drawText("3", x+border_width, y+border_width+probability_top_buffer, paint);
		} else {
			paint.setColor(harbor.getResource().getColor());
			canvas.drawCircle(x+border_width, y+border_width, harbor_circle, paint);
			drawHarborLine(canvas, paint, currentMap.getHarborLines()[i][dir], x+border_width, y+border_width);
			drawHarborLine(canvas, paint, currentMap.getHarborLines()[i][dir+1], x+border_width, y+border_width);
			paint.setColor(0xFF000000);
			canvas.drawText("2", x+border_width, y+border_width+probability_top_buffer, paint);
		}  	
  }
  
	private void drawHarborLine(Canvas canvas, Paint paint, int dir, int x, int y) {
		switch(dir) {
		case 0:
			canvas.drawLine(x, y, x-xd, y-(yd2/2), paint);
			break;
		case 1:
			canvas.drawLine(x, y, x, y-(yd1+(yd2/2)), paint);
			break;
		case 2:
			canvas.drawLine(x, y, x+xd, y-(yd2/2), paint);
			break;
		case 3:
			canvas.drawLine(x, y, x+xd, y+(yd2/2), paint);
			break;
		case 4:
			canvas.drawLine(x, y, x, y+(yd1+(yd2/2)), paint);
			break;
		case 5:
			canvas.drawLine(x, y, x-xd, y+(yd2/2), paint);
			break;
		default:
			System.out.println("WARNING: Cannot draw this line: " + dir);
			break;
		}
	}
	
	private int calculatePlacementOffsetX(int num) {
		switch (num) {
		case 0:
			return -(xd);
		case 1:
			return 0;
		case 2:
			return xd;
		case 3:
			return xd ;
		case 4:
			return 0;
		case 5:
			return -(xd);
		default:
  		return 0;
		}
	}
	
	private int calculatePlacementOffsetY(int num) {
		switch (num) {
		case 0:
			return -yd1;
		case 1:
			return -(yd1 + yd2/2);
		case 2:
			return -yd1;
		case 3:
			return yd1;
		case 4:
			return yd1 + yd2/2;
		case 5:
			return yd1;
		default:
			return 0;
		}
	}
	
	@Override
  public void onDraw(Canvas canvas) {
		int this_x = (currentMap.equals(MapSize.STANDARD)) ? standard_starting_x : starting_x;
  	Paint paint = new Paint();
  	paint.setTextAlign(Paint.Align.CENTER);
  	paint.setTypeface(Typeface.DEFAULT_BOLD);
  	paint.setStrokeWidth(2);
  	paint.setTextSize(font_size);
		paint.setAntiAlias(true);
  	for (int i = 0; i < pieces.length; i++) {
  		for (int j = 0; j < pieces[i].length; j++) {
  			if (pieces != null && pieces[i][j] != null) {
  				pieces[i][j].draw(canvas);
  				if (probs != null && probs[i][j] != 0) {
  					if (probs[i][j] == 6 || probs[i][j] == 8) {
  						paint.setColor(0xFFD20000);
  					} else {
  						paint.setColor(0xFF000000);
  					}
  	  			canvas.drawText(Integer.toString(probs[i][j]), this_x + i*xd,
  	  					y_diff+probability_top_buffer + j*ydt, paint);
  	  			drawDots(canvas, paint, probs[i][j], this_x + i*xd,
  	  					y_diff+(probability_top_buffer*2) + j*ydt);
  				}
  			}
  		}
  	}
  	for (int i = 0; i < harbors.size(); i++) {
  		if (harbors.get(i) != null) {
      	Point point = currentMap.getWaterGrid()[i];
  			drawHarbor(canvas, paint, harbors.get(i),
  					this_x + point.x*xd,
  					y_diff + point.y*ydt, i);
  		}
  	}

  	if (placementBookmark != -1) {
  		int placementIndex = orderedPlacements.get(placementBookmark);

  		// Ignore any places that aren't allowed settlements on the coast
  		if (currentMap.getPlacementIndexes()[placementIndex].length == 2) {
    		int landNeighbor = currentMap.getPlacementIndexes()[placementIndex][0];
    		int landDirection = currentMap.getPlacementIndexes()[placementIndex][1];
    		
    		Point xAndY = currentMap.getLandGrid()[landNeighbor];
    		
    		// Draw circle
    		drawDotSuggestions(canvas,
    				this_x + xAndY.x*xd + calculatePlacementOffsetX(landDirection),
    				y_diff + xAndY.y*ydt + calculatePlacementOffsetY(landDirection));
    		
    		// Draw reasons box
    		drawReasonsBox(canvas, placementBookmark, placements.get(placementIndex),
    				this_x + xAndY.x*xd + calculatePlacementOffsetX(landDirection),
    				y_diff + xAndY.y*ydt + calculatePlacementOffsetY(landDirection));
  		}
  	}
  }
}
