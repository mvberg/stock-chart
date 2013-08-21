/**
 * Copyright 20.08.2012 Alex Vikulov (vikuloff@gmail.com)

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

import org.stockchart.indicators.SmaIndicator.SmaIterator;
import org.stockchart.series.LinearSeries;
import org.stockchart.series.RangeSeries;
import org.stockchart.series.SeriesBase;

/**
 * @author alexv
 *
 */
public class BollingerBandsIndicator extends AbstractIndicator 
{
	private final SmaIndicator fSma;
		
	private int fPeriodsCount = 20;
	private double fUpperCoeff = 2.0;
	private double fLowerCoeff = 2.0;
	
	private final LinearSeries fDstSma;
	private final RangeSeries fDstSeries;
	
	public BollingerBandsIndicator(SeriesBase src, int valueIndex, LinearSeries dstSma,RangeSeries bbSeries)
	{
		super(src,valueIndex,bbSeries);
		
		fDstSma = dstSma;
		fDstSeries = bbSeries;
	
		fSma = new SmaIndicator(src,valueIndex,null);
	}
	
	public LinearSeries getDstSMA() {
		return fDstSma;
	}

	public RangeSeries getDstBb() {
		return fDstSeries;
	}

	public int getPeriodsCount() 
	{
		return fPeriodsCount;
	}

	public void setPeriodsCount(int v) 
	{
		this.fPeriodsCount = v;
	}

	public double getUpperCoeff() 
	{
		return fUpperCoeff;
	}

	public void setUpperCoeff(double v) 
	{
		this.fUpperCoeff = v;
	}


	public double getLowerCoeff() 
	{
		return fLowerCoeff;
	}



	public void setLowerCoeff(double v) 
	{
		this.fLowerCoeff = v;
	}



	@Override
	public void recalc() 
	{
		fDstSma.getPoints().clear();
		fDstSeries.getPoints().clear();
				
		fSma.setPeriodsCount(fPeriodsCount);
		
		SmaIterator smaIterator = fSma.iterator();
				
		int i = fPeriodsCount - 1;
		while(smaIterator.hasNext())
		{
			double sma = smaIterator.getNext();
			
			double summ = 0.0;
			
			for(int j=0;j<fPeriodsCount;j++)
			{
				double p = this.getSrcPointAt(i-j);
				
				summ+=(p - sma)*(p-sma);
			}
			
			double stDev = Math.sqrt(summ / fPeriodsCount);
			
			fDstSma.addPoint(sma);
			fDstSeries.addPoint(sma + fUpperCoeff * stDev, sma - fLowerCoeff * stDev);
			i++;
		}
		
		this.resetDstIndexOffset(getSrc(), fDstSma);
		this.resetDstIndexOffset(getSrc(), fDstSeries);
	}

}
