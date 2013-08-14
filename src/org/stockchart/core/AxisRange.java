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

package org.stockchart.core;

import org.json.JSONException;
import org.json.JSONObject;

public class AxisRange 
{
	public enum ViewValues
	{
		FIT_SCREEN,
		SCROLL_TO_LAST,
		SCROLL_TO_FIRST,		
	}
	
	private boolean fAuto = true;
	
	private double fMinValue =Double.NaN;
	private double fMaxValue = Double.NaN;
	
	private double fMinViewValue = Double.NaN;
	private double fMaxViewValue = Double.NaN;
	
	private double fMinAutoValue = Double.NaN;
	private double fMaxAutoValue = Double.NaN;
		
	private boolean fIsZoomable = false;
	private boolean fIsMovable = false;
	
	private float fMargin = 0.0f;
	
	private double fMinViewLength = Double.NaN;
	private double fMaxViewLength = Double.NaN;
	
	/**
	 *  Sets maximal and minimal view length
	 *  
	 * @param maxViewLength maximal view length, or <code>Double.Nan</code> if there is no limit
	 * @param minViewLength minimal view length, or <code>Double.Nan</code> if there is no limit
	 */
	public void setMaxMinViewLength(double maxViewLength,double minViewLength)
	{
		fMinViewLength = minViewLength;
		fMaxViewLength = maxViewLength;
	}
	
	public double getMaxViewLength()
	{
		return fMaxViewLength;
	}
	
	public double getMinViewLength()
	{
		return fMinViewLength;
	}
	
	public void setMargin(float f)
	{
		fMargin = f;
	}
	
	public float getMargin()
	{
		return fMargin;
	}
	
	public double getMargin(double max,double min)
	{
		return (max - min)*fMargin;
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject r = new JSONObject();
		r.put("auto", fAuto);
		
		if(!Double.isNaN(fMinViewLength))
			r.put("minViewLength", fMinViewLength);

		if(!Double.isNaN(fMaxViewLength))
			r.put("maxViewLength", fMaxViewLength);

		
		if(!Double.isNaN(fMinValue))
			r.put("minValue", fMinValue);
		
		if(!Double.isNaN(fMaxValue))
			r.put("maxValue",fMaxValue);
		
		if(!Double.isNaN(fMinViewValue))
			r.put("minViewValue", fMinViewValue);
		
		if(!Double.isNaN(fMaxViewValue))
			r.put("maxViewValue", fMaxViewValue);
		
		r.put("zoomable", fIsZoomable);
		r.put("movable",fIsMovable);
		
		r.put("margin", fMargin);
		
		return r;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{
		fAuto = j.getBoolean("auto");
		
		fMinValue = j.has("minValue")?j.getDouble("minValue"):Double.NaN;
		fMaxValue = j.has("maxValue")?j.getDouble("maxValue"):Double.NaN;		
		fMinViewValue = j.has("minViewValue")?j.getDouble("minViewValue"):Double.NaN;
		fMaxViewValue = j.has("maxViewValue")?j.getDouble("maxViewValue"):Double.NaN;
		
		fMinViewLength = j.has("minViewLength")?j.getDouble("minViewLength"):Double.NaN;
		fMaxViewLength = j.has("maxViewLength")?j.getDouble("maxViewLength"):Double.NaN;
		
		
		fIsZoomable = j.getBoolean("zoomable");
		fIsMovable = j.getBoolean("movable");
		fMargin = (float)j.getDouble("margin");
	}
	
	public boolean isZoomable()
	{
		return fIsZoomable;
	}
	
	public void setZoomable(boolean value)
	{
		fIsZoomable = value;
	}
	
	public boolean isMovable()
	{
		return fIsMovable;
	}
	
	public void setMovable(boolean value)
	{
		fIsMovable = value;
	}
	
	public void resetViewValues()
	{
		fMinViewValue = Double.NaN;
		fMaxViewValue = Double.NaN;
	}
	
	public void resetAutoValues()
	{
		fMinAutoValue = Double.NaN;
		fMaxAutoValue = Double.NaN;
	}
	
	public double getMinViewValueOrAutoValue()
	{
		if(Double.isNaN(fMinViewValue))
			return this.getMinOrAutoValue();
		
		return fMinViewValue;
	}

	public double getMaxViewValueOrAutoValue()
	{
		if(Double.isNaN(fMaxViewValue))
			return this.getMaxOrAutoValue();
		
		return fMaxViewValue;
	}


	public boolean isAuto()
	{
		return fAuto;
	}
	
	public void setAuto(boolean value)
	{
		fAuto = value;
	}
	
	public double getViewLength()
	{
		return this.getMaxViewValueOrAutoValue() - this.getMinViewValueOrAutoValue();
	}
	
	public double getLength()
	{
		return this.getMaxOrAutoValue() - this.getMinOrAutoValue();
	}
	
	public double getMaxOrAutoValue()
	{
		return fAuto?fMaxAutoValue:fMaxValue;
	}
	
	public double getMinOrAutoValue()
	{
		return fAuto?fMinAutoValue:fMinValue;
	}
	
	/** 
	    * Changes current view values by given amount
	    *
	    *<p>
	    *This method takes view length, multiplies it by factor.
	    *The calculated amount is add respectively to current view values
	    *
	    * @param factor  zoom factor
	    */
	
	public void moveViewValues(float factor)
	{
		if(!this.isMovable()) return;
		
		double viewLength = getViewLength();
		
		double d = (viewLength*factor);
		
		double fNewMinValue = getMinViewValueOrAutoValue() + d;
		double fNewMaxValue = getMaxViewValueOrAutoValue() + d;
		
		if(fNewMaxValue >= this.getMaxOrAutoValue() && !Double.isNaN(fMaxViewValue))
		{
			fNewMaxValue = this.getMaxOrAutoValue();
			fNewMinValue = fNewMaxValue - viewLength;
		}
		else if(fNewMinValue <= this.getMinOrAutoValue() && !Double.isNaN(fMinViewValue))
		{
			fNewMinValue = this.getMinOrAutoValue();
			fNewMaxValue = fNewMinValue + viewLength;
		}
		
		
		setViewValues(fNewMaxValue, fNewMinValue); 
	}
	
	/** 
	    * Changes current view values by given amount
	    *
	    *<p>
	    *This method takes view length, multiplies it by factor and divides it by two.
	    *The calculated amount is added and subtracted from current min and max view values respectively
	    *
	    * @param factor  zoom factor
	    */
	
	public void zoomViewValues(float factor)
	{
		if(!this.isZoomable()) return;
		
		double viewLength = getViewLength();
		double d = (viewLength*factor) / 2f;
		
		double fNewMinValue = getMinViewValueOrAutoValue() + d;
		double fNewMaxValue = getMaxViewValueOrAutoValue() - d;
		
		if(fNewMaxValue >= this.getMaxOrAutoValue() && fNewMinValue <= this.getMinOrAutoValue())
		{
			this.resetViewValues();
		}
		else 
		{
			if(fNewMaxValue >= this.getMaxOrAutoValue() && !Double.isNaN(fMaxViewValue))
				fNewMaxValue = this.getMaxOrAutoValue();
			
			if(fNewMinValue <= this.getMinOrAutoValue() && !Double.isNaN(fMinViewValue))
				fNewMinValue = this.getMinOrAutoValue();
			
			setViewValues(fNewMaxValue, fNewMinValue); 
		}		
	}

	/** 
	    * Expands range's auto values 
	    *
	    *<p>
	    *If auto values are not set, then they will be set; otherwise 
	    *minimum value will be set only if minValue is less than min auto value,
	    *and maximum only if maxValue is greater than max auto value
	    *
	    * @param maxValue  new maximum value
	    * @param minValue  new minimum value 
	    */
	public void expandAutoValues(double maxValue,double minValue)
	{			
		if(0 == Double.compare(fMinAutoValue, Double.NaN))
			fMinAutoValue = minValue;
		else if(minValue < fMinAutoValue)
			fMinAutoValue = minValue;
		
		if(0 == Double.compare(fMaxAutoValue, Double.NaN))
			fMaxAutoValue = maxValue;
		else if(maxValue > fMaxAutoValue)
			fMaxAutoValue = maxValue;
		
	}

	/** 
	    * Sets range's maximum and minimum values (NOT view values)
	    *
	    * @param maxValue  new maximum value
	    * @param minValue  new minimum value 
	    */
	
	public void setMaxMinValues(double maxValue,double minValue)
	{
		fMinValue = minValue;
		fMaxValue = maxValue;
	}
	
	public boolean expandViewValues(double maxValue,double minValue)
	{		
		double newMinValue = fMinViewValue;
		double newMaxValue = fMaxViewValue;
		
		if(0 == Double.compare(fMinViewValue, Double.NaN))
			newMinValue = minValue;
		else if(minValue < fMinViewValue)
			newMinValue = minValue;
		
		if(0 == Double.compare(fMaxViewValue, Double.NaN))
			newMaxValue = maxValue;
		else if(maxValue > fMaxViewValue)
			newMaxValue = maxValue;
		
		return this.setViewValues(newMinValue, newMaxValue);
//		if(maxValue <= this.getMaxOrAutoValue() && minValue >= this.getMinOrAutoValue())
//		{
//			if(0 == Double.compare(fMinViewValue, Double.NaN))
//				fMinViewValue = minValue;
//			else if(minValue < fMinViewValue)
//				fMinViewValue = minValue;
//			
//			if(0 == Double.compare(fMaxViewValue, Double.NaN))
//				fMaxViewValue = maxValue;
//			else if(maxValue > fMaxViewValue)
//				fMaxViewValue = maxValue;
//		}
	}
	
	/** 
	    * Sets new view values but always preserves the view length.
	    * Same as <code> setViewValues(ViewValues, Double.NaN)</code>
	    * 
	    * @param vw        the type of values to be set (i.e. SCROLL_TO_LAST to show the most recent values)
	    * @see             #setViewValues(ViewValues)
	    
	    */
	
	public boolean setViewValues(ViewValues vw)
	{
		 return setViewValues(vw, Double.NaN);
	}
	/** 
    * Sets new view values
    *
    * @param vw        the type of values to be set (i.e. SCROLL_TO_LAST to show the most recent values)
    * @param viewSize  the size of the view. If current view size is different 
    * 				   than viewSize it will be changed. Pass Double.NaN 
    * 				   to preserve current view length. In FIT_SCREEN mode viewSize 
    * 				   doesn't make any sence.
    * @return          <code>true</code> if view values were set correctly
    *                  <code>false</code> otherwise.
    */
	public boolean setViewValues(ViewValues vw, double viewSize)
	{
		double viewLength = Double.isNaN(viewSize)?getViewLength():viewSize;
				
		double min = 0.0, max = 0.0;
		
		switch(vw)
		{
		case FIT_SCREEN:
			min = this.getMinOrAutoValue();
			max = this.getMaxOrAutoValue();
			break;
		case SCROLL_TO_LAST:
			max = this.getMaxOrAutoValue();
			min = max - viewLength;
			
			if(min < this.getMinOrAutoValue())
				min = this.getMinOrAutoValue();
			
			break;
		case SCROLL_TO_FIRST:
			min = this.getMinOrAutoValue();
			max = min + viewLength;
			
			if(max > this.getMaxOrAutoValue())
				max = this.getMaxOrAutoValue();
			
			break;

		}
		
		return setViewValues(max,min);
	}
	
	/** 
	    * Sets new view values
	    *
	    * @param maxValue  new view maximum value 
	    * @param minValue  new view minimum value 

	    * @return          <code>true</code> if view values were set correctly
	    *                  <code>false</code> otherwise.
	    */
	
	public boolean setViewValues(double maxValue,double minValue)
	{
		if(maxValue < minValue) return false;
		
		if(maxValue <= this.getMaxOrAutoValue() && minValue >= this.getMinOrAutoValue())
		{							
			double viewLength = maxValue - minValue;
			
			double middle = (maxValue + minValue) * 0.5;
			
			if(!Double.isNaN(fMaxViewLength) && viewLength > fMaxViewLength)
			{
				double d = fMaxViewLength * 0.5;
				
				maxValue = middle + d;
				minValue = middle - d;
			}
			else if(!Double.isNaN(fMinViewLength) && viewLength < fMinViewLength)
			{
				double d = fMinViewLength * 0.5;
				maxValue = middle + d;
				minValue = middle - d;
			}
			
			fMinViewValue = minValue;
			fMaxViewValue = maxValue;
		}
		
		return true;
	}
}
