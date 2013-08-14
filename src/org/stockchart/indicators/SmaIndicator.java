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

import org.stockchart.series.LinearSeries;
import org.stockchart.series.SeriesBase;

/**
 * @author alexv
 *
 */
public class SmaIndicator extends AbstractIndicator
{	
	private int fPeriodsCount = 26;
	private final LinearSeries fDstSma;
	
	public class SmaIterator
	{
		public double smaSumm = Double.NaN;
		public int index = SmaIndicator.this.fPeriodsCount - 1;
				
		public boolean hasNext()
		{
			return index < SmaIndicator.this.getSrcPointCount();
		}
		
		public double getNext()
		{
			int pk = SmaIndicator.this.fPeriodsCount;
			
			if(Double.isNaN(smaSumm))
			{
				smaSumm = 0.0;
				
				for(int i=0;i<pk;i++)
				{
					smaSumm += SmaIndicator.this.getSrcPointAt(i);
				}				
				
				double result = smaSumm / pk;
				index++;
				
				return result;
			}
			
			smaSumm+=SmaIndicator.this.getSrcPointAt(index);
			smaSumm-=SmaIndicator.this.getSrcPointAt(index - pk);
			
			index++;
			
			return smaSumm/pk;			
		}
		
	}
	
	public SmaIndicator(SeriesBase src, int valueIndex, LinearSeries dst)
	{
		super(src,valueIndex,  dst);
		
		fDstSma = dst;
	}
	
	public int getPeriodsCount() {
		return fPeriodsCount;
	}

	public void setPeriodsCount(int periodsCount) {
		fPeriodsCount = periodsCount;
	}

	public LinearSeries getDstSma() {
		return fDstSma;
	}

	public SmaIterator iterator()
	{
		return new SmaIterator();
	}


	@Override
	public void recalc() 
	{
		fDstSma.getPoints().clear();
		
		SmaIterator i = iterator();
		while(i.hasNext())
		{
			fDstSma.addPoint(i.getNext());
		}
		
		this.resetDstIndexOffset(getSrc(), fDstSma);
		
	}	
}
