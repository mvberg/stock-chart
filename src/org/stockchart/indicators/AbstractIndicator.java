/**
 * Copyright 18.07.2012 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart.indicators;

import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.points.AbstractPoint;
import org.stockchart.points.LinePoint;
import org.stockchart.series.AbstractSeries;
import org.stockchart.series.SeriesBase;

import android.graphics.Canvas;

/**
 * @author alexv
 *
 */
public abstract class AbstractIndicator 
{		
	private final SeriesBase fSrc;
	private int fValueIndex;
	
	protected class DummySeries extends AbstractSeries<LinePoint>
	{
		@Override
		protected void drawPoint(Canvas c, SeriesPaintInfo pinfo, float x1,	float x2, LinePoint p) { } 		
	}
	
	public  <T extends AbstractPoint> AbstractIndicator(SeriesBase src,int valueIndex)
	{
		fSrc = src;
		fValueIndex = valueIndex;		
	}
	
	public int getValueIndex()
	{
		return fValueIndex;
	}
	
	public void setValueIndex(int v)
	{
		fValueIndex = v;
	}
	
	public SeriesBase getSrc() 
	{
		return fSrc;
	}
	
	/**
	 * Recalcs the indicator using given parameters
	 * <p>
	 * This method clears any Dst... series and recalculates them 
	 * again using indicator parameters
	 * 
	 * */
	public abstract void recalc();
	
	protected int getSrcPointCount()
	{
		return fSrc.getPointCount();
	}
	
	protected double getSrcPointAt(int i)
	{		
		return ((AbstractPoint)fSrc.getPointAt(i)).getValues()[fValueIndex];
	}
	
	protected void resetDstIndexOffset(SeriesBase src, SeriesBase dst)	
	{
		dst.setIndexOffset(src.getIndexOffset() + (src.getPointCount() - dst.getPointCount()));		
	}
}
