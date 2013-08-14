/**
 * Copyright 2011 Alex Vikulov (vikuloff@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.stockchart;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Area;
import org.stockchart.core.Axis;
import org.stockchart.core.Axis.Side;
import org.stockchart.core.AxisRange;
import org.stockchart.core.ChartElement;
import org.stockchart.core.Crosshair;
import org.stockchart.core.IndicatorManager;
import org.stockchart.core.Plot;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.indicators.AbstractIndicator;
import org.stockchart.points.AbstractPoint;
import org.stockchart.series.AbstractSeries;
import org.stockchart.series.BarSeries;
import org.stockchart.series.SeriesBase;
import org.stockchart.series.StockSeries;
import org.stockchart.stickers.AbstractSticker;
import org.stockchart.utils.CustomObjects;
import org.stockchart.utils.DrawingCache;
import org.stockchart.utils.DrawingCache.Params;
import org.stockchart.utils.PointD;
import org.stockchart.utils.StockDataGenerator;
import org.stockchart.utils.StockDataGenerator.Point;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class StockChartView extends View
{
	private enum TouchMode
	{
		NONE,
		ZOOM,
		DRAG
	}
	
	
	public static final int HIT_TEST_DEEP = 255;
	public static final int HIT_TEST_SHALLOW = 0;
	public static final int HIT_TEST_SERIES = 1;
	public static final int HIT_TEST_STICKERS = 2;
	
	private static final int STICKER_AUTO_POINT = 0;
	private static final int STICKER_FIRST_POINT = 202020;
	private static final int STICKER_SECOND_POINT = 303030;
	private static final int STICKER_MID_POINT = 404040;
	
	public class HitTestInfo
	{
		public ChartElement element;		
		public TreeMap<String,AbstractPoint> points;
		public StickerInfo stickerInfo;
		
		public void reset()
		{
			points = null;
			stickerInfo = null;
			element = null;
		}
	}
	
	public class StickerInfo
	{
		public AbstractSticker sticker;
		public int stickerPoint;
		public Area area;
		
		public StickerInfo(Area a, AbstractSticker s)
		{
			area = a;
			sticker = s;
		}
	}
	
	public interface ITouchEventListener
	{
		public void onTouchEvent(MotionEvent e);
	}

	private boolean fInit = false;
	
	// temporary appearance
	private int fClearColor = Color.WHITE;
	
	private final Crosshair fCrosshair = new Crosshair();
	
	private final ArrayList<Area> fAreas = new ArrayList<Area>();
	
	//private final ArrayList<AbstractIndicator> fIndicators = new ArrayList<AbstractIndicator>();
	
	private final IndicatorManager fIndicatorManager = new IndicatorManager(this);
	
	private final EnumMap<Axis.Side, AxisRange> fGlobalRanges = new EnumMap<Axis.Side, AxisRange>(Axis.Side.class);

	private TouchMode fMode = TouchMode.NONE;
	
	private boolean fTouchEventHandled = false;
	
	private float fOldDist = 1;
	private final PointF fDragStartPoint = new PointF();
	private final PointD fStickerMidPoint = new PointD();
	private final PointD fStickerFirstPoint = new PointD();
	private final PointD fStickerSecondPoint = new PointD();
	
	private ITouchEventListener fTouchEventUpListener;
	
	private final CustomObjects fCustomObjects = new CustomObjects();
	
	private StickerInfo fStickerInfo;
	
	private final HitTestInfo fInnerHitTestInfo = new HitTestInfo();
	
	private DrawingCache fCache = new DrawingCache();
	
	public StockChartView(Context context)
	{
		super(context);
	
		this.setFocusable(true);
		init();
	}
	
	public StockChartView(Context context, AttributeSet attrs) {
	    this(context, attrs, 0);
	    this.setFocusable(true);
	    
	    init();
	}

	public StockChartView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    this.setFocusable(true);
	    
	    init();
	}
		
	private void init()
	{		
		if(fInit) return;
				
		this.setWillNotDraw(false);
		this.fCustomObjects.put(CustomObjects.CROSSHAIR, fCrosshair);
			
		if(this.isInEditMode())
		{
			doEditMode();
		}
		
		fInit = true;
	}
	
	public void letTheUserGlueSticker(Area area, AbstractSticker s)
	{
		this.fStickerInfo = new StickerInfo(area, s);
	}
	
	public Crosshair getCrosshair()
	{
		return fCrosshair;
	}
	

	public ITouchEventListener getTouchEventUpListener()
	{
		return fTouchEventUpListener;
	}
	
	public void setTouchEventUpListener(ITouchEventListener ev)
	{
		fTouchEventUpListener = ev;
	}
	
	public Area addArea()
	{
		Area a = new Area();
		fAreas.add(a);
		return a;
	}
	
	public ArrayList<Area> getAreas()
	{		
		return fAreas;
	}
	
	public IndicatorManager getIndicatorManager()
	{
		return fIndicatorManager;
	}
	
	@Deprecated
	public ArrayList<AbstractIndicator> getIndicators()
	{
		return fIndicatorManager.getIndicators();
	}
	
	public void setClearColor(int color)
	{
		fClearColor = color;
	}
	
	public int getClearColor()
	{
		return fClearColor;
	}
	
	public void reset()
	{
		fAreas.clear();		
		fGlobalRanges.clear();
		fIndicatorManager.getIndicators().clear();
	}
	
	public Area findAreaBySeriesName(String seriesName)
	{
		for(Area a:fAreas)
		{
			SeriesBase s = a.findSeriesByName(seriesName);
			if(null != s)
				return a;
		}
		
		return null;
	}
	
	public SeriesBase findSeriesByName(String name)	
	{
		for(Area a:fAreas)
		{
			SeriesBase s = a.findSeriesByName(name);
			if(null != s)
				return s;
		}
		
		return null;
	}
	
	public Area findAreaByName(String name)
	{
		for(Area a:fAreas)
		{
			if(a.getName().equals(name)) return a;
		}
		
		return null;
	}
	
	public void load(String s) throws JSONException
	{
		reset();
		
		JSONObject j = new JSONObject(s);
		if(j.has("areas"))
		{
			JSONArray areas = j.getJSONArray("areas");
			for(int i=0;i<areas.length();i++)
			{
				JSONObject area = areas.getJSONObject(i);
				
				Area a = new Area();
				a.fromJSONObject(area);
				
				fAreas.add(a);
			}
		}
		
		if(j.has("globalAxisRanges"))
		{
			JSONArray ranges = j.getJSONArray("globalAxisRanges");
			for(int i=0;i<ranges.length();i++)
			{
				JSONObject range = ranges.getJSONObject(i);
				AxisRange ar = new AxisRange();
				Axis.Side side = Axis.Side.valueOf(range.getString("side"));
				ar.fromJSONObject(range.getJSONObject("axisRange"));
				
				fGlobalRanges.put(side, ar);
			}
		}
		
		if(j.has("indicators"))
		{
			JSONArray indicators = j.getJSONArray("indicators");
			fIndicatorManager.fromJSONArray(indicators);
		}
		
		fCrosshair.fromJSONObject(j.getJSONObject("crosshair"));
		
		fClearColor = j.getInt("clearColor");
	}
	
	public String save() throws JSONException
	{
		JSONObject view = new JSONObject();
		JSONArray areas = new JSONArray();
		
		for(Area a: fAreas)
		{									
			areas.put(a.toJSONObject());
		}
		
		view.put("areas", areas);
		
		JSONArray ranges = new JSONArray();
		Iterator<Entry<Axis.Side, AxisRange>> iterator = this.fGlobalRanges.entrySet().iterator();

		while(iterator.hasNext())
		{
			JSONObject range = new JSONObject();
			
			Entry<Axis.Side, AxisRange> i = iterator.next();
			range.put("side", i.getKey());
			range.put("axisRange", i.getValue().toJSONObject());
			
			ranges.put(range);
		}
		
		view.put("globalAxisRanges", ranges);			
		view.put("indicators", fIndicatorManager.toJSONArray());
		
		view.put("clearColor", fClearColor);
		view.put("crosshair", fCrosshair.toJSONObject());
		
		return view.toString();
	}
	

	public void recalcIndicators()
	{
		for(AbstractIndicator a: fIndicatorManager.getIndicators())
		{
			a.recalc();
		}
	}
	
	public AxisRange getGlobalAxisRange(Axis.Side side)
	{
		return fGlobalRanges.get(side);
	}
	
	public void disableGlobalAxisRange(Axis.Side side)
	{
		fGlobalRanges.remove(side);
	}
	
	public void enableGlobalAxisRange(Axis.Side side, AxisRange value)
	{
		fGlobalRanges.put(side, value);
	}
	
	
	public void resetView()
	{
		Set<Entry<Axis.Side, AxisRange>> entrySet = fGlobalRanges.entrySet();
		Iterator<Entry<Axis.Side, AxisRange>> iterator = entrySet.iterator();

		// calc global zoom first
		while(iterator.hasNext())
		{
			Entry<Axis.Side, AxisRange> i = iterator.next();
			i.getValue().resetViewValues();
		}
		
		for(Area a: fAreas)
		{			 
			for(Axis axis:a.getAxes())
			{
				axis.getAxisRange().resetViewValues();
			}
		}
	}
	
	public void zoom(float hFactor, float vFactor)
	{
		Set<Entry<Axis.Side, AxisRange>> entrySet = this.fGlobalRanges.entrySet();
		Iterator<Entry<Axis.Side, AxisRange>> iterator = entrySet.iterator();

		// calc global zoom first
		while(iterator.hasNext())
		{
			Entry<Axis.Side, AxisRange> i = iterator.next();
			
			if(Axis.isHorizontal(i.getKey()))
				i.getValue().zoomViewValues(hFactor);
			else if(Axis.isVertical(i.getKey()))
				i.getValue().zoomViewValues(vFactor);
		}
		
		
		for(Area a: fAreas)
		{			 
			if(!a.isVisible()) continue;
			
			Axis[] axes = a.getAxes();
			
			for(Axis axis:axes)
			{
				if(!fGlobalRanges.containsKey(axis.getSide()))
				{
					if(axis.isHorizontal())
						axis.getAxisRange().zoomViewValues(hFactor);
					else if(axis.isVertical())
						axis.getAxisRange().zoomViewValues(vFactor);
				}
			}
		}
	}
	
	public void move(float hFactor, float vFactor)
	{
		Set<Entry<Axis.Side, AxisRange>> entrySet = this.fGlobalRanges.entrySet();
		Iterator<Entry<Axis.Side, AxisRange>> iterator = entrySet.iterator();

		// calc global zoom first
		while(iterator.hasNext())
		{
			Entry<Axis.Side, AxisRange> i = iterator.next();
			
			if(Axis.isHorizontal(i.getKey()))
				i.getValue().moveViewValues(hFactor);
			else if(Axis.isVertical(i.getKey()))
				i.getValue().moveViewValues(vFactor);
		}
		
		
		for(Area a: fAreas)
		{			 
			if(!a.isVisible()) continue;
			
			Axis[] axes = a.getAxes();
			
			for(Axis axis:axes)
			{
				if(!fGlobalRanges.containsKey(axis.getSide()))
				{
					if(axis.isHorizontal())
						axis.getAxisRange().moveViewValues(hFactor);
					else if(axis.isVertical())
						axis.getAxisRange().moveViewValues(vFactor);
				}
			}
		}
	}
	
	protected void onDraw(Canvas canvas) 
	{	
		super.onDraw(canvas);
		
		Params p = fCache.getParams(canvas);
				
		recalc();
		resetPositions();
						
		drawClear(p.bitmap);
		drawAreas(p.canvas);
		
		canvas.drawBitmap(p.bitmap,0f,0f,null);
	}


	@Override
	protected void onDetachedFromWindow() 
	{
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		
		this.fCache.recycle();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
	
	    int desiredWidth = 100;
	    int desiredHeight = 100;

	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    int width;
	    int height;

	    //Measure Width
	    if (widthMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        width = widthSize;
	    } else if (widthMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        width = Math.min(desiredWidth, widthSize);
	    } else {
	        //Be whatever you want
	        width = desiredWidth;
	    }

	    //Measure Height
	    if (heightMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        height = heightSize;
	    } else if (heightMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        height = Math.min(desiredHeight, heightSize);
	    } else {
	        //Be whatever you want
	        height = desiredHeight;
	    }

	    //MUST CALL THIS
	    setMeasuredDimension(width, height);
	}
	
	public boolean onTouchEvent(MotionEvent ev)
	{
		preHandleTouchEvent(ev);
		
		if(fStickerInfo != null)
			return handleTouchStickerEvent(ev);
		
		return handleTouchEvent(ev);
	}
	
	private void preHandleTouchEvent(MotionEvent ev)
	{
		if(null != fStickerInfo) return;
		
		this.getHitTestInfo(fInnerHitTestInfo, ev.getX(), ev.getY(), HIT_TEST_STICKERS);
		
		if(null != fInnerHitTestInfo.stickerInfo)
		{
			fStickerInfo = fInnerHitTestInfo.stickerInfo;
		}
	}
	
	private boolean handleTouchStickerEvent(MotionEvent ev)
	{
		int e = ev.getAction() & MotionEvent.ACTION_MASK;
 		float x = ev.getX();
 		float y = ev.getY();

		Plot stickerPlot = fStickerInfo.area.getPlot();
			
		switch (e) 
		{
			case MotionEvent.ACTION_DOWN:
			{	
				if(stickerPlot.getAbsoluteBounds().contains(x, y))
				{
					double vx = fStickerInfo.area.getValueByCoordinate(fStickerInfo.sticker.getHorizontalAxis(), x, true);
					double vy = fStickerInfo.area.getValueByCoordinate(fStickerInfo.sticker.getVerticalAxis(), y, true);
					
					switch(fStickerInfo.stickerPoint)
					{
					case STICKER_FIRST_POINT:
						fStickerInfo.sticker.setFirstPoint(vx, vy);
						break;
					case STICKER_SECOND_POINT:
						fStickerInfo.sticker.setSecondPoint(vx, vy);
						break;
					case STICKER_MID_POINT:
						{
							this.fStickerMidPoint.set(vx, vy);
							this.fStickerFirstPoint.set(fStickerInfo.sticker.getX1(), fStickerInfo.sticker.getY1());
							this.fStickerSecondPoint.set(fStickerInfo.sticker.getX2(), fStickerInfo.sticker.getY2());
						}
						break;
					default:
						{
							fStickerInfo.sticker.setFirstPoint(vx, vy);
							this.fCustomObjects.put(CustomObjects.STICKER, fStickerInfo);
						}
					}
					
					this.invalidate();
				}
			}
			break;
			case MotionEvent.ACTION_UP:
		 	{
		 		if(fStickerInfo.sticker.isValid() && fStickerInfo.stickerPoint == STICKER_AUTO_POINT)
		 			fStickerInfo.area.getStickers().add(fStickerInfo.sticker);
		 		
		 		this.fCustomObjects.remove(CustomObjects.STICKER);
		 		fStickerInfo = null;
		 		this.invalidate();

		 	}
            break;
		 	case MotionEvent.ACTION_MOVE:
		 	{	
		 		RectF absoluteBounds = stickerPlot.getAbsoluteBounds();
		 		
		 		if(!absoluteBounds.contains(x, y))
		 		{
		 			if(x < absoluteBounds.left)
		 				x = absoluteBounds.left;
		 			else if(x > absoluteBounds.right)
		 				x = absoluteBounds.right;
		 			
		 			if(y < absoluteBounds.top)
		 				y = absoluteBounds.top;
		 			else if(y > absoluteBounds.bottom)
		 				y = absoluteBounds.bottom;
		 		}
		 		
		 		double vx = fStickerInfo.area.getValueByCoordinate(fStickerInfo.sticker.getHorizontalAxis(), x, true);
				double vy = fStickerInfo.area.getValueByCoordinate(fStickerInfo.sticker.getVerticalAxis(), y, true);
				
				if(fStickerInfo.stickerPoint == STICKER_FIRST_POINT)
					fStickerInfo.sticker.setFirstPoint(vx, vy);
				else if(fStickerInfo.stickerPoint == STICKER_MID_POINT)
				{
					double dx = vx - this.fStickerMidPoint.x;
					double dy = vy - this.fStickerMidPoint.y;
					
					fStickerInfo.sticker.setFirstPoint(fStickerFirstPoint.x + dx, fStickerFirstPoint.y + dy);
					fStickerInfo.sticker.setSecondPoint(fStickerSecondPoint.x  + dx, fStickerSecondPoint.y + dy);
				}
				else
					fStickerInfo.sticker.setSecondPoint(vx, vy);

				this.invalidate();
		 	}
		 	break;
		 		
		}

		return true;
	}
	
	private boolean handleTouchEvent(MotionEvent ev)
	{
		int e = ev.getAction() & MotionEvent.ACTION_MASK;
		
		switch (e) 
		{
			case MotionEvent.ACTION_DOWN:
			{				
				 fMode = TouchMode.DRAG;
				 fDragStartPoint.set(ev.getX(), ev.getY());					 
			}
			break;
			case MotionEvent.ACTION_POINTER_DOWN:
			{						 
				 fOldDist = getSpacing(ev);
				 
	             if (fOldDist > 10)           	 	            	 
	            	 fMode = TouchMode.ZOOM;	            	
			}		
            break;
			case MotionEvent.ACTION_UP:
		 	case MotionEvent.ACTION_POINTER_UP:
		 	{
		 		if(e == MotionEvent.ACTION_UP)
		 		{
		 			if(!fTouchEventHandled)
		 				this.onTouchEventUp(ev);
			 		
		 			fTouchEventHandled = false;
		 		}
		 		
		 		if(fMode == TouchMode.DRAG)
		 			processCrosshair(ev.getX(),ev.getY());
	 			
		 		fMode = TouchMode.NONE;
		 	}
            break;
		 	case MotionEvent.ACTION_MOVE:
		 	{		 				 	

	 			if(TouchMode.ZOOM == fMode)
	 			{	 				
	 				float newDist = getSpacing(ev);		 				
	 									  
	 				if(newDist > 10f)
	 				{
	 					float scale = newDist - fOldDist;

	 					this.zoom(inPercentsOfWidth(scale), inPercentsOfHeight(scale));
	 					
	 					invalidate();
	 					fTouchEventHandled = true;
	 				}
	 				
	 			}
	 			else if(TouchMode.DRAG == fMode)
	 			{
	 				float dx = fDragStartPoint.x - ev.getX();
	 				float dy = ev.getY() - fDragStartPoint.y;
	 				
	 				if(dx != 0f || dy != 0f)
	 				{
	 					this.move(inPercentsOfWidth(dx), inPercentsOfHeight(dy));
	 				
	 					invalidate();
	 					fTouchEventHandled = true;
	 				}
	 			}
	 			
	 			processCrosshair(ev.getX(),ev.getY());
		 	}
		 	break;
		 		
		}

		return true;
	}
	
	private void processCrosshair(float x, float y)
	{
		if(!this.fCrosshair.isAuto()) return;

		this.getHitTestInfo(fInnerHitTestInfo, x, y, HIT_TEST_SHALLOW);
		
		if(fInnerHitTestInfo.element instanceof Plot)
		{
			this.fCrosshair.setPositionInPercents(inPercentsOfWidth(x), inPercentsOfHeight(y));
			this.fCrosshair.setVisible(true);
			this.invalidate();
		}
	}
	
	private void onTouchEventUp(MotionEvent ev)
	{
		if(null != this.fTouchEventUpListener)
			this.fTouchEventUpListener.onTouchEvent(ev);
	}

	
	
	
	
	
	private float inPercentsOfHeight(float value)
	{
		return value / (float)this.getHeight();
	}
	
	private float inPercentsOfWidth(float value)
	{
		return value / (float)this.getWidth();
	}
	
	private void calcGlobalValues()
	{
		Set<Entry<Axis.Side, AxisRange>> entrySet = this.fGlobalRanges.entrySet();
		Iterator<Entry<Axis.Side, AxisRange>> iterator = entrySet.iterator();

		while(iterator.hasNext())
		{
			Entry<Axis.Side, AxisRange> i = iterator.next();
			
			AxisRange dst = i.getValue();
			dst.resetAutoValues();
			
			if(dst.isAuto())
			{
				for(Area a : fAreas)
				{
					if(!a.isVisible()) continue;
					
					Axis axis = a.getAxis(i.getKey());	
					AxisRange src = axis.getAxisRange();
					
					dst.expandAutoValues(src.getMaxOrAutoValue(), src.getMinOrAutoValue());
				}
			}
		}
		
		for(Area a:fAreas)
		{
			if(!a.isVisible()) continue;
			
			Axis[] axes = a.getAxes();
			
			for(Axis axis:axes)
			{
				axis.setGlobalAxisRange(fGlobalRanges.get(axis.getSide()));
			}
		}	
	}
	
	public void recalc()
	{
		// set local maxmin x and y
		for(Area a:fAreas)
		{
			if(a.isVisible())
				a.calcAutoValues();
		}

		// set globals
		calcGlobalValues();
		
		// set y again if global x changed 
		for(Area a:fAreas)
		{
			if(a.isVisible())
				a.calcYAutoValues();
		}
	}
	
	private void doEditMode()
	{
		StockSeries price = new StockSeries();
		price.setName("price");				
		price.setYAxisSide(Side.RIGHT);
		
		BarSeries volume = new BarSeries();
		volume.setName("volume");
		volume.setYAxisSide(Side.LEFT);
		
		Area a = this.addArea();
		a.getLeftAxis().setVisible(false);
		a.getTopAxis().setVisible(false);
		a.getSeries().add(volume);
		a.getSeries().add(price);
		
		StockDataGenerator gen = new StockDataGenerator();
		for(int i=0;i<100;i++)
		{
			Point p = gen.getNextPoint();
		
			price.addPoint(p.o, p.h, p.l, p.c);
			volume.addPoint(0.0, p.v);
		}
	}
	
	public HitTestInfo getHitTestInfo(float x, float y)
	{
		return getHitTestInfo(x,y,HIT_TEST_DEEP);
	}
	
	public HitTestInfo getHitTestInfo(float x, float y, int hitTestOptions)
	{
		HitTestInfo info = new HitTestInfo();
		
		getHitTestInfo(info,x,y,hitTestOptions);
		
		return info;
	}
	
	public void getHitTestInfo(HitTestInfo info, float x, float y, int hitTestOptions)
	{	
		info.reset();
		
		resetPositions();
		
		for(Area a : fAreas)
		{
			if(!a.isVisible()) continue;
			
			if(a.getAbsoluteBounds().contains(x, y))
			{				
				if(a.getPlot().getAbsoluteBounds().contains(x,y))
				{					
					info.element = a.getPlot();
					SeriesPaintInfo pinfo = new SeriesPaintInfo();
					RectF rectF = new RectF();

					if(HIT_TEST_STICKERS == (hitTestOptions & HIT_TEST_STICKERS))
					{
						final float r = AbstractSticker.RADIUS;
						
						PointF rel = a.getPlot().getRelativePosition(x, y);
						
						for(AbstractSticker s: a.getStickers())
						{
							pinfo.loadFrom(a.getAxis(s.getHorizontalAxis()),
									a.getAxis(s.getVerticalAxis()));
						
							double midX = s.getMidX();
							double midY = s.getMidY();
							
							// mid point
							rectF.set(pinfo.getX(midX) - r,
									  pinfo.getY(midY) - r,
									  pinfo.getX(midX) + r,
									  pinfo.getY(midY) + r);
							
							if(rectF.contains(rel.x,rel.y))
							{
								info.stickerInfo = new StickerInfo(a,s);
								info.stickerInfo.stickerPoint = STICKER_MID_POINT;
								break;
							}
							
							// first point
							rectF.set(pinfo.getX(s.getX1()) - r,
									  pinfo.getY(s.getY1()) - r,
									  pinfo.getX(s.getX1()) + r,
									  pinfo.getY(s.getY1()) + r
									  );
							
							if(rectF.contains(rel.x,rel.y))
							{
								info.stickerInfo = new StickerInfo(a,s);
								info.stickerInfo.stickerPoint = STICKER_FIRST_POINT;
								break;
							}
							
							// second point
							rectF.set(pinfo.getX(s.getX2()) - r,
									  pinfo.getY(s.getY2()) - r,
									  pinfo.getX(s.getX2()) + r,
									  pinfo.getY(s.getY2()) + r
									  );
							
							if(rectF.contains(rel.x,rel.y))
							{
								info.stickerInfo = new StickerInfo(a,s);
								info.stickerInfo.stickerPoint = STICKER_SECOND_POINT;
								break;
							}
						}
					}
					
					if(HIT_TEST_SERIES == (hitTestOptions & HIT_TEST_SERIES))
					{
						
						for(SeriesBase s: a.getSeries())
						{				
							AbstractSeries<?> as = (AbstractSeries<?>)s;

							pinfo.loadFrom(a.getAxis(as.getXAxisSide()), a.getAxis(as.getYAxisSide()));

							if(!as.isVisibleOnScreen(pinfo.X.Max, pinfo.X.Min)) continue;

							int index = as.convertToArrayIndexZeroBased(pinfo.X.Min);

							for(int i=index;i<as.getPointCount();i++)
							{							
								AbstractPoint ap = as.getPointAt(i);		

								float scaleIndex = as.convertToScaleIndex(i);

								if(ap.isVisible())
								{							
									double[] maxMin = ap.getMaxMin();
									float x1 = pinfo.getX(scaleIndex - 0.5f) + 1f;
									float x2 = pinfo.getX(scaleIndex + 0.5f) - 1f;

									float max = pinfo.getY(maxMin[0]);
									float min = pinfo.getY(maxMin[1]);

									if(maxMin[0] == maxMin[1])
									{
										float d = (x2-x1)/2f; 
										max -= d;
										min += d;
									}


									PointF p = a.getPlot().getAbsolutePosition(x1,max);								
									rectF.set(p.x, p.y, p.x + (x2-x1), p.y + (min-max));

									if(rectF.contains(x, y))
									{
										if(null == info.points)
											info.points =  new TreeMap<String,AbstractPoint>();
										
										info.points.put(s.getName(), ap);
										break;
									}

								}

								if(scaleIndex > pinfo.X.Max)
									break;
							}
						}
					}
						
				}
				else
				{
					for(Axis axis : a.getAxes())
					{
						if(axis.getAbsoluteBounds().contains(x,y))
						{
							info.element = axis;
							break;
						}
					}
				}
				
				if(info.element == null)
					info.element = a;
				
				break;
			}
		}
	}
	
	public void resetPositions()
	{
		float y = 0f;
		float pxAutoHeight = this.getAreaAutoHeightInPixels();
		float pxWidth = this.getWidth();
		float pxHeight = this.getHeight();
		
		final float[] lr = getLeftRightMargins();
		
		for(Area a:fAreas)
		{
			if(!a.isVisible()) continue;
			
			float height = a.isAutoHeight()? pxAutoHeight : a.getHeightInPercents() * pxHeight;
			
			RectF rect = new RectF(0,y,pxWidth,y+height);

			a.setGlobalMargins(lr[0], lr[1]);
			
			a.setBounds(0f, rect.top, rect.width(), rect.height());
			
			y+=height;
		}
	}

	private void drawAreas(Canvas c)
	{		
		for(Area a: fAreas)
		{
			if(!a.isVisible()) continue;
			
			a.draw(c,fCustomObjects);
		}
	}

	
	private void drawClear(Bitmap b)
	{		
		b.eraseColor(fClearColor);
	}
			

	private float[] getLeftRightMargins()
	{
		float leftMargin = Float.MIN_VALUE;
		float rightMargin = Float.MIN_VALUE;
		
		for(Area a: fAreas)
		{
			if(!a.isVisible()) continue;
			
			RectF r = a.getSideMargins();
			
			if(r.left > leftMargin)
				leftMargin = r.left;
			
			if(r.right > rightMargin)
				rightMargin =r.right;
		}
		
		return new float[] { leftMargin, rightMargin };
	}
	
	private float getAreaAutoHeightInPixels()
	{
		int autoAreaCount = 0;
		
		final float totalHeight = this.getHeight();
		
		float remainingHeight = totalHeight;
		
		for(Area a: fAreas)
		{
			if(!a.isVisible()) continue;
			
			if(a.isAutoHeight())
				autoAreaCount++;
			else
				remainingHeight-=a.getHeightInPercents() * totalHeight;
		}			

		if(0 == autoAreaCount)
			return Float.NaN;
		
		if(remainingHeight < 0)
			return 0f;
		
		return (float)remainingHeight/(float)autoAreaCount; 
	}
		
	private static float getSpacing(MotionEvent event) 
	{
		   float x = event.getX(0) - event.getX(1);
		   float y = event.getY(0) - event.getY(1);
		   
		   return (float)android.util.FloatMath.sqrt(x * x + y * y);
	}
}
