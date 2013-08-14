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

package org.stockchart.utils;

import org.stockchart.core.Appearance;
import org.stockchart.core.PaintInfo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

public class GridPainter 
{	
	public enum GridType
	{
		HORIZONTAL,
		VERTICAL
	}
	
	public enum GridLabelPosition
	{
		INDEFINITE,
		NEAR,
		FAR,
	}
	
	public interface IGridLabelsProvider
	{
		public String getLabel(double value); 
	}

	public static void drawGridLineAt(double value, Rect clipRect,Canvas c, Appearance app, Paint paint, PaintInfo pinfo, GridType gt)
	{
		drawGridLineAt(value,clipRect,c,app, paint,pinfo,gt,GridLabelPosition.INDEFINITE,null,0f);	
	}
	
	public static void drawGridLineAt(double value, Rect clipRect,Canvas c, Appearance app, Paint paint, PaintInfo pinfo, GridType gt, GridLabelPosition glp,IGridLabelsProvider provider,float labelLength)
	{	
		final Rect bounds = new Rect();
		
		boolean isNear = (glp == GridLabelPosition.NEAR);
				
		float v = pinfo.get(value);
		
		//paint.setTextAlign(Align.CENTER);
		
		if(gt == GridType.HORIZONTAL)
		{
			if(null != provider)
			{
				float x = (isNear?clipRect.left:clipRect.right);
				float dx = (isNear?labelLength:-labelLength);
						
				app.applyOutline(paint);
				PaintUtils.drawLine(c, x, v, x+dx, v, paint);
				
				String label = provider.getLabel(value);
								
				if(null != label)
				{
					app.applyText(paint);
					
					paint.getTextBounds(label, 0, label.length(), bounds);
		
					paint.setTextAlign(Align.CENTER);
					
					float y = v;
					
					if(v <= clipRect.top)
						y = clipRect.top + bounds.height() + 1f;
					else if(v + bounds.height() >= clipRect.bottom)
						y = clipRect.bottom - 1f;
					else				
						y = v+(float)bounds.height()/2f;
					
					float _x = x + dx + (bounds.width()/2f + 1f)*(isNear?1f:-1f);
					
					c.drawText(label, _x, y, paint);
				}
			}
			else
			{
				app.applyOutline(paint);
				PaintUtils.drawLine(c, clipRect.left, v, clipRect.right, v, paint);
			}
		}
		else if(gt == GridType.VERTICAL)
		{
			
			if(null != provider)
			{
				String label = provider.getLabel(value);
				
				float y = (isNear?clipRect.top:clipRect.bottom);
				float dy = (isNear?labelLength:-labelLength);
				
				app.applyOutline(paint);
				PaintUtils.drawLine(c, v, y, v, y+dy, paint);
				
				if(null != label)
				{				
					app.applyText(paint);
					
					paint.setTextAlign(Align.CENTER);
					
					paint.getTextBounds(label, 0, label.length(), bounds);
					float textHeight = bounds.height();		
					
					float _x = v;
					final float halfWidth = bounds.width()/2f;
					
					if(_x - halfWidth <= clipRect.left)
						_x = clipRect.left + halfWidth;
					else if(_x + halfWidth >= clipRect.right)
						_x = clipRect.right - halfWidth;
					
					float _y = y + dy + (isNear?textHeight + 1f:-1f); 
					c.drawText(label, _x, _y, paint);
				}
			}
			else
			{
				app.applyOutline(paint);
				PaintUtils.drawLine(c, v, clipRect.top, v, clipRect.bottom, paint);	
			}
		}
	}

	public static void drawGrid(Double[] values, Rect clipRect,Canvas c,  Appearance app, Paint paint, PaintInfo pinfo, GridType gt, GridLabelPosition glp,IGridLabelsProvider provider,float labelLength)
	{
		for(double v:values)
			drawGridLineAt(v,clipRect,c,app,paint,pinfo,gt,glp,provider,labelLength);
	
	}
	
	public static void drawGrid(Double[] values, Rect clipRect,Canvas c, Appearance app,  Paint paint, PaintInfo pinfo,GridType gt)
	{
		drawGrid(values,clipRect,c,app,paint,pinfo,gt,GridLabelPosition.INDEFINITE,null,0f);
	}

}
