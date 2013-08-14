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
public class RsiIndicator extends AbstractIndicator
{
	private int fPeriodsCount = 14;
	private final LinearSeries fDst;
	
	public RsiIndicator(SeriesBase src, int valueIndex, LinearSeries dst)
	{
		super(src,valueIndex);
		
		fDst = dst;			
	}

	
	public int getPeriodsCount() {
		return fPeriodsCount;
	}


	public void setPeriodsCount(int v) 
	{
		this.fPeriodsCount = v;
	}


	public LinearSeries getDst() {
		return fDst;
	}


	@Override
	public void recalc() 
	{		
		fDst.getPoints().clear();
		
		double k = EmaIndicator.getK(fPeriodsCount);
		
		double uSma = 0.0;
		double dSma = 0.0;
		
		double uEma = Double.NaN;
		double dEma = Double.NaN;
		
		for(int i=1;i<getSrc().getPointCount();i++)
		{
			double[] ud = getUD(i);
			
			if(i >= fPeriodsCount)
			{
				if(Double.isNaN(uEma) && Double.isNaN(dEma))
				{
					uEma = uSma / fPeriodsCount;
					dEma = dSma / fPeriodsCount;
				}
				else
				{
					uEma = ud[0] * k + uEma * (1.0 - k);
					dEma = ud[1] * k + dEma * (1.0 - k);
				}
				
				double rsi = 100.0;
				
				if(dEma != 0.0)
				{
					double rs = uEma / dEma;
					rsi = 100.0 - 100/(1.0 + rs);
				}
				
				fDst.addPoint(rsi);
			}
			else
			{
				uSma += ud[0];
				dSma += ud[1];
			}
		}
		
		this.resetDstIndexOffset(getSrc(), fDst);
	}
	
	private double[] getUD(int i)
	{
		double value =this.getSrcPointAt(i);
		double prevValue = this.getSrcPointAt(i-1);

		double u = value > prevValue?value-prevValue:0.0;
		double d = value < prevValue?prevValue - value:0.0;
		
		return new double[] { u, d };
	}
}
