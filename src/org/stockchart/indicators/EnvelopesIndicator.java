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

import org.stockchart.indicators.EmaIndicator.EmaIterator;
import org.stockchart.series.RangeSeries;
import org.stockchart.series.SeriesBase;

/**
 * @author alexv
 *
 */
public class EnvelopesIndicator extends AbstractIndicator 
{
	private final EmaIndicator fEma;
		
	private double fPercent = 1.0;
	private int fPeriodsCount = 26;
	private final RangeSeries fDstEnvelopes;
	
	public EnvelopesIndicator(SeriesBase src, int valueIndex, RangeSeries dstEnvelopes)
	{
		super(src,valueIndex, dstEnvelopes);
		
		fDstEnvelopes = dstEnvelopes;
				
		fEma = new EmaIndicator(src,valueIndex, null);
	}

	
	public double getPercent() {
		return fPercent;
	}


	public void setPercent(double percent) {
		fPercent = percent;
	}


	public int getPeriodsCount() {
		return fPeriodsCount;
	}


	public void setPeriodsCount(int periodsCount) {
		fPeriodsCount = periodsCount;
	}


	public RangeSeries getDstEnvelopes() {
		return fDstEnvelopes;
	}


	public void recalc()
	{
		fDstEnvelopes.getPoints().clear();
	
		fEma.setPeriodsCount(fPeriodsCount);
		EmaIterator i = fEma.iterator();
		
		while(i.hasNext())
		{
			double value = i.getNext();
			double lower = value*(1.0 - fPercent/100.0);
			double upper = value*(1.0 + fPercent/100.0);
			
			fDstEnvelopes.addPoint(lower, upper);
		}
		
		this.resetDstIndexOffset(getSrc(), fDstEnvelopes);
	}
}
