package org.stockchart.utils;

import java.util.Date;

public class StockDataGenerator 
{
	private double fVolatility = 2.0;
	private double fStartPrice = 1000.0;
	private double fLastPrice = Double.NaN;
	private double fMaxVolume = 100000.0;
	
	public class Point 
	{
		public Date dt;
		public double o, h, l, c, v;
	}
	
	public StockDataGenerator()
	{
	}
	
	public StockDataGenerator(double volatility,double startPrice,double maxVolume)
	{
		fVolatility = volatility;
		fStartPrice = startPrice;
		fMaxVolume = maxVolume;
	}
	
	public double getVolatility()
	{
		return fVolatility;
	}
	
	public double getStartPrice()
	{
		return fStartPrice;
	}
	
	
	public Point getNextPoint()
	{
		// taken from http://stackoverflow.com/questions/8597731/are-there-known-techniques-to-generate-realistic-looking-fake-stock-data
		double old_price = Double.isNaN(fLastPrice)?fStartPrice:fLastPrice;
		
		double rnd = Math.random();
		double change_percent = 2.0*fVolatility*rnd;
		if(change_percent > fVolatility)
			change_percent-= (2*fVolatility);
		
		double change_amount = (old_price/100.0)*change_percent;
		
		// close
		fLastPrice = old_price + change_amount;
		// open
		double open = old_price;
		// high
		double high = Math.max(open,fLastPrice) + Math.abs(change_amount)*Math.random()*0.5;
		// low
		double low = Math.min(open,fLastPrice) - Math.abs(change_amount)*Math.random()*0.5;
		
		Point p = new Point();
		p.o = open;
		p.h = high;
		p.l = low;
		p.c = fLastPrice;
		p.v = Math.random()*fMaxVolume;
		
		return  p;
	}

}
