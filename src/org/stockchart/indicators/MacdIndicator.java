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
import org.stockchart.series.BarSeries;
import org.stockchart.series.LinearSeries;
import org.stockchart.series.SeriesBase;

/**
 * @author alexv
 *
 */
public class MacdIndicator extends AbstractIndicator
{
	private final EmaIndicator fLongEma;
	private final EmaIndicator fShortEma;
	private final EmaIndicator fSignalEma;
	
	private final LinearSeries fDstMacd;
	private final LinearSeries fDstSignal;
	private final BarSeries fDstHistogram;
	
	private int fLongMacdPeriod = 26;
	private int fShortMacdPeriod = 12;
	private int fSignalPeriod = 9;
	
	public MacdIndicator(SeriesBase src, int valueIndex, LinearSeries dstMacd,LinearSeries dstSignal,BarSeries dstHistogram)
	{
		super(src,valueIndex);
		
		fDstMacd = dstMacd;
		fDstSignal = dstSignal;
		fDstHistogram = dstHistogram;
				
		fLongEma = new EmaIndicator(src,valueIndex,null);
		fShortEma = new EmaIndicator(src,valueIndex,null);
		
		fSignalEma = new EmaIndicator(fDstMacd,0,null);
	}


	public int getLongMacdPeriod() {
		return fLongMacdPeriod;
	}


	public void setLongMacdPeriod(int longMacdPeriod) {
		fLongMacdPeriod = longMacdPeriod;
	}


	public int getShortMacdPeriod() {
		return fShortMacdPeriod;
	}


	public void setShortMacdPeriod(int shortMacdPeriod) {
		fShortMacdPeriod = shortMacdPeriod;
	}


	public int getSignalPeriod() {
		return fSignalPeriod;
	}


	public void setSignalPeriod(int signalPeriod) {
		fSignalPeriod = signalPeriod;
	}


	public LinearSeries getDstMacd() {
		return fDstMacd;
	}


	public LinearSeries getDstSignal() {
		return fDstSignal;
	}


	public BarSeries getDstHistogram() {
		return fDstHistogram;
	}


	@Override
	public void recalc() 
	{	
		fDstMacd.getPoints().clear();
		fDstSignal.getPoints().clear();
		fDstHistogram.getPoints().clear();

		fLongEma.setPeriodsCount(fLongMacdPeriod);
		EmaIterator longIterator  = fLongEma.iterator();
				
		fShortEma.setPeriodsCount(fShortMacdPeriod);
		EmaIterator shortIterator = fShortEma.iterator();
		

		int i = fShortMacdPeriod;
		while(shortIterator.hasNext())
		{
			double shortMacd = shortIterator.getNext();
			
			if(i >= fLongMacdPeriod)
			{
				if(!longIterator.hasNext()) return;
				
				double longMacd = longIterator.getNext();
				
				fDstMacd.addPoint(shortMacd - longMacd);
			}
			
			i++;
		}
		
		fSignalEma.setPeriodsCount(fSignalPeriod);
		EmaIterator signalIterator = fSignalEma.iterator();
		
		int k = fSignalPeriod - 1;
		while(signalIterator.hasNext())
		{
			double signal = signalIterator.getNext();
			
			double macd = fDstMacd.getPoints().get(k).getValue();
			
			fDstSignal.addPoint(signal);
			fDstHistogram.addPoint(0.0, macd - signal);
			k++;
		}
		
		this.resetDstIndexOffset(getSrc(), fDstMacd);
		this.resetDstIndexOffset(fDstMacd, fDstSignal);
		this.resetDstIndexOffset(fDstMacd, fDstHistogram);
	
	}}
