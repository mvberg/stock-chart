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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.utils.PaintUtils;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

public class FibonacciSticker extends AbstractSticker
{
	public enum Type
	{
		RETRACEMENT,
		FANS,
		ARCS
	}
	
	public static final TreeMap<Float,String> RETRACEMENTS = new TreeMap<Float,String>();
	
	public static final String R0 = "0.0%";
	public static final String R100 = "100.0%";
	public static final String R382 = "38.2%";
	public static final String R500 = "50.0%";
	public static final String R618 = "61.8%";
	
	private final RectF fTempRectF = new RectF();
	
	private Type fType = Type.RETRACEMENT;
	
	static
	{
		RETRACEMENTS.put(0.0f, "0.0%");
		RETRACEMENTS.put(0.236f, "23.6%");
		RETRACEMENTS.put(0.382f, "23.6%");
		RETRACEMENTS.put(0.5f, "50.0%");
		RETRACEMENTS.put(0.618f, "61.8%");
		RETRACEMENTS.put(1.0f, "100.0%");
		RETRACEMENTS.put(1.618f, "161.8%");
		RETRACEMENTS.put(2.618f, "261.8%");
		RETRACEMENTS.put(4.236f, "4.236%");
	}
	
	public Type getType()
	{
		return fType;
	}
	
	public void setType(Type t)
	{
		fType = t;
	}

	@Override
	public void fromJSONObject(JSONObject j) throws JSONException 
	{
		super.fromJSONObject(j);
		
		fType = Type.valueOf(j.getString("type"));
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException 
	{
		JSONObject r = super.toJSONObject();
		
		r.put("type", fType);
		
		return r;
		
	}
	
	@Override
	protected void draw(SeriesPaintInfo info, PointF p1, PointF p2, Canvas c)
	{
		PointF rightMost = Utils.getRightmost(p1, p2);
		PointF leftMost = Utils.getLeftmost(p1, p2);
		
		boolean isRising = Utils.isRising(p1, p2);
		
		float maxY = Math.max(p1.y, p2.y);
		float minY = Math.min(p1.y, p2.y);
		
		float height = maxY - minY;			
		float originY = isRising?minY:maxY;
		
		float m = fAppearance.getOutlineWidth();
		
		// guideline
//		fAppearance.applyOutline(fPaint);
//		PaintUtils.drawLine(c, p1.x, p1.y, p2.x, p2.y, fPaint);
		
		switch(fType)
		{
		case FANS:
			{
				float r382 = originY + height*0.382f * (isRising?1f:-1f);
				float r500 = originY + height*0.5f * (isRising?1f:-1f);
				float r618 = originY + height*0.618f * (isRising?1f:-1f);
				fAppearance.applyOutline(fPaint);
				
				float rightMostX = Math.max(rightMost.x, c.getWidth() + 1f);
				
				PaintUtils.drawLine(c, leftMost.x,leftMost.y,rightMostX,Utils.getY(leftMost.x,leftMost.y,rightMost.x,r382,rightMostX),fPaint);
				PaintUtils.drawLine(c, leftMost.x,leftMost.y,rightMostX,Utils.getY(leftMost.x,leftMost.y,rightMost.x,r500,rightMostX),fPaint);
				PaintUtils.drawLine(c, leftMost.x,leftMost.y,rightMostX,Utils.getY(leftMost.x,leftMost.y,rightMost.x,r618,rightMostX),fPaint);
			}
			break;
		case RETRACEMENT:
			{	
				Set<Entry<Float, String>> entrySet = RETRACEMENTS.entrySet();
				Iterator<Entry<Float, String>> iterator = entrySet.iterator();

				while(iterator.hasNext())
				{
					Entry<Float,String> i = iterator.next();
					
					float y = originY + height* i.getKey() * (isRising?1f:-1f);
					
					fAppearance.applyOutline(fPaint);
					PaintUtils.drawLine(c, 0, y, c.getWidth(), y, fPaint);
					
					fAppearance.applyText(fPaint);
					c.drawText(i.getValue(),1f,y - m,fPaint);
				}
				
			}
			break;
		case ARCS:
			{
				float length = (float) Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y)); // euclidean distance
				float r382 = length*0.382f;
				float r500 = length*0.5f;
				float r618 = length*0.618f;
				
				fAppearance.applyOutline(fPaint);
				
				int a2 = isRising?180:-180;
				
				fTempRectF.set(rightMost.x - r500,rightMost.y-r500,rightMost.x + r500,rightMost.y+r500);
				c.drawArc(fTempRectF, 0, a2, false, fPaint);
				
				fTempRectF.set(rightMost.x - r382,rightMost.y-r382,rightMost.x + r382,rightMost.y+r382);
				c.drawArc(fTempRectF, 0, a2, false, fPaint);
				
				fTempRectF.set(rightMost.x - r618,rightMost.y-r618,rightMost.x + r618,rightMost.y+r618);
				c.drawArc(fTempRectF, 0, a2, false, fPaint);
			}
			break;
		}
	}

}
