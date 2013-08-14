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

package org.stockchart.series;

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Appearance;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.core.Theme;
import org.stockchart.core.Appearance.Gradient;
import org.stockchart.points.StockPoint;
import org.stockchart.utils.ColorUtils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class StockSeries extends AbstractSeries<StockPoint>
{
	public enum ViewType
	{
		CANDLESTICK,
		BAR
	}

	private ViewType fViewType = ViewType.CANDLESTICK;
	
	private final Appearance fFallAppearance = new Appearance();
	
	private final Paint fPaint = new Paint();
	
	private final RectF fBodyRect = new RectF();

	public StockSeries()
	{	
		getRiseAppearance().setGradient(Gradient.LINEAR_HORIZONTAL);
		getRiseAppearance().setPrimaryFillColor(ColorUtils.lighten(Color.GREEN,0.5f));
		getRiseAppearance().setSecondaryFillColor(Color.GREEN);
		getRiseAppearance().setOutlineWidth(2f);
		getRiseAppearance().setOutlineColor(ColorUtils.darker(Color.GREEN));
		
		fFallAppearance.setGradient(Gradient.LINEAR_HORIZONTAL);
		fFallAppearance.setPrimaryFillColor(ColorUtils.lighten(Color.RED,0.5f));
		fFallAppearance.setSecondaryFillColor(Color.RED);
		fFallAppearance.setOutlineWidth(2f);
		fFallAppearance.setOutlineColor(ColorUtils.darker(Color.RED));
		
		Theme.fillAppearanceFromCurrentTheme(StockSeries.class, "riseAppearance", getRiseAppearance());
		Theme.fillAppearanceFromCurrentTheme(StockSeries.class, "fallAppearance", getFallAppearance());
	}
	/** 
	    * Gets the appearance of candle/bar if it's close price is greater than open price.
	    * It doesn't create separate appearance. It simply uses <code>getAppearance()</code> method
	    * of the super class, so calling <code>getRiseApperance()</code> or <code>getAppearance()</code>
	    * has the same effect.
	    */
	
	public Appearance getRiseAppearance()
	{
		return getAppearance();
	}
	
	/** 
	    * Gets the appearance of candle/bar if it's close price is less than open price.
	    */
	public Appearance getFallAppearance()
	{
		return fFallAppearance;
	}
	
	/** 
	    * Adds point to the end of collection, using OHLC price model
	    * 
	    * @param o open price of the bar
	    * @param h high price of the bar
	    * @param l low price of the bar
	    * @param c close price of the bar
	    * 
	    * @return new instance of StockPoint
	    */
	public StockPoint addPoint(double o, double h, double l, double c)
	{
		StockPoint sp = new StockPoint(o,h,l,c);
		
		this.getPoints().add(sp);
		
		return sp;
	}
	
	/** 
	    * Gets current view type (i.e. candlestick or bar)
	    * 
	    * @return new StockPoint
	    */
	
	public ViewType getViewType()
	{
		return fViewType;
	}
	
	/** 
	    * Sets current view type (i.e. candlestick or bar)
	    * 
	    * 
	    * @param viewType	new view type
	    */
	
	public void setViewType(ViewType viewType)
	{
		fViewType = viewType;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject obj = super.toJSONObject();
		obj.put("fallAppearance", fFallAppearance.toJSONObject());
		
		return obj;
	}
	
	@Override
	public void fromJSONObject(JSONObject j) throws JSONException
	{
		super.fromJSONObject(j);
		
		fFallAppearance.fromJSONObject(j.getJSONObject("fallAppearance"));
	}
	
	protected void drawPoint(Canvas c, SeriesPaintInfo pinfo, float x1,float x2, StockPoint p) 
	{
		this.fPaint.reset();
		
		double openPrice = p.getOpen();
		double closePrice = p.getClose();
		
		boolean isFall = (closePrice < openPrice);
		
		float open = pinfo.getY(p.getOpen());
		float close = pinfo.getY(p.getClose());
		float high = pinfo.getY(p.getHigh());
		float low = pinfo.getY(p.getLow());
				
		float midX = (x1 + x2) / 2;
				
		Appearance a = isFall?fFallAppearance:getRiseAppearance();
		
		if(getViewType() == ViewType.CANDLESTICK)
		{
			fBodyRect.set(x1, open, x2, close);
			fBodyRect.sort();

			// high-low line
			a.applyOutline(fPaint);
			c.drawLine(midX, high, midX, low, fPaint);
			
			// body fill
			a.applyFill(fPaint,fBodyRect);			
			c.drawRect(fBodyRect, fPaint);
	
			// body outline			
			a.applyOutline(fPaint);
			c.drawRect(fBodyRect, fPaint);			
		}
		else if(getViewType() == ViewType.BAR)
		{
			a.applyOutline(fPaint);
			c.drawLine(midX, high, midX, low, fPaint);
			c.drawLine(x1, open, midX, open, fPaint);
			c.drawLine(midX, close, x2, close, fPaint);
		}
	}
	
}
