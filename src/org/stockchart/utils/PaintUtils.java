/**
 * Copyright 09.11.2012 Alex Vikulov (vikuloff@gmail.com)

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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @author alexv
 *
 */
public class PaintUtils 
{
	private static final Path PATH = new Path();
	
	private static final RectF fTempRectF = new RectF();
	
	/**
	 * Workaround for possible bug https://code.google.com/p/android/issues/detail?id=29944
	 */
	public static void drawLine(Canvas c, float x1, float y1,float x2,float y2, Paint p)
	{
		PATH.reset();
		PATH.moveTo(x1, y1);
		PATH.lineTo(x2, y2);
		
		c.drawPath(PATH, p);
	}
//	
//	public static void drawTextOnPath(Canvas c, String text, float x1, float y1,float x2,float y2, Paint p)
//	{
//		PATH.reset();
//		PATH.moveTo(x1, y1);
//		PATH.lineTo(x2, y2);
//		
//		c.drawTextOnPath(text, PATH, 0f, 0f, p);
//	}
	
	public static void drawBorder(Canvas c,Paint p,Appearance a,RectF rect)
	{		
		float o = a.getOutlineWidth();
		if(o < 0f) return;
		
		a.applyOutline(p);
		
		c.drawRect(o,o,rect.width()-o,rect.height()-o, p);
	}
	
	public static void drawCircle(float x, float y, float r,Canvas c,Paint p,Appearance a)
	{
		fTempRectF.set(x - r,y - r, x + r,y + r);
		
		a.applyFill(p, fTempRectF);
		c.drawCircle(x, y, r, p);
		a.applyOutline(p);
		c.drawCircle(x, y, r, p);
	}
	
	public static void drawSquare(float x, float y, float r,Canvas c,Paint p,Appearance a)
	{
		fTempRectF.set(x - r,y - r, x + r,y + r);
		
		a.applyFill(p, fTempRectF);
		c.drawRect(fTempRectF, p);
		a.applyOutline(p);
		c.drawRect(fTempRectF, p);
	}
	
	public static void drawBorder(Canvas c,Paint p,Appearance a,Rect rect)
	{
		 drawBorder(c, p, a,new RectF(rect));
	}
	
	public static void drawFullRect(Canvas c,Paint p,Appearance a,Rect rect)
	{
		drawFullRect(c, p, a, new RectF(rect));
	}
	
	public static void drawFullRect(Canvas c,Paint p,Appearance a,RectF rect)
	{
		float w = rect.width();
		float h = rect.height();
		float o = a.getOutlineWidth();
		a.applyFill(p, rect);
		
		c.drawRect(o,o,w,h, p);
		
		drawBorder(c,p,a,rect);
	}
}
