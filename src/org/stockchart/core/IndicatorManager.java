/**
 * Copyright 14.08.2013 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart.core;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.StockChartView;
import org.stockchart.indicators.AbstractIndicator;
import org.stockchart.indicators.BollingerBandsIndicator;
import org.stockchart.indicators.EmaIndicator;
import org.stockchart.indicators.EnvelopesIndicator;
import org.stockchart.indicators.MacdIndicator;
import org.stockchart.indicators.RsiIndicator;
import org.stockchart.indicators.SmaIndicator;
import org.stockchart.indicators.StochasticIndicator;
import org.stockchart.series.BarSeries;
import org.stockchart.series.LinearSeries;
import org.stockchart.series.SeriesBase;


public class IndicatorManager 
{
	private final StockChartView fView;	
	private final ArrayList<AbstractIndicator> fIndicators = new ArrayList<AbstractIndicator>();
	
	public IndicatorManager(StockChartView view)
	{
		fView = view;
	}
	
	public ArrayList<AbstractIndicator> getIndicators()
	{
		return fIndicators;
	}
	
	public JSONArray toJSONArray() throws JSONException
	{
		JSONArray indicators = new JSONArray();
		for(AbstractIndicator a:fIndicators)		
		{
			indicators.put(this.indicatorToJSONObject(a));
		}		
		
		return indicators;
		
	}
	
	public void fromJSONArray(JSONArray indicators) throws JSONException
	{
		for(int i=0;i<indicators.length();i++)
		{
			JSONObject ind = indicators.getJSONObject(i);
			
			fIndicators.add(this.jsonObjectToIndicator(ind));
		}

	}
	
	public void removeIndicator(AbstractIndicator i)
	{
		for(SeriesBase s:i.getDst())
		{
			Area a = fView.findAreaBySeriesName(s.getName());
			
			if(a.getSeries().remove(s))
			{
				if(a.getSeries().size() == 0)
				{
					fView.getAreas().remove(a);
				}
			}
		}
		
		fIndicators.remove(i);
	}
	
	public StochasticIndicator addStochastic(SeriesBase src, int valueIndex)
	{
		LinearSeries stoch1 = new LinearSeries();
		LinearSeries stoch2 = new LinearSeries();
		
		// clone parameters
		stoch1.setXAxisSide(src.getXAxisSide());
		stoch1.setYAxisSide(src.getYAxisSide());

		stoch2.setXAxisSide(src.getXAxisSide());
		stoch2.setYAxisSide(src.getYAxisSide());
		
		// adding to area
		Area parent = fView.findAreaBySeriesName(src.getName());
		
		Area a = createArea(parent);
		fView.getAreas().add(a);
		
		// add series
		a.getSeries().add(stoch1);
		a.getSeries().add(stoch2);
		
		StochasticIndicator i = new StochasticIndicator(src,valueIndex,stoch1,stoch2);
		
		fIndicators.add(i);
		
		return i;
	}
	
	
	public RsiIndicator addRsi(SeriesBase src, int valueIndex)
	{
		LinearSeries rsi = new LinearSeries();
		
		// clone parameters
		 rsi.setXAxisSide(src.getXAxisSide());
		 rsi.setYAxisSide(src.getYAxisSide());

	
		// adding to area
		Area parent = fView.findAreaBySeriesName(src.getName());
		
		Area a = createArea(parent);
		fView.getAreas().add(a);
		
		// add series
		a.getSeries().add(rsi);
		
		RsiIndicator i = new RsiIndicator(src,valueIndex,rsi);
		
		fIndicators.add(i);
		
		return i;
	}
	
	
	public MacdIndicator addMacd(SeriesBase src, int valueIndex)
	{
		LinearSeries macd = new LinearSeries();
		LinearSeries signal = new LinearSeries();
		BarSeries hist = new BarSeries();
		
		// clone parameters
		macd.setXAxisSide(src.getXAxisSide());
		macd.setYAxisSide(src.getYAxisSide());

		signal.setXAxisSide(src.getXAxisSide());
		signal.setYAxisSide(src.getYAxisSide());

		hist.setXAxisSide(src.getXAxisSide());
		hist.setYAxisSide(src.getYAxisSide());
	
		// adding to area
		Area parent = fView.findAreaBySeriesName(src.getName());
		
		Area a = createArea(parent);
		fView.getAreas().add(a);
		
		// add series
		a.getSeries().add(macd);
		a.getSeries().add(signal);
		a.getSeries().add(hist);
		
		MacdIndicator i = new MacdIndicator(src,valueIndex,macd,signal,hist);
		
		fIndicators.add(i);
		
		return i;
	}
	
	public SmaIndicator addSma(SeriesBase src, int valueIndex)
	{
		LinearSeries dst = new LinearSeries();
		
		// clone parameters
		dst.setXAxisSide(src.getXAxisSide());
		dst.setYAxisSide(src.getYAxisSide());
		
		// adding to area
		Area a = fView.findAreaBySeriesName(src.getName());
		a.getSeries().add(dst);
		
		SmaIndicator i = new SmaIndicator(src, valueIndex, dst);
		
		fIndicators.add(i);
		
		return i;
	}
	
	public EmaIndicator addEma(SeriesBase src, int valueIndex)
	{
		LinearSeries dst = new LinearSeries();
		
		// clone parameters
		dst.setXAxisSide(src.getXAxisSide());
		dst.setYAxisSide(src.getYAxisSide());
		
		// adding to area
		Area a = fView.findAreaBySeriesName(src.getName());
		a.getSeries().add(dst);
		
		EmaIndicator i = new EmaIndicator(src, valueIndex, dst);
		
		fIndicators.add(i);
		
		return i;
	}
	
	public EnvelopesIndicator addEnvelopes(SeriesBase src, int valueIndex)
	{
		LinearSeries upper = new LinearSeries();
		LinearSeries lower = new LinearSeries();
		
		// clone parameters
		upper.setXAxisSide(src.getXAxisSide());
		upper.setYAxisSide(src.getYAxisSide());
		
		lower.setXAxisSide(src.getXAxisSide());
		lower.setYAxisSide(src.getYAxisSide());
		
		// adding to area
		Area a = fView.findAreaBySeriesName(src.getName());
		a.getSeries().add(upper);
		a.getSeries().add(lower);
		
		EnvelopesIndicator i = new EnvelopesIndicator(src, valueIndex, upper, lower);
		
		fIndicators.add(i);
		
		return i;
	}
	
	public BollingerBandsIndicator addBollingerBands(SeriesBase src, int valueIndex)
	{
		LinearSeries sma = new LinearSeries();
		LinearSeries upper = new LinearSeries();
		LinearSeries lower = new LinearSeries();
		
		// clone parameters
		upper.setXAxisSide(src.getXAxisSide());
		upper.setYAxisSide(src.getYAxisSide());
		
		lower.setXAxisSide(src.getXAxisSide());
		lower.setYAxisSide(src.getYAxisSide());
		
		sma.setXAxisSide(src.getXAxisSide());
		sma.setYAxisSide(src.getYAxisSide());
		
		// adding to area
		Area a = fView.findAreaBySeriesName(src.getName());
		a.getSeries().add(upper);
		a.getSeries().add(lower);
		a.getSeries().add(sma);
		
		BollingerBandsIndicator i = new BollingerBandsIndicator(src, valueIndex, sma, upper, lower);
		
		fIndicators.add(i);
		
		return i;
	}
	
	private static Area createArea(Area parent)
	{
		Area a = new Area();
		
		a.setAutoHeight(false);
		a.setHeightInPercents(0.2f);
		a.setAxesVisible(parent.getLeftAxis().isVisible(),
						 false,
						 parent.getRightAxis().isVisible(),
						 false);
		
		return a;
	}
	
	private AbstractIndicator jsonObjectToIndicator(JSONObject j) throws JSONException
	{
		String type = j.getString("type");
		int valueIndex = j.getInt("valueIndex");
		SeriesBase src = fView.findSeriesByName(j.getString("src"));
		
		AbstractIndicator result = null;
		
		if(type.equals("sma"))
		{
			LinearSeries dst = (LinearSeries)fView.findSeriesByName(j.getString("dst"));
			int pk = j.getInt("periodsCount");
			
			SmaIndicator s = new SmaIndicator(src,valueIndex,dst);
			s.setPeriodsCount(pk);
			result = s;
		}
		else if(type.equals("ema"))
		{
			LinearSeries dst = (LinearSeries)fView.findSeriesByName(j.getString("dst"));
			int pk = j.getInt("periodsCount");
			
			EmaIndicator e = new EmaIndicator(src,valueIndex,dst);
			e.setPeriodsCount(pk);
			result = e;
		}
		else if(type.equals("rsi"))
		{
			LinearSeries dst = (LinearSeries)fView.findSeriesByName(j.getString("dst"));
			int pk = j.getInt("periodsCount");
			
			RsiIndicator rsi = new RsiIndicator(src,valueIndex,dst); 
			rsi.setPeriodsCount(pk);
			result = rsi;
		}
		else if(type.equals("envelopes"))		
		{
			LinearSeries upper = (LinearSeries)fView.findSeriesByName(j.getString("dstUpperEnvelope"));
			LinearSeries lower = (LinearSeries)fView.findSeriesByName(j.getString("dstLowerEnvelope"));
			
			int pk = j.getInt("periodsCount");
			double percent = j.getDouble("percent");
			
			EnvelopesIndicator env = new EnvelopesIndicator(src,valueIndex,upper,lower);
			env.setPeriodsCount(pk);
			env.setPercent(percent);
			result = env;
		}
		else if(type.equals("macd"))
		{
			LinearSeries macd = (LinearSeries)fView.findSeriesByName(j.getString("dstMacd"));
			LinearSeries signal = (LinearSeries)fView.findSeriesByName(j.getString("dstSignal"));
			BarSeries bar = (BarSeries)fView.findSeriesByName(j.getString("dstHistogram"));
			int longPeriod = j.getInt("longMacdPeriod");
			int shortPeriod = j.getInt("shortMacdPeriod");
			int signalPeriod = j.getInt("signalMacdPeriod");
			
			MacdIndicator m = new MacdIndicator(src,valueIndex,macd,signal,bar);
			m.setLongMacdPeriod(longPeriod);
			m.setShortMacdPeriod(shortPeriod);
			m.setSignalPeriod(signalPeriod);
			
			result = m;
		}
		else if(type.equals("bb"))
		{
			LinearSeries sma = (LinearSeries)fView.findSeriesByName(j.getString("dstSma"));
			LinearSeries upper = (LinearSeries)fView.findSeriesByName(j.getString("dstUpper"));
			LinearSeries lower = (LinearSeries)fView.findSeriesByName(j.getString("dstLower"));
			
			int periodsCount = j.getInt("periodsCount");
			double lowerCoeff = j.getDouble("lowerCoeff");
			double upperCoeff = j.getDouble("upperCoeff");
						
			BollingerBandsIndicator bb = new BollingerBandsIndicator(src,valueIndex,sma,upper,lower);
			bb.setPeriodsCount(periodsCount);
			bb.setLowerCoeff(lowerCoeff);
			bb.setUpperCoeff(upperCoeff);
			result = bb;
		}
		else if(type.equals("stoch"))
		{
			LinearSeries d = (LinearSeries)fView.findSeriesByName(j.getString("dstSlowD"));
			LinearSeries k = (LinearSeries)fView.findSeriesByName(j.getString("dstSlowK"));
			
			int periodsCount = j.getInt("periodsCount");
			int sd = j.getInt("slowD");
			int sk = j.getInt("slowK");
			
			StochasticIndicator s = new StochasticIndicator(src,valueIndex,d,k);
			s.setPeriodsCount(periodsCount);
			s.setSlowD(sd);
			s.setSlowK(sk);
			
			result = s;
		}

		return result;
	}
	
	private JSONObject indicatorToJSONObject(AbstractIndicator a) throws JSONException
	{
		JSONObject obj = new JSONObject();
		obj.put("valueIndex", a.getValueIndex());
		obj.put("src", a.getSrc().getName());
		
		if(a instanceof EmaIndicator)
		{
			EmaIndicator ema = (EmaIndicator)a;
			obj.put("type", "ema");
			obj.put("periodsCount", ema.getPeriodsCount());
			obj.put("dst", ema.getDstEma().getName());
		}
		else if(a instanceof SmaIndicator)
		{
			SmaIndicator sma = (SmaIndicator)a;
			obj.put("type", "sma");
			obj.put("periodsCount", sma.getPeriodsCount());
			obj.put("dst", sma.getDstSma().getName());
		}
		else if(a instanceof RsiIndicator)
		{
			RsiIndicator rsi = (RsiIndicator)a;
			obj.put("type", "rsi");
			obj.put("periodsCount", rsi.getPeriodsCount());
			obj.put("dst", rsi.getDstRsi().getName());
		}
		else if(a instanceof EnvelopesIndicator)
		{
			EnvelopesIndicator env = (EnvelopesIndicator)a;
			obj.put("type", "envelopes");
			obj.put("periodsCount", env.getPeriodsCount());
			obj.put("dstUpperEnvelope", env.getDstUpperEnvelope().getName());
			obj.put("dstLowerEnvelope", env.getDstLowerEnvelope().getName());
			obj.put("percent", env.getPercent());
		}
		else if(a instanceof MacdIndicator)
		{
			MacdIndicator macd = (MacdIndicator)a;
			obj.put("type", "macd");
			obj.put("longMacdPeriod", macd.getLongMacdPeriod());
			obj.put("shortMacdPeriod", macd.getShortMacdPeriod());
			obj.put("signalMacdPeriod", macd.getSignalPeriod());
			obj.put("dstMacd", macd.getDstMacd().getName());
			obj.put("dstSignal", macd.getDstSignal().getName());
			obj.put("dstHistogram", macd.getDstHistogram().getName());	
		}
		else if(a instanceof BollingerBandsIndicator)
		{
			BollingerBandsIndicator bb = (BollingerBandsIndicator)a;
			obj.put("type", "bb");
			obj.put("periodsCount", bb.getPeriodsCount());
			obj.put("lowerCoeff", bb.getLowerCoeff());
			obj.put("upperCoeff", bb.getUpperCoeff());
			obj.put("dstUpper", bb.getDstUpperBand().getName());
			obj.put("dstLower", bb.getDstLowerBand().getName());
			obj.put("dstSma", bb.getDstSMA().getName());
		}
		else if(a instanceof StochasticIndicator)
		{
			StochasticIndicator s = (StochasticIndicator)a;
			obj.put("type", "stoch");
			obj.put("periodsCount", s.getPeriodsCount());
			obj.put("slowD", s.getSlowD());
			obj.put("slowK", s.getSlowK());
			obj.put("dstSlowD", s.getDstSlowD().getName());
			obj.put("dstSlowK", s.getDstSlowK().getName());
		}
		
		return obj;

	}
}
