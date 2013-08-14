/**
 * Copyright 31.08.2012 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart.core;

import org.stockchart.utils.CustomObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;

/**
 * @author alexv
 *
 */
public abstract class ChartElement 
{
	private RectF fTempRectF;
	private Rect fTempRect;
	private Paint fPaint;
	
	private final ChartElement fParent;
	
	private final RectF fRectF = new RectF();
	
	public ChartElement(ChartElement parent)
	{
		fParent = parent;		
	}
	
	public void drawSimple(Canvas c)
	{
		draw(c,null);
	}
	
	public void draw(Canvas c,CustomObjects customObjects)
	{
		preDraw(c);
		
		innerDraw(c,customObjects);
		
		postDraw(c);
	}
	

	
	private void preDraw(Canvas c)
	{
		c.save();
		c.translate(fRectF.left, fRectF.top);
		
		this.TempRectF().set(0,0,fRectF.width(),fRectF.height());
		c.clipRect(this.TempRectF(), Op.REPLACE);		
	}
	
	private void postDraw(Canvas c)
	{
		c.restore();
	}
	
	protected abstract void innerDraw(Canvas c,CustomObjects customObjects);
	
	protected void onBoundsChanged() { }
	
	public ChartElement getParent()
	{
		return fParent;
	}
	public void setBounds(RectF r)
	{
		fRectF.set(r);
	}
	
	public float width()
	{
		return fRectF.width();
	}
	
	public float height()
	{
		return fRectF.height();
	}
	
	public void setBounds(float relX,float relY, float width,float height)
	{
		fRectF.left = relX;
		fRectF.top = relY;
		fRectF.right = fRectF.left + width;
		fRectF.bottom = fRectF.top + height;

		onBoundsChanged();
	}
	
	public RectF getBounds()
	{
		return fRectF;
	}
	
	public PointF getAbsolutePosition(float x,float y)
	{
		ChartElement parent = this;
		
		do
		{
			x+=parent.fRectF.left;
			y+=parent.fRectF.top;
			
			parent = parent.getParent();
		}
		while(parent != null);
		
		return new PointF(x,y);
	}
	public RectF getAbsoluteBounds()
	{
		PointF ap = (getParent() != null?getParent().getAbsolutePosition(fRectF.left,fRectF.top):new PointF(fRectF.left,fRectF.top));
		
		return new RectF(ap.x,ap.y,ap.x+fRectF.width(),ap.y+fRectF.height());		
	}
	
	public PointF getRelativePosition(float x,float y)
	{
		RectF ab = getAbsoluteBounds();
		
		return new PointF(x-ab.left,y-ab.top);
	}

	protected RectF TempRectF()
	{
		if(null == fTempRectF)
		{
			fTempRectF = new RectF();
		}
		
		return fTempRectF;
	}
	
	protected Rect TempRect()
	{
		if(null == fTempRect)
		{
			fTempRect = new Rect();
		}
		
		return fTempRect;
	}
	
	protected Paint Paint()
	{
		if(null == fPaint)
		{
			fPaint = new Paint();
		}
		
		return fPaint;
	}
}
