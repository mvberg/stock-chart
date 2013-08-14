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


public class SeriesPaintInfo 
{	
	public SeriesPaintInfo()
	{
		reset();
	}
	
	public float getX(double value)
	{
		return X.get(value);
	}
	
	public float getY(double value)
	{
		return Y.get(value); 
	}
	
	public static float getCoordinate(double value, double min, double factor)
	{
		return (float)((value - min) * factor);
	}
	
	public void loadFrom(Axis xAxis, Axis yAxis)
	{
		X.loadFrom(xAxis);
		Y.loadFrom(yAxis);
	}
	
	public void reset()
	{
//		MaxX = Double.NaN;
//		MinX = Double.NaN;
//		MaxY = Double.NaN;
//		MinY = Double.NaN;
//		
//		FactorX = Double.NaN;
//		FactorY = Double.NaN;		
	}
	
	public final PaintInfo X = new PaintInfo();
	public final PaintInfo Y = new PaintInfo();
	
//	public double MaxX;
//	public double MinX;
//	public double MaxY;
//	public double MinY;
//	
//	public double FactorX;
//	public double FactorY;
}
