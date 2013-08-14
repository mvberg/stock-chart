/**
 * Copyright 01.08.2012 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart.points;

/**
 * @author alexv
 *
 */
public abstract class AbstractPoint 
{
	private Object fID = null;
	private boolean fVisible = true;	
	private final double[] fValues; 
	
	public double[] getMaxMin()
	{
		double[] maxMin = new double[] { fValues[0], fValues[0] };
		
		for(int i=1;i < fValues.length;i++)
		{
			if(fValues[i] > maxMin[0]) 
				maxMin[0] = fValues[i];
			else if(fValues[i] < maxMin[1])
				maxMin[1] = fValues[i];
		}
		
		return maxMin;
	}
		
	public AbstractPoint(int valuesCount)
	{
		fValues = new double[valuesCount];
	}
	
	public AbstractPoint(double[] values)
	{
		fValues = values;
	}
	
	public double getValueAt(int i)
	{
		return fValues[i];
	}
	
	public void setValueAt(int i,double v)
	{
		fValues[i] = v;
	}
	
	public double[] getValues()
	{
		return fValues;		
	}
	
	public void setValues(double[] v)
	{
		for(int i=0;i<v.length;i++)
			fValues[i] = v[i];
	}
	
	public Object getID()
	{
		return fID;
	}	
	
	public void setID(Object id)
	{
		fID = id;
	}
	
	public boolean isVisible()
	{
		return fVisible;
	}
	
	public void setVisible(boolean value)
	{
		fVisible = value;
	}
}
