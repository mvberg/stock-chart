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

import org.stockchart.core.Appearance.Gradient;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.core.Theme;
import org.stockchart.points.BarPoint;
import org.stockchart.utils.ColorUtils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class BarSeries extends AbstractSeries<BarPoint>
{
	private final Paint fPaint = new Paint();
	
	private final RectF fBodyRect = new RectF();
	
	public BarSeries()
	{	
		getAppearance().setGradient(Gradient.LINEAR_HORIZONTAL);	
		getAppearance().setPrimaryFillColor(ColorUtils.lighten(Color.BLUE,0.5f));
		getAppearance().setSecondaryFillColor(Color.BLUE);		
		getAppearance().setOutlineColor(Color.BLUE);
		
		Theme.fillAppearanceFromCurrentTheme(BarSeries.class, getAppearance());
	}
	
	/** 
	    * Adds point to the end of collection 
	    * 
	    * @param v1 first value of the bar
	    * @param v2 second value of the bar
	    * 
	    * @return new instance of BarPoint
	    */
	
	public BarPoint addPoint(double v1,double v2)
	{
		BarPoint bp = new BarPoint(v1,v2);
		this.getPoints().add(bp);
		
		return bp;
	}
	
	
	@Override
	protected void drawPoint(Canvas c, SeriesPaintInfo pinfo, float x1, float x2,BarPoint p) 
	{	
		float y1 = pinfo.getY(p.getValueAt(0));
		float y2 = pinfo.getY(p.getValueAt(1));
				
		if(x2 - x1 > 1f)
		{
			fBodyRect.set(x1, y1, x2, y2);		
			fBodyRect.sort();
			
			getAppearance().applyFill(fPaint, fBodyRect);
			c.drawRect(fBodyRect, fPaint);
			
			getAppearance().applyOutline(fPaint);
			c.drawRect(fBodyRect, fPaint);
		}
		else
		{
			float x = (x2+x1)/2;
			getAppearance().applyOutline(fPaint);
			c.drawLine(x, y1, x, y2, fPaint);
		}
	}
}
