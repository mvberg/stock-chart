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

import org.stockchart.points.LinePoint;
import org.stockchart.series.LinearSeries;
import org.stockchart.series.SeriesBase;

/**
 * @author alexv
 *
 */
public class EmaIndicator extends AbstractIndicator
{
	public class EmaIterator
	{
		public double ema = Double.NaN;
		public int index = EmaIndicator.this.fPeriodsCount - 1;
		
		private final double K =  2.0/(1.0 + EmaIndicator.this.fPeriodsCount);
		
		public boolean hasNext()
		{
			return index < EmaIndicator.this.getSrcPointCount();
		}
		
		public double getNext()
		{
			if(Double.isNaN(ema))
			{
				double sma = 0.0;
				
				for(int i=0;i<EmaIndicator.this.fPeriodsCount;i++)
				{
					sma += EmaIndicator.this.getSrcPointAt(i);
				}
				
				ema = sma / EmaIndicator.this.fPeriodsCount;	
				index++;
				return ema;
			}

			ema = EmaIndicator.this.getSrcPointAt(index) * K + ema * (1.0 - K);
			index++;
			
			return ema;
		}		
	}
		
	private int fPeriodsCount = 20;
	private final LinearSeries fDstEma;
	
	public EmaIndicator(SeriesBase src,int valueIndex, LinearSeries dst)
	{
		super(src,valueIndex, dst);
		
		fDstEma = dst;		
	}
	
	public EmaIterator iterator()
	{
		return new EmaIterator();
	}

	
	public int getPeriodsCount() {
		return fPeriodsCount;
	}

	public void setPeriodsCount(int v) {
		this.fPeriodsCount = v;
	}

	public LinearSeries getDstEma() {
		return fDstEma;
	}

	@Override
	public void recalc()
	{
		fDstEma.getPoints().clear();
		
		EmaIterator i = this.iterator();
		while(i.hasNext())
		{
			fDstEma.getPoints().add(new LinePoint(i.getNext()));
		}
		
		this.resetDstIndexOffset(getSrc(), fDstEma);
	}
	
	public static double getK(int periodsCount)
	{
		return 2.0/(1.0 + periodsCount);
	}
}
