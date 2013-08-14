/**
 * Copyright 2011 Alex Vikulov (vikuloff@gmail.com)

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


public class StockPoint extends AbstractPoint
{
	public enum PointValue
	{
		HIGH,
		LOW,
		OPEN,
		CLOSE
	}	
		
	public double getHigh() 
	{
		return this.getValueAt(getValueIndex(PointValue.HIGH));
	}

	public void setHigh(double high) 
	{
		this.setValueAt(getValueIndex(PointValue.HIGH),high);
	}

	public double getLow() 
	{
		return this.getValueAt(getValueIndex(PointValue.LOW));
	}

	public void setLow(double low) {
		this.setValueAt(getValueIndex(PointValue.LOW),low);
	}

	public double getOpen() 
	{
		return this.getValueAt(getValueIndex(PointValue.OPEN));
	}

	public void setOpen(double open) 
	{
		this.setValueAt(getValueIndex(PointValue.OPEN),open);
	}

	public double getClose() {
		return this.getValueAt(getValueIndex(PointValue.CLOSE));
	}

	public void setClose(double close) {
		this.setValueAt(getValueIndex(PointValue.CLOSE),close);
	}

	public StockPoint()
	{
		super(4);
	}
	
	public StockPoint(double o, double h, double l, double c)
	{
		super(new double[] { o, h, l, c} );

	}
	
	public double getValue(PointValue v)
	{
		switch(v)
		{
		case HIGH: return this.getHigh();
		case LOW: return this.getLow();
		case OPEN: return this.getOpen();
		case CLOSE: return this.getClose();
		}
		
		return Double.NaN;
	}
	
	public void setValues(double o, double h, double l, double c)
	{
		super.setValues(new double[] { o, h, l, c });
	}
	
	@Override
	public double[] getMaxMin()
	{
		return new double[] { getHigh(), getLow() }; 
	}
	
	public static int getValueIndex(PointValue pv)
	{
		switch(pv)
		{
		case OPEN: return 0;
		case HIGH: return 1;
		case LOW: return 2;
		case CLOSE: return 3;
		}
		
		return -1;
	}	
}
