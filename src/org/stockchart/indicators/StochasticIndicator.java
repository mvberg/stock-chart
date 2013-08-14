/**
 * Copyright 30.08.2012 Alex Vikulov (vikuloff@gmail.com)

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
 * Implementation of the Stochastic Oscillator refer to {@link http://en.wikipedia.org/wiki/Stochastic_oscillator}
 * 
 */

public class StochasticIndicator extends AbstractIndicator 
{

	private final LinearSeries fDstSlowK;
	private final LinearSeries fDstSlowD;
	private int fPeriodsCount = 20;
	private int fSlowK = 3;
	private int fSlowD = 3;
	
	private final DummySeries fDstFastK = new DummySeries();
	private final EmaIndicator fSlowKEma;
	private final EmaIndicator fSlowDEma;
	/** 
     * Creates new Stochastic indicator with given parameters
     * @param src       	source series
     * @param valueIndex    index of point's value that will be used for calculation
     * 						(if your src is StockSeries Open is 0, High is 1, Low is 2, Close is 3)  
     * @param dstSlowK  	EMA smoothed K% output series
     * @param dstSlowD  	TTwice EMA smoothed K% output series (aka D% series)                        
     */
	
	public StochasticIndicator(SeriesBase src,int valueIndex, LinearSeries dstSlowK,LinearSeries dstSlowD)
	{
		super(src, valueIndex, dstSlowK, dstSlowD);
		fDstSlowK = dstSlowK;
		fDstSlowD = dstSlowD;
		fSlowKEma = new EmaIndicator(fDstFastK,0,fDstSlowK);
		fSlowDEma = new EmaIndicator(fDstSlowK,0,fDstSlowD);
	}	


		
	public int getPeriodsCount() {
		return fPeriodsCount;
	}

	public void setPeriodsCount(int v) {
		this.fPeriodsCount = v;
	}



	public int getSlowK() {
		return fSlowK;
	}



	public void setSlowK(int v) {
		this.fSlowK = v;
	}



	public int getSlowD() {
		return fSlowD;
	}



	public void setSlowD(int v) {
		this.fSlowD = v;
	}



	public LinearSeries getDstSlowK() {
		return fDstSlowK;
	}



	public LinearSeries getDstSlowD() {
		return fDstSlowD;
	}


	@Override
	public void recalc() 
	{		
		fDstFastK.getPoints().clear();
		fDstSlowK.getPoints().clear();
		fDstSlowD.getPoints().clear();
						
		for(int i=fPeriodsCount;i < getSrc().getPointCount();i++)
		{			
			double[] maxMin = getSrc().getMaxMinPrice2(i-fPeriodsCount, i);
			double v = this.getSrcPointAt(i);
			double k = 100.0 * (v - maxMin[1])/(maxMin[0] - maxMin[1]);
			fDstFastK.getPoints().add(new LinePoint(k));
		}
				
		fSlowKEma.setPeriodsCount(fSlowK);		
		fSlowKEma.recalc();				
				
		fSlowDEma.setPeriodsCount(fSlowD);
		fSlowDEma.recalc();
		
		resetDstIndexOffset(getSrc(),fDstFastK);
		resetDstIndexOffset(fDstFastK,fDstSlowK);
		resetDstIndexOffset(fDstSlowK,fDstSlowD);
	}	
}
