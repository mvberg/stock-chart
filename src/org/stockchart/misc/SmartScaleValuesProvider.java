/**
 * Copyright 12.09.2012 Alex Vikulov (vikuloff@gmail.com)

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

import org.stockchart.core.Axis.IScaleValuesProvider;
import org.stockchart.core.PaintInfo;

/**
 * @author alexv
 *
 */
public class SmartScaleValuesProvider implements IScaleValuesProvider 
{
	private static final double LEVELS[] = { 1.0, 2.0, 2.5, 5.0, 7.5 };
	private static final double ALL_LEVELS[];
	
	private static final int MIN_DEPTH = -5;	
	private static final int MAX_DEPTH = 10;
	
	private final int fDecimals;
	
	private final double fMinStep;	
	private final int fMultiplier;
	
	static 
	{
		int count = Math.abs(MAX_DEPTH - MIN_DEPTH)*LEVELS.length;
		
		ALL_LEVELS = new double[count];
		
		for(int i=MIN_DEPTH, k = 0;i<MAX_DEPTH;i++)
		{
			double x = Math.pow(10,i);
			
			for(int j=0;j<LEVELS.length;j++,k++)
			{
				ALL_LEVELS[k] = LEVELS[j]*x;
			}
		}
			
	}
	
	public SmartScaleValuesProvider(int decimals)
	{
		// i.e. decimals = 1
		fDecimals = decimals;
		
		// multiplier = 0.1
		fMinStep = Math.pow(10, -decimals);
		fMultiplier = (int)Math.pow(10, decimals);
	}
	
	public int getDecimals()
	{
		return fDecimals;
	}
	

	public Double[] getScaleValues(PaintInfo info, int valuesCount) 
	{	
		long lMax = double2long(info.Max);
		long lMin = double2long(info.Min);
		
		for(int i=0;i<ALL_LEVELS.length;i++)
		{
			long step = double2long(ALL_LEVELS[i]);
			
			if(0 == step) continue;
			
			long max = alignLong(lMax,step);
			long min = alignLong(lMin,step);
						
			long vc = ((max-min)/step);
			
			if(vc > 0 && vc <= valuesCount)
			{
				Double[] result = new Double[(int)vc];
				
				int k = 0;
				for(long v = min + step;v<=max;v+=step, k++)
				{
					result[k] = long2double(v);
				}
				
				return result;				
			}
		}
		
		return null;
	}
	
	private static long alignLong(long v,long step)
	{
		return (v/step)*step;
	}
	
	private double long2double(long l)
	{
		return ((double)l)*fMinStep;
	}
	
	private long double2long(double v)
	{
		return (long)(v * fMultiplier);
	}

}
