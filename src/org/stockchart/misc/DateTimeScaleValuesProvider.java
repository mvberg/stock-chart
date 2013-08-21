/**
 * Copyright 20.08.2013 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart.misc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.stockchart.core.Axis.IScaleValuesProvider;
import org.stockchart.core.PaintInfo;
import org.stockchart.series.SeriesBase;

public class DateTimeScaleValuesProvider  implements IScaleValuesProvider 
{	
	private interface IDateComparer
	{
		public boolean compare (Date prevDate, Date date);
	}
	
	private class ComparerListPair 
	{
		private final IDateComparer fComparer;
		private ArrayList<Double> fScaleValues;
		
		ComparerListPair(IDateComparer comparer)
		{
			fComparer = comparer;
		}
		
		public int count()
		{
			if(null == fScaleValues)
				return 0;
			
			return fScaleValues.size();
		}
		
		public void clearList()
		{
			if(null != fScaleValues)
				fScaleValues.clear();
		}
		
		public Double[] getValues()
		{
			if(null == fScaleValues)
				return new Double[0];
						
			return fScaleValues.toArray(new Double[fScaleValues.size()]);
		}
		
		public boolean add(Date prev,Date curr, Double value)
		{
			if(fComparer.compare(prev, curr))
			{
				this.getList().add(value);
				return true;
			}
			
			return false;
		}
		
		public ArrayList<Double> getList()
		{
			if(null == fScaleValues)
			{
				fScaleValues = new ArrayList<Double>();
			}
			
			return fScaleValues;
		}
		
	}
	
	private class SimpleDateComparer implements IDateComparer 
	{	
		private final long fInterval;
		
		SimpleDateComparer(long interval)
		{
			fInterval = interval;
		}
		
		
		public boolean compare(Date prevDate, Date date) 
		{
			return (0 ==  date.getTime() % fInterval);
		}
		
	}
	
	private class ExtDateComparer implements IDateComparer
	{
		private final int fField;
		private final Calendar fCalendar = Calendar.getInstance();
		
		ExtDateComparer(int field)
		{
			fField = field;
		}
		
		@Override
		public boolean compare(Date prevDate, Date date) 
		{
			fCalendar.setTime(prevDate);
			int prev = fCalendar.get(fField);
			
			fCalendar.setTime(date);
			int curr = fCalendar.get(fField);
			
			return prev != curr;
		}
		
	}
	
	private static final int EVERY_SECOND = 1000;
	private static final int EVERY_MINUTE = 60*EVERY_SECOND; // scale value every 1 minutes
	private static final int EVERY_FIVE_MINUTES = 5 * EVERY_MINUTE;
	private static final int EVERY_FIFTEEN_MINUTES = 15 * EVERY_MINUTE;
	private static final int EVERY_THIRTY_MINUTES = 30 * EVERY_MINUTE;
	private static final int EVERY_HOUR = 60 * EVERY_MINUTE;
	private static final int EVERY_FOUR_HOURS = 4*EVERY_HOUR;
	
	private final ComparerListPair[] fPairs = new ComparerListPair[]
			{
				new ComparerListPair(new SimpleDateComparer(EVERY_SECOND)),
				new ComparerListPair(new SimpleDateComparer(EVERY_MINUTE)),
				new ComparerListPair(new SimpleDateComparer(EVERY_FIVE_MINUTES)),
				new ComparerListPair(new SimpleDateComparer(EVERY_FIFTEEN_MINUTES)),
				new ComparerListPair(new SimpleDateComparer(EVERY_THIRTY_MINUTES)),
				new ComparerListPair(new SimpleDateComparer(EVERY_HOUR)),
				new ComparerListPair(new SimpleDateComparer(EVERY_FOUR_HOURS)),
				new ComparerListPair(new ExtDateComparer(Calendar.DAY_OF_MONTH)),
				new ComparerListPair(new ExtDateComparer(Calendar.YEAR)),
			};

	private SeriesBase fSeries;

	private double fMax = Double.NaN;
	private double fMin = Double.NaN;
	private Double[] fLastValuesCache = null;
	
	public DateTimeScaleValuesProvider(SeriesBase series)
	{
		fSeries = series;
	}
	
	@Override
	public Double[] getScaleValues(PaintInfo info, int valuesCount) 
	{	
		if(checkCache(info))
			return fLastValuesCache;
		
		int startIndex = fSeries.convertToArrayIndexZeroBased(info.Min);
		
		clearPairs();
				
		for(int i=startIndex + 1;i<fSeries.getPointCount();i++)
		{
			Date prev = (Date)fSeries.getPointAt(i-1).getID();
			Date curr = (Date)fSeries.getPointAt(i).getID();
			
			double scaleIndex = (double)fSeries.convertToScaleIndex(i);
			
			for(ComparerListPair p:fPairs)
			{
				p.add(prev, curr, scaleIndex - 0.5);
			}
			
		
			if(scaleIndex > info.Max)
				break;
		}
		
		int minCount = Integer.MAX_VALUE;
		
		ComparerListPair winningPair = null;
		
		for(ComparerListPair p:fPairs)
		{
			int diff = Math.abs(p.count() - valuesCount);
			
			if(diff < minCount)
			{
				winningPair = p;
				minCount = diff;
				
				if(diff <= valuesCount)
					break;
			}
		}
				
		fLastValuesCache = (null == winningPair?new Double[0]: winningPair.getValues());
		
		return fLastValuesCache;
	}
	
	private boolean checkCache(PaintInfo pinfo)
	{
		if(fLastValuesCache == null) 
			return false;
		
		if(pinfo.Max != fMax || pinfo.Min != fMin)
		{
			fMax = pinfo.Max;
			fMin = pinfo.Min;
			return false;
		}
		
		return true;
	}
	private void clearPairs()
	{
		for(ComparerListPair p:fPairs)
			p.clearList();
	}
}
