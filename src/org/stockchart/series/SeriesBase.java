/**
 * Copyright 01.08.2012 Alex Vikulov (vikuloff@gmail.com)

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

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Axis;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.points.AbstractPoint;

import android.graphics.Canvas;

/**
 * @author alexv
 *
 */
public abstract class SeriesBase 
{
	private Axis.Side fXAxisSide = Axis.Side.BOTTOM;
	private Axis.Side fYAxisSide = Axis.Side.RIGHT;
	
	private int fIndexOffset = 0;
	
	private boolean fIsVisible = true;
	
	private double fLastValue = Double.NaN;
	
	private String fName; 
	
	private static int DEFAULT_NAME_ID = 0;
	
	public SeriesBase()
	{		
		fName = this.getClass().getSimpleName()+String.valueOf(++DEFAULT_NAME_ID);		
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject j = new JSONObject();
		j.put("name",fName);
		j.put("indexOffset", fIndexOffset);
		j.put("xAxisSide",fXAxisSide);
		j.put("yAxisSide", fYAxisSide);
		j.put("visible", fIsVisible);
		
		if(!Double.isNaN(fLastValue))
			j.put("lastValue", fLastValue);
		
		return j;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{
		fName = j.getString("name");
		fIndexOffset = j.getInt("indexOffset");
		fXAxisSide = Axis.Side.valueOf(j.getString("xAxisSide"));
		fYAxisSide = Axis.Side.valueOf(j.getString("yAxisSide"));
		fIsVisible = j.getBoolean("visible");
		
		fLastValue = j.has("lastValue")?j.getDouble("lastValue"):Double.NaN;
	}
	
	public String getName()
	{
		return fName;
	}
	
	public void setName(String value)
	{
		fName = value;
	}
	
	public boolean isVisible()
	{
		return fIsVisible;
	}
	
	public void setVisible(boolean value)
	{
		fIsVisible = value;
	}
	
		
	public void setXAxisSide(Axis.Side side)
	{
		fXAxisSide = side;
	}
	
	public void setYAxisSide(Axis.Side side)
	{
		fYAxisSide = side;
	}
		
	public Axis.Side getXAxisSide()
	{		
		return fXAxisSide;
	}
	
	public Axis.Side getYAxisSide()
	{
		return fYAxisSide;
	}
	
	public abstract void draw(Canvas c,SeriesPaintInfo pinfo);
	public abstract int getPointCount();
	public abstract AbstractPoint getPointAt(int i);
	public double[] getMaxMinPrice(double viewMax,double viewMin)
	{
		if(!isVisibleOnScreen(viewMax,viewMin)) return null;

		double max = Double.NaN;
		double min = Double.NaN;
		
		int arrayIndex = convertToArrayIndexZeroBased(viewMin);
							
		for(int i=arrayIndex;i<getPointCount();i++)
		{
			AbstractPoint p = getPointAt(i);
			
			double[] maxMin = p.getMaxMin();
						
			max = (0 == Double.compare(max, Double.NaN))?maxMin[0]:Math.max(maxMin[0], max);
			min = (0 == Double.compare(min, Double.NaN))?maxMin[1]:Math.min(maxMin[1], min);
			
			if(convertToScaleIndex(i) > viewMax)
				break;
		}
		
		return new double[] { max, min };	
	}
	
	public double[] getMaxMinPrice2(int startIndex,int endIndex)
	{
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		
		for(int i=startIndex;i<=endIndex;i++)
		{
			AbstractPoint p = getPointAt(i);
			
			double[] maxMin = p.getMaxMin();
			if(maxMin[0] > max)
				max = maxMin[0];
			
			if(maxMin[1] < min)
				min = maxMin[1];
		}
		
		return new double[] { max, min };
	}
	
	public AbstractPoint getFirstPoint()
	{
		return getPointAt(0);
	}
	
	public AbstractPoint getLastPoint()
	{
		return getPointAt(getPointCount() - 1);
	}
	
	public void setIndexOffset(int offset)
	{
		fIndexOffset = offset;
	}
	
	public int getIndexOffset()
	{
		return fIndexOffset;
	}
	
	public int convertToArrayIndexZeroBased(double value)
	{
		int index = convertToArrayIndex(value);
		
		if(index < 0)
			index = 0;
	
		return index;
	}
	
	public int convertToArrayIndex(double value)
	{
		return (int)Math.round(value) - fIndexOffset;
	}
	
	public float convertToScaleIndex(int indexInArray)
	{
		return (float)(fIndexOffset + indexInArray);
	}
	
	public boolean hasPoints()
	{
		return getPointCount() > 0;
	}
	
	public float getLastScaleIndex()
	{
		return convertToScaleIndex(getPointCount() - 1);
	}
	
	public float getFirstScaleIndex()
	{
		return convertToScaleIndex(0);
	}
	
	public void setLastValue(double value)
	{
		fLastValue = value;		
	}

	public double getLastValue()
	{
		return fLastValue;
	}
	
	
	public boolean isVisibleOnScreen(double viewMax, double viewMin)
	{
		if(!isVisible() || !hasPoints()) return false;
		
		if(getFirstScaleIndex() > viewMax || getLastScaleIndex() < viewMin) return false;
		
		return true;	
	}	
	
}
