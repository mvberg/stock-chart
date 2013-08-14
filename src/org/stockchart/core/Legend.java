/**
 * Copyright 07.11.2012 Alex Vikulov (vikuloff@gmail.com)

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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Axis.Side;
import org.stockchart.utils.CustomObjects;
import org.stockchart.utils.PaintUtils;
import org.stockchart.utils.SizeF;
import org.stockchart.utils.Tuple;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;


public class Legend extends ChartElement 
{	
	private boolean fIsVisible = false;
	
	private ArrayList<LegendItem> fItems = new ArrayList<LegendItem>(); 
	
	private final Paint fPaint = new Paint();
	
	private Side fSide = Side.TOP;
	
	private final LegendItemPositionerIterator fIterator = new LegendItemPositionerIterator();
	
	private final RectF fTempRectF = new RectF();
	private final Rect fTempRect = new Rect();
	
	private final Appearance fLegendAppearance = new Appearance();
	
	public enum MarkerStyle
	{
		SQUARE,
		CIRCLE
	}
	
	public class LegendItem extends ChartElement
	{
		private MarkerStyle fMarkerStyle = MarkerStyle.CIRCLE;
		
		private String fText = "";
		
		private Appearance fAppearance = new Appearance();
		private float fMarkerSize = 20f;
		
		private final float DISTANCE_BETWEEN_TEST_AND_MARKER = 1f;
		
		private LegendItem()
		{
			super(Legend.this);
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
			SizeF sz = fAppearance.measureTextSize(fText, fPaint,true);
			

			sz.width+=fMarkerSize + DISTANCE_BETWEEN_TEST_AND_MARKER + getOutlineIssues(); 
			sz.height = Math.max(fMarkerSize + getOutlineIssues(), sz.height);
			return sz;
		}
	
		protected void innerDraw(Canvas c,CustomObjects customObjects) 
		{			
			c.getClipBounds(fTempRect);
			
			float halfHeight = fTempRect.height()/2f;
			
			drawMarker(c,halfHeight,halfHeight,fMarkerSize/2);
			
			fAppearance.applyText(fPaint);
			
			SizeF sz = fAppearance.measureTextSize(fText, fPaint,false);
			
			c.drawText(fText, fMarkerSize + getOutlineIssues(), halfHeight + sz.height/2f, fPaint);
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
			
					fTempRectF.set(x-r, y-r, x+r, y+r);			
					getAppearance().applyFill(fPaint, fTempRectF);
					c.drawRect(fTempRectF, fPaint);
					getAppearance().applyOutline(fPaint);
					c.drawRect(fTempRectF, fPaint);
				break;
				case CIRCLE:
					getAppearance().applyFill(fPaint, fTempRectF);
					c.drawCircle(x, y, r, fPaint);
					getAppearance().applyOutline(fPaint);
					c.drawCircle(x, y, r, fPaint);
					break;
			}		
		}
	}
	
	
	private class LegendItemPositionerIterator
	{
		private int fIndex = 0;

		private float fPos = 0f;
		
		private static final float DISTANCE = 5f;
		
		public void reset()
		{
			fIndex = 0;
			fPos = 0f;
		}
		
		public float getPosition()
		{
			return fPos;
		}
		
		public boolean hasNext()
		{
			return fIndex < Legend.this.fItems.size();
		}
		
		public Tuple<LegendItem,RectF> getNext()
		{
			if(!hasNext()) return null;
			
			LegendItem a = Legend.this.fItems.get(fIndex);
			
			SizeF size = a.getSize();
			
			RectF r = new RectF();
			
			if(!isVertical())
			{
				r.set(fPos, 0f, fPos + size.width, size.height);
				fPos+= size.width + DISTANCE;
			}
			else
			{
				r.set(0f,fPos,size.width,fPos + size.height);
				fPos+=size.height + DISTANCE;
			}
			
			fIndex++;
			if(!hasNext())
				fPos-=DISTANCE;
			
			return new Tuple<LegendItem,RectF>(a,r);
		}
		
	}
	
	public Legend(Area parent)	
	{
		super(parent);	
		
		fLegendAppearance.setPrimaryFillColor(Color.TRANSPARENT);
		fLegendAppearance.setOutlineWidth(-1f);
		
		Theme.fillAppearanceFromCurrentTheme(Legend.class, fLegendAppearance);
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject obj = new JSONObject();
		obj.put("side", fSide);		
		obj.put("visible",fIsVisible);
		
		JSONArray items = new JSONArray();
		for(LegendItem i:fItems)
		{
			items.put(i.toJSONObject());
		}
		
		obj.put("items", items);
		return obj;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{				
		fSide = Side.valueOf(j.getString("side"));
		fIsVisible = j.getBoolean("visible");
		
		this.fItems.clear();
		
		if(j.has("items"))
		{
			JSONArray items = j.getJSONArray("items");
			for(int i=0;i<items.length();i++)
			{
				JSONObject obj = items.getJSONObject(i);
				LegendItem item = new LegendItem();
				item.fromJSONObject(obj);
				
				fItems.add(item);

			}
		}
	}
	
	
	public ArrayList<LegendItem> getItems()
	{
		return fItems;
	}
	
	public Appearance getAppearance()
	{
		return fLegendAppearance;
	}
	
	public LegendItem addItem()
	{
		LegendItem i = new LegendItem();
		fItems.add(i);
		return i;
	}
	
	public boolean isVisible() 
	{
		return fIsVisible;
	}
	
	public boolean isVertical()
	{
		return fSide == Side.LEFT || fSide == Side.RIGHT; 
	}
	
	public void setVisible(boolean value)
	{
		fIsVisible = value;
	}
	
	public boolean checkSide(Side s)
	{
		return fSide == s;
	}
	
	public Side getSide()
	{
		return fSide;
	}
	
	public void setSide(Side s)
	{
		fSide = s;
	}
	
	public RectF getSize()
	{	
		RectF r = new RectF();
		
		fIterator.reset();
				
		float fMaxWidthOrHeight = 0f;
		
		while(fIterator.hasNext())
		{
			Tuple<LegendItem,RectF> item = fIterator.getNext();
			
			float v = isVertical()?item.x.getSize().width:item.x.getSize().height;
			
			if(v > fMaxWidthOrHeight)
				fMaxWidthOrHeight = v;
		}
		
		float right = isVertical()?fMaxWidthOrHeight:fIterator.getPosition();
		float bottom = isVertical()?fIterator.getPosition():fMaxWidthOrHeight;
		
		r.set(0f,0f,right,bottom);
		
		return r;

	}
	
	protected void innerDraw(Canvas c,CustomObjects customObjects) 
	{
		if(!isVisible()) return;
		
		c.getClipBounds(fTempRect);
		
		PaintUtils.drawFullRect(c, fPaint, fLegendAppearance, fTempRect);
		
		for(LegendItem i :fItems)
			i.draw(c, customObjects);

	}
	
	@Override
	protected void onBoundsChanged()
	{		
		fIterator.reset();
		
		while(fIterator.hasNext())
		{
			Tuple<LegendItem,RectF> item = fIterator.getNext();
			
			item.x.setBounds(item.y);
		}
	}

}
