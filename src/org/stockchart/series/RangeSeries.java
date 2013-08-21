/**
 * Copyright 14.08.2013 Alex Vikulov (vikuloff@gmail.com)

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

import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.core.Theme;
import org.stockchart.core.Appearance.Gradient;
import org.stockchart.points.BarPoint;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;


public class RangeSeries extends AbstractSeries<BarPoint>
{
	float fMinY = Float.MAX_VALUE;
	float fMaxY = Float.MIN_VALUE;
	
	float fMinX = Float.MAX_VALUE;
	float fMaxX = Float.MIN_VALUE;
	
	private final RectF fTempRectF = new RectF();
	
	private final Path fPath1 = new Path();
	private final Path fPath2 = new Path();
	private final Path fResultPath = new Path();
	
	private final Paint fPaint = new Paint();
	
	private ArrayList<PointF> fPoints = new ArrayList<PointF>();
	
	public RangeSeries()
	{
		getAppearance().setOutlineColor(Color.RED);
		getAppearance().setOutlineWidth(2f);
		getAppearance().setPrimaryFillColor(0x80FF0000);
		getAppearance().setSecondaryFillColor(0x8000FF00);
		getAppearance().setGradient(Gradient.LINEAR_VERTICAL);
		
		Theme.fillAppearanceFromCurrentTheme(RangeSeries.class, getAppearance());
	}
	
	public BarPoint addPoint(double v1,double v2)
	{
		BarPoint bp = new BarPoint(v1,v2);
		this.getPoints().add(bp);
		
		return bp;
	}
	@Override
	protected void preDraw() 
	{
		fMinY = Float.MAX_VALUE;
		fMaxY = Float.MIN_VALUE;
		fMinX = Float.MAX_VALUE;
		fMaxX = Float.MIN_VALUE;
		
		fPoints.clear();
		fResultPath.reset();
		fPath1.reset();
		fPath2.reset();
	}
	
	@Override
	protected void drawPoint(Canvas c, SeriesPaintInfo pinfo, float x1,	float x2, BarPoint p) 
	{
		float x = (x1 + x2)/2f;
		float y0 = pinfo.getY(p.getValueAt(0));
		float y1 = pinfo.getY(p.getValueAt(1));
		
		float minY = Math.min(y0, y1);
		float maxY = Math.max(y0, y1);
		
		if(fPoints.isEmpty())
			fPath1.moveTo(x, y0);
		else
			fPath1.lineTo(x, y0);
		
	
		if(fPoints.isEmpty())
			fPath2.moveTo(x, y1);
		else
			fPath2.lineTo(x, y1);

		fPoints.add(new PointF(x,y1));
		
		if(minY < fMinY)
			fMinY = minY;
		
		if(maxY > fMaxY)
			fMaxY = maxY;
		
		if(x < fMinX)
			fMinX = x; 
 
		if(x > fMaxX)
			fMaxX = x;
	}

	@Override
	protected void postDraw(Canvas c) 
	{
		if(fPoints.isEmpty()) return;
		
		fResultPath.addPath(fPath1);
		
		for(int i = fPoints.size() - 1;i>=0;i--)
		{
			PointF p = fPoints.get(i);
			fResultPath.lineTo(p.x,p.y);
		}
		
		fResultPath.close();
				
		this.fTempRectF.set(fMinX, fMinY, fMaxX, fMaxY);
		this.getAppearance().applyFill(fPaint,fTempRectF);
		c.drawPath(fResultPath, fPaint);
		
		this.getAppearance().applyOutline(fPaint);
		c.drawPath(fPath1,fPaint);
		c.drawPath(fPath2,fPaint);
	}
}
