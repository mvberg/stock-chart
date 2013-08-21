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

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Appearance;
import org.stockchart.core.Appearance.Gradient;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.core.Theme;
import org.stockchart.points.LinePoint;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

public class LinearSeries extends AbstractSeries<LinePoint>
{
	float minY = Float.MAX_VALUE;
	float fDistanceBetweenPoints = 0f;
	
	private final Paint fPaint = new Paint();
	
	private float fPointSizeInPercents = 0.5f;
	
	private boolean fPointsVisible = false;
	
	private PointStyle fPointStyle = PointStyle.CIRCLE;
	
	private final RectF fTempRectF = new RectF();
	
	private final Path fLinePath = new Path();
	private final Path fFillPath = new Path();
	
	private ArrayList<PointF> fPoints = new ArrayList<PointF>();
	
	private final Appearance fPointAppearance = new Appearance();
	
	public enum PointStyle
	{
		SQUARE,
		CIRCLE
	}

	public LinearSeries()
	{		
		getAppearance().setOutlineColor(Color.RED);
		getAppearance().setOutlineWidth(2f);
		getAppearance().setPrimaryFillColor(0x80FF0000);
		getAppearance().setSecondaryFillColor(0x8000FF00);
		getAppearance().setGradient(Gradient.LINEAR_VERTICAL);
		
		this.getPointAppearance().setOutlineColor(Color.RED);
		
		Theme.fillAppearanceFromCurrentTheme(LinearSeries.class, getAppearance());
		Theme.fillAppearanceFromCurrentTheme(LinearSeries.class, "pointAppearance", getPointAppearance());
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject j = super.toJSONObject();
		j.put("pointSize", fPointSizeInPercents);
		j.put("pointStyle", fPointStyle);
		j.put("pointsVisible", fPointsVisible);
		j.put("pointAppearance", fPointAppearance.toJSONObject());
		
		return j;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{
		super.fromJSONObject(j);
		fPointSizeInPercents = (float)j.getDouble("pointSize");
		fPointStyle = PointStyle.valueOf(j.getString("pointStyle"));
		fPointsVisible = j.getBoolean("pointsVisible");
		fPointAppearance.fromJSONObject(j.getJSONObject("pointAppearance"));
	}
	
	public Appearance getPointAppearance()
	{
		return this.fPointAppearance;
	}
	/** 
	    * Sets new point style. 
	    * 
	    * @param ps new point style value
	    */
	
	public void setPointsStyle(PointStyle ps)
	{
		fPointStyle = ps;
	}
	
	/** 
	    * Gets point style. 
	    * 
	    * @return current point style
	    */
	
	public PointStyle getPointsStyle()
	{
		return fPointStyle;
	}
	
	/** 
	    * Gets whether points visible or not 
	    * 
	    * @return <code>true</code> if points visible 
	    * 		  <code>false</code> otherwise
	    */	
	public boolean isPointsVisible()
	{
		return fPointsVisible;
	}
	
	/** 
	    * Sets whether points visible or not 
	    * 
	    * @param v new point visible value
	    */	
	
	public void setPointsVisible(boolean v)
	{
		fPointsVisible = v;
	}
	
	/** 
	    * Sets the new size of points in percents. The default value is 0.5. It means 
	    * that the shape of the point will be painted twice smaller than bar or candlestick for instance. 
	    * 
	    * @param ps new point size
	    */	
	public void setPointSizeInPercents(float ps)
	{
		fPointSizeInPercents = ps;
	}
	
	public float getPointSizeInPercents()
	{
		return fPointSizeInPercents;
	}
	
	public LinePoint addPoint(double value)
	{
		LinePoint p = new LinePoint(value);
		this.getPoints().add(p);
		
		return p;
	}
	
	@Override
	protected void drawPoint(Canvas c, SeriesPaintInfo pinfo, float x1, float x2, LinePoint p) 
	{	
		fDistanceBetweenPoints = x2 - x1;
		
		float x = (x1 + x2)/2f;
		float y = pinfo.getY(p.getValue());
		

		if(fPoints.isEmpty())
			fLinePath.moveTo(x, y);
		else
			fLinePath.lineTo(x, y);
		
		if(fPoints.isEmpty())
			fFillPath.moveTo(x, y);
		else
			fFillPath.lineTo(x, y);
		
		this.fPoints.add(new PointF(x,y));
		
		if(y < minY)
			minY = y;
	}
	
	private void drawPoint(Canvas c,float x,float y,float r)
	{			
		this.fTempRectF.set(x-r, y-r, x+r, y+r);
		
		switch(fPointStyle)
		{
			case SQUARE: 

				getPointAppearance().applyFill(fPaint, fTempRectF);
				c.drawRect(fTempRectF, fPaint);
				getPointAppearance().applyOutline(fPaint);
				c.drawRect(fTempRectF, fPaint);
			break;
			case CIRCLE:
				getPointAppearance().applyFill(fPaint, fTempRectF);
				c.drawCircle(x, y, r, fPaint);
				getPointAppearance().applyOutline(fPaint);
				c.drawCircle(x, y, r, fPaint);
				break;
		}		
	}
	
	@Override
	protected void preDraw() 
	{
		fDistanceBetweenPoints = 0f;
		minY = Float.MAX_VALUE;
		
		fLinePath.reset();
		fFillPath.reset();
		fPoints.clear();
	}

	@Override
	protected void postDraw(Canvas c) 
	{
		if(fPoints.size() == 0) return;
		
		int h = c.getClipBounds().bottom;
		
		PointF firstPoint = fPoints.get(0);
		PointF lastPoint = fPoints.get(fPoints.size() - 1);
		
		fFillPath.lineTo(lastPoint.x, h);
		fFillPath.lineTo(firstPoint.x, h);
		fFillPath.close();
		
		
		this.fTempRectF.set(firstPoint.x, minY, lastPoint.x, h);
		this.getAppearance().applyFill(fPaint,fTempRectF);
		c.drawPath(fFillPath, fPaint);
		
		this.getAppearance().applyOutline(fPaint);
		c.drawPath(fLinePath,fPaint);
		
		if(fPointsVisible)
		{
			for(PointF p:fPoints)
			{
				float r = fDistanceBetweenPoints*fPointSizeInPercents*0.5f;
				drawPoint(c, p.x, p.y, r);	
			}
			//drawPoint(c, prevX,prevY,r);
		}
	}
}
