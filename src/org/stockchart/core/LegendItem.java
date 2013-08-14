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
package org.stockchart.core;

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Legend.MarkerStyle;
import org.stockchart.utils.CustomObjects;
import org.stockchart.utils.SizeF;

import android.graphics.Canvas;


public class LegendItem extends ChartElement
{
	private MarkerStyle fMarkerStyle = MarkerStyle.CIRCLE;
	
	private String fText = "";
	
	private Appearance fAppearance = new Appearance();
	private float fMarkerSize = 20f;
	
	private final float DISTANCE_BETWEEN_TEST_AND_MARKER = 1f;
	
	LegendItem(Legend parent)
	{
		super(parent);
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject obj = new JSONObject();
		obj.put("text", fText);
		obj.put("appearance", fAppearance.toJSONObject());
		obj.put("markerSize",fMarkerSize);
		
		return obj;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{
		fText = j.getString("text");
		fAppearance.fromJSONObject(j.getJSONObject("appearance"));
		fMarkerSize = (float)j.getDouble("markerSize");
	}
	
	public Appearance getAppearance()
	{
		return fAppearance;
	}
	
	public float getMarkerSize()
	{
		return fMarkerSize;
	}
	
	public void setMarkerSize(float f)
	{
		fMarkerSize = f;
	}
	
	public MarkerStyle getMarkerStyle()
	{
		return fMarkerStyle;
	}
	
	public void setMarkerStyle(MarkerStyle ms)
	{
		fMarkerStyle = ms;
	}
	
	public String getText()
	{
		return fText;
	}
	
	public void setText(String text)
	{
		fText = text;
	}
	
	public SizeF getSize()
	{
		SizeF sz = fAppearance.measureTextSize(fText, this.Paint(),true);
		

		sz.width+=fMarkerSize + DISTANCE_BETWEEN_TEST_AND_MARKER + getOutlineIssues(); 
		sz.height = Math.max(fMarkerSize + getOutlineIssues(), sz.height);
		return sz;
	}

	protected void innerDraw(Canvas c,CustomObjects customObjects) 
	{			
		c.getClipBounds(this.TempRect());
		
		float halfHeight = this.TempRect().height()/2f;
		
		drawMarker(c,halfHeight,halfHeight,fMarkerSize/2);
		
		fAppearance.applyText(this.Paint());
		
		SizeF sz = fAppearance.measureTextSize(fText, this.Paint(),false);
		
		c.drawText(fText, fMarkerSize + getOutlineIssues(), halfHeight + sz.height/2f, this.Paint());
	}
	
	private float getOutlineIssues()
	{
		return fAppearance.getOutlineWidth() * 2 + 1f + 1f; // from top and bottom 1px
	}
	
	private void drawMarker(Canvas c,float x,float y,float r)
	{			
		switch(fMarkerStyle)
		{
			case SQUARE: 
		
				this.TempRectF().set(x-r, y-r, x+r, y+r);			
				getAppearance().applyFill(this.Paint(), this.TempRectF());
				c.drawRect(this.TempRectF(), this.Paint());
				getAppearance().applyOutline(this.Paint());
				c.drawRect(this.TempRectF(), this.Paint());
			break;
			case CIRCLE:
				getAppearance().applyFill(this.Paint(), this.TempRectF());
				c.drawCircle(x, y, r, this.Paint());
				getAppearance().applyOutline(this.Paint());
				c.drawCircle(x, y, r, this.Paint());
				break;
		}		
	}
}