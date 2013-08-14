/**
 * Copyright 22.03.2013 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart.stickers;

import org.stockchart.core.Area;
import org.stockchart.core.Axis.Side;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.utils.PaintUtils;

import android.graphics.Canvas;
import android.graphics.PointF;

public class SimpleGuideSticker extends AbstractSticker
{
	public static SimpleGuideSticker createParallel(Area area,SimpleGuideSticker parent,float offset,boolean addToArea)
	{
		Side h = parent.getHorizontalAxis();
		Side v = parent.getVerticalAxis();
		
		float x1 = area.getCoordinate(h, parent.getX1());
		float x2 = area.getCoordinate(h, parent.getX2());
		float y1 = area.getCoordinate(v, parent.getY1());
		float y2 = area.getCoordinate(v, parent.getY2());
		
		PointF[] p = getParallel(x1,y1,x2,y2,offset);
		
		SimpleGuideSticker result = new SimpleGuideSticker();
		result.setFirstPoint(area.getValueByCoordinate(h, p[0].x,false),
							 area.getValueByCoordinate(v, p[0].y, false));
		
		result.setSecondPoint(area.getValueByCoordinate(h, p[1].x,false),
				 			  area.getValueByCoordinate(v, p[1].y, false));

		if(addToArea)
			area.getStickers().add(result);
		
		return result;
	}
	
	public SimpleGuideSticker createParallel(Area area,float offset,boolean addToArea)
	{
		return createParallel(area,this,offset,addToArea);
	}
	
	@Override
	protected void draw(SeriesPaintInfo info,PointF p1, PointF p2, Canvas c)
	{
		this.fAppearance.applyOutline(fPaint);
		PaintUtils.drawLine(c, p1.x,p1.y,p2.x,p2.y,fPaint);
	}
	
	private static PointF[] getParallel(float x1,float y1,float x2, float y2, float offset)
	{
		float l = (float)Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		
		PointF r1 = new PointF();
		PointF r2 = new PointF();
			    
		r1.x = x1 + offset * (y2-y1) / l;
		r2.x = x2 + offset * (y2-y1) / l;
		r1.y = y1 + offset * (x1-x2) / l;
		r2.y = y2 + offset * (x1-x2) / l;
		
		return new PointF[] { r1, r2 };
	}
}
