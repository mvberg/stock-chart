/**
 * Copyright 10.04.2012 Alex Vikulov (vikuloff@gmail.com)

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

import org.stockchart.utils.DoubleUtils;

/**
 * @author alexv
 *
 */
public class PaintInfo 
{
	public double Max;
	public double Min;

	public boolean IsX = true;
	
	public boolean IsLogarithmic = false;
	
	public float Size = 0f;
	
	public void loadFrom(Axis axis)	
	{
		IsLogarithmic = axis.isLogarithmic();
		
		AxisRange range = axis.getAxisRangeOrGlobalAxisRange();
		
		Max = range.getMaxViewValueOrAutoValue();
		Min = range.getMinViewValueOrAutoValue();	
		IsX = axis.isHorizontal();
		Size = axis.isVertical()?axis.getBounds().height():axis.getBounds().width();
	}
	
	public float get(double value)
	{
		double max = IsLogarithmic?Math.log10(Max):Max;
		double min = IsLogarithmic?Math.log10(Min):Min;		
		double v = IsLogarithmic?Math.log10(value):value;
		double length = max - min;
		double factor = Size / length;
		
		return IsX?getCoordinate(v,min,factor):getCoordinate(max,v,factor);
	}
			
	private static float getCoordinate(double value, double min, double factor)
	{
		if(DoubleUtils.equals(value, min)) return 0.0f;
		
		return (float)((value - min) * factor);
	}
}
