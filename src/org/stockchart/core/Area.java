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

package org.stockchart.core;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Axis.Side;
import org.stockchart.series.SeriesBase;
import org.stockchart.stickers.AbstractSticker;
import org.stockchart.utils.CustomObjects;
import org.stockchart.utils.PaintUtils;
import org.stockchart.utils.Reflection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

public class Area extends ChartElement
{
	private final ArrayList<SeriesBase> fSeries = new ArrayList<SeriesBase>();
	private final ArrayList<Line> fLines = new ArrayList<Line>();
	private final ArrayList<AbstractSticker> fStickers = new ArrayList<AbstractSticker>();
	
	private final Axis[] fAxes;
	
	private final Appearance fAreaAppearance = new Appearance();
		
	private Axis.Side fVerticalGridAxisSide = Axis.Side.BOTTOM;
	private Axis.Side fHorizontalGridAxisSide = Axis.Side.RIGHT;
	
	private boolean fVerticalGridVisible = true;
	private boolean fHorizontalGridVisible = true;

	private boolean fAutoHeight = true;

	private float fHeightInPercents = 0;
							
	private final Plot fPlot = new Plot(this);
	private final Legend fLegend = new Legend(this);
	
	private String fTitle = "";
	
	private String fName;
	
	private static int DEFAULT_NAME_ID = 0;
	
	private float fGlobalLeftMargin = 0f;
	private float fGlobalRightMargin = 0f;

	private boolean fVisible = true;

	public Area()
	{
		super(null);
		
		fAxes = new Axis[] 
					{ 
						new Axis(this,Side.LEFT),
						new Axis(this,Side.RIGHT),
						new Axis(this,Side.TOP),
						new Axis(this,Side.BOTTOM)
					}; 
		
		fName = "Area"+String.valueOf(++DEFAULT_NAME_ID);
				
		fAreaAppearance.setOutlineColor(Color.BLACK);
		
		Theme.fillAppearanceFromCurrentTheme(Area.class, fAreaAppearance);
		
	}		
	
	public float getCoordinate(Axis.Side side, double value)
	{
		return this.getAxis(side).getCoordinate(value);
	}
	
	public double getValueByCoordinate(Axis.Side side, float coordinate,boolean isAbsolute)
	{
		return this.getAxis(side).getValueByCoordinate(coordinate, isAbsolute);
	}
	
	public boolean isVisible()
	{
		return fVisible;
	}
	
	public void setVisible(boolean v)
	{
		fVisible = v;
	}
	
	public Legend getLegend()
	{
		return fLegend;
	}
	
	public ArrayList<AbstractSticker> getStickers()
	{
		return fStickers;
	}
	
	public Appearance getAppearance()
	{
		return fAreaAppearance;	
	}
	
	/**	Finds series by name. 
	 * 
	 * @return <code>SeriesBase</code> if series with given name presents in this area
	 * 		   <code>null</code> otherwise 
	 * */
	public SeriesBase findSeriesByName(String name)
	{
		for(SeriesBase s: fSeries)
		{
			if(s.getName().equals(name)) return s;			
		}
		
		return null;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{		
		fTitle = j.getString("title");
		fName = j.getString("name");
		fVisible = j.getBoolean("visible");
		fAutoHeight = j.getBoolean("autoHeight");
		fHeightInPercents = (float)j.getDouble("heightInPercents");
		fHorizontalGridVisible = j.getBoolean("horizontalGridVisible");
		fVerticalGridVisible = j.getBoolean("verticalGridVisible");
		fVerticalGridAxisSide = Axis.Side.valueOf(j.getString("verticalGridAxis"));
		fHorizontalGridAxisSide = Axis.Side.valueOf(j.getString("horizontalGridAxis"));
		fAreaAppearance.fromJSONObject(j.getJSONObject("areaAppearance"));
		fPlot.getAppearance().fromJSONObject(j.getJSONObject("plotAppearance"));
		fLegend.fromJSONObject(j.getJSONObject("legend"));
		JSONArray ja = null;
		
		ja = j.getJSONArray("axes");
		int i = 0;
		for(Axis axis:fAxes)
		{
			axis.fromJSONObject(ja.getJSONObject(i));
			i++;
		}
		
		ja = j.getJSONArray("lines");
		fLines.clear();
		for(int k=0;k<ja.length();k++)
		{
			Line l = new Line();
			l.fromJSONObject(ja.getJSONObject(k));
			fLines.add(l);
		}
		
		ja = j.getJSONArray("series");
		fSeries.clear();
		for(int k=0;k<ja.length();k++)
		{
			JSONObject ss = ja.getJSONObject(k);
			String cn = ss.getString("className");		
			
			SeriesBase as = (SeriesBase)Reflection.newInstance(cn);
			as.fromJSONObject(ss.getJSONObject("params"));
			
			fSeries.add(as);
		}

		ja = j.getJSONArray("stickers");
		fStickers.clear();
		for(int k=0;k<ja.length();k++)
		{
			JSONObject ss = ja.getJSONObject(k);
			String cn = ss.getString("className");		
			
			AbstractSticker as = (AbstractSticker)Reflection.newInstance(cn);
			as.fromJSONObject(ss.getJSONObject("params"));
			
			fStickers.add(as);
		}
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject r = new JSONObject();
		
		r.put("title", fTitle);
		r.put("visible", fVisible);
		r.put("name", fName);
		r.put("autoHeight", fAutoHeight);
		r.put("heightInPercents", fHeightInPercents);
		r.put("verticalGridAxis", fVerticalGridAxisSide);
		r.put("horizontalGridAxis", fHorizontalGridAxisSide);
		r.put("horizontalGridVisible",fHorizontalGridVisible);
		r.put("verticalGridVisible",fVerticalGridVisible);
		r.put("areaAppearance",fAreaAppearance.toJSONObject());
		r.put("plotAppearance",fPlot.getAppearance().toJSONObject());
		r.put("legend", fLegend.toJSONObject());
		
		JSONArray axes = new JSONArray();
		
		for(Axis axis:fAxes)
		{
			axes.put(axis.toJSONObject());
		}
		
		JSONArray lines = new JSONArray();
		for(Line l:fLines)
		{
			lines.put(l.toJSONObject());
		}
		
		JSONArray series = new JSONArray();
		for(SeriesBase s:fSeries)
		{
			JSONObject ss = new JSONObject();
			ss.put("className", s.getClass().getName());
			ss.put("params", s.toJSONObject());
			
			series.put(ss);
		}
		
		JSONArray stickers = new JSONArray();
		for(AbstractSticker s:fStickers)
		{
			JSONObject ss = new JSONObject();
			ss.put("className", s.getClass().getName());
			ss.put("params", s.toJSONObject());
			
			stickers.put(ss);
		}
		
		r.put("axes", axes);
		r.put("series",series);
		r.put("lines", lines);
		r.put("stickers", stickers);
				
		return r;
		
	}
	
	public void setAllAxesVisible(boolean value)
	{
		for(Axis a:this.fAxes)
		{
			a.setVisible(value);
		}
	}
	
	public void setAxesVisible(boolean left,boolean top,boolean right,boolean bottom)
	{
		this.getLeftAxis().setVisible(left);
		this.getRightAxis().setVisible(right);
		this.getTopAxis().setVisible(top);
		this.getBottomAxis().setVisible(bottom);
	}
	
	public void setGlobalMargins(float left,float right)
	{
		this.fGlobalLeftMargin = left;
		this.fGlobalRightMargin = right;
	}
	
	public Axis getVerticalGridAxis()
	{
		return getAxis(fVerticalGridAxisSide);
	}
	
	public Axis getHorizontalGridAxis()
	{
		return getAxis(fHorizontalGridAxisSide);
	}
	
	public Axis.Side getVerticalGridAxisSide() 
	{
		return fVerticalGridAxisSide;
	}

	public void setVerticalGridAxis(Axis.Side v) 
	{
		fVerticalGridAxisSide = v;
	}

	public Axis.Side getHorizontalGridAxisSide() 
	{
		return fHorizontalGridAxisSide;
	}

	public void setHorizontalGridAxisSide(Axis.Side v) 
	{
		fHorizontalGridAxisSide = v;
	}

	public boolean isVerticalGridVisible() 
	{
		return fVerticalGridVisible;
	}

	public void setVerticalGridVisible(boolean v) {
		fVerticalGridVisible = v;
	}

	public boolean isHorizontalGridVisible() 
	{
		return fHorizontalGridVisible;
	}

	public void setHorizontalGridVisible(boolean v) 
	{
		fHorizontalGridVisible = v;
	}

	public ArrayList<SeriesBase> getSeries() 
	{
		return fSeries;
	}

	public ArrayList<Line> getLines() 
	{
		return fLines;
	}

	public Plot getPlot()
	{
		return fPlot;
	}
	
	public String getName()
	{
		return fName;
	}
	
	public void setName(String value)
	{
		fName = value;
	}
	
	public String getTitle()
	{
		return fTitle;
	}
	
	public void setTitle(String s)
	{
		fTitle = s;
	}
	
	public void move(float hFactor, float vFactor)
	{
		for(Axis a: fAxes)
		{
			if(a.isHorizontal())									
				a.getAxisRange().moveViewValues(hFactor);
			else if(a.isVertical())
				a.getAxisRange().moveViewValues(vFactor);
		}
	}
		
	public void zoom(float hFactor, float vFactor)
	{
		for(Axis a: fAxes)
		{
			if(a.isHorizontal())									
				a.getAxisRange().zoomViewValues(hFactor);
			else if(a.isVertical())
				a.getAxisRange().zoomViewValues(vFactor);
		}
	}
	
	public Axis getAxis(Side side)
	{
		switch(side)
		{
		case LEFT: return getLeftAxis();
		case RIGHT: return getRightAxis();
		case TOP: return getTopAxis();
		case BOTTOM: return getBottomAxis();
		}
		
		return null;
	}
	
	public Axis[] getAxes() { return fAxes; }
	
	public Axis getLeftAxis() { return fAxes[0]; }
	public Axis getRightAxis() { return fAxes[1]; }
	public Axis getTopAxis() { return fAxes[2]; }
	public Axis getBottomAxis() { return fAxes[3]; }

	public void setAutoHeight(boolean value)
	{
		this.fAutoHeight = value;
	}
	
	public boolean isAutoHeight()
	{
		return fAutoHeight;
	}

	public float getHeightInPercents()
	{
		return fHeightInPercents;
	}

	public void setHeightInPercents(float value)
	{
		fHeightInPercents = value;
	}

	public void calcXAutoValues()
	{
		for(SeriesBase s: fSeries)
		{
			if(!s.isVisible() || !s.hasPoints()) continue;
				
			Axis xAxis = this.getAxis(s.getXAxisSide());
			
			if(xAxis.getAxisRange().isAuto())
			{
				xAxis.getAxisRange().expandAutoValues(s.getLastScaleIndex()+1,s.getFirstScaleIndex()-1);
			}			
		}			
	}
	
	public void calcYAutoValues()
	{
		for(SeriesBase s: fSeries)
		{
			if(!s.isVisible() || !s.hasPoints()) continue;
			
			Axis xAxis = this.getAxis(s.getXAxisSide());
			Axis yAxis = this.getAxis(s.getYAxisSide());
			
			if(yAxis.getAxisRange().isAuto())
			{
				double maxMin[] = s.getMaxMinPrice(xAxis.getAxisRangeOrGlobalAxisRange().getMaxViewValueOrAutoValue(), xAxis.getAxisRangeOrGlobalAxisRange().getMinViewValueOrAutoValue());
				
				if(null != maxMin)
				{
					double m = yAxis.getAxisRange().getMargin(maxMin[0], maxMin[1]);
					yAxis.getAxisRange().expandAutoValues(maxMin[0]+m,maxMin[1]-m);
				}
			}				
		}
	}
	
	public void resetAutoValues()
	{
		for(Axis a: fAxes)
			a.getAxisRange().resetAutoValues();			
	}
	
	public void calcAutoValues()
	{			
		resetAutoValues();
		calcXAutoValues();
		calcYAutoValues();
	}
	
	public RectF getSideMargins()
	{
		RectF r = new RectF();
		
		r.left = getLeftAxis().getSize(0f);
		r.right = getRightAxis().getSize(0f);
		r.top = getTopAxis().getSize(0f);
		r.bottom = getBottomAxis().getSize(0f);
				
		if(fLegend.isVisible())
		{
			RectF size = fLegend.getSize();
			
			switch(fLegend.getSide())
			{
			case LEFT:
				r.left+=size.width();
				break;
			case RIGHT:
				r.right+=size.width();
				break;
			case BOTTOM:
				r.bottom+=size.height();
				break;
			case TOP:
				r.top+=size.height();
				break;
			
			}
		}
		
		return r;
	}
	

	private void drawEverything(Canvas c,CustomObjects customObjects)
	{		
		this.Paint().reset();
		
		if(fLegend.isVisible())
		{
			fLegend.drawSimple(c);
		}
		
		// top axis positioning
		if(getTopAxis().isVisible())
		{
			getTopAxis().drawSimple(c);
		}
	
		// left axis positioning
		if(getLeftAxis().isVisible())
		{
			getLeftAxis().drawSimple(c);
		}
		
		// right axis positioning
		if(getRightAxis().isVisible())
		{
			getRightAxis().drawSimple(c);
		}
		
		// bottom axis positioning
		if(getBottomAxis().isVisible())
		{
			getBottomAxis().drawSimple(c);
		}
		
		fPlot.draw(c,customObjects);	
	}

	@Override
	protected void innerDraw(Canvas c,CustomObjects customObjects) 
	{
		drawClear(c);
		drawEverything(c, customObjects);
	}
	
	@Override
	protected void onBoundsChanged()
	{
		final RectF margins = this.getSideMargins();
		margins.left = Math.max(fGlobalLeftMargin, margins.left);
		margins.right = Math.max(fGlobalRightMargin, margins.right);
		
		final RectF legendSize = this.fLegend.getSize();
		
		final float horizontalAxisWidth = getBounds().width() - (margins.left +  margins.right);
		final float verticalAxisHeight = getBounds().height() - (margins.top +  margins.bottom);
										
		getTopAxis().setBounds(margins.left,margins.top - getTopAxis().getSize(0f),horizontalAxisWidth,getTopAxis().getSize(0f)); 
		getBottomAxis().setBounds(margins.left,getBounds().height() - margins.bottom,horizontalAxisWidth,getBottomAxis().getSize(0f));
		
		getLeftAxis().setBounds(margins.left - getLeftAxis().getSize(0f),margins.top,getLeftAxis().getSize(0f),verticalAxisHeight);
		getRightAxis().setBounds(getBounds().width()-margins.right, margins.top, getRightAxis().getSize(0f), verticalAxisHeight);
				
		fPlot.setBounds(margins.left, margins.top, horizontalAxisWidth, verticalAxisHeight);
		
		switch(fLegend.getSide())
		{
		case LEFT:
			fLegend.setBounds(0, margins.top + (verticalAxisHeight-legendSize.height())/2f, legendSize.width(), legendSize.height());
			break;
		case RIGHT:
			fLegend.setBounds(getBounds().width() - legendSize.width(), margins.top + (verticalAxisHeight-legendSize.height())/2f,legendSize.width(), legendSize.height());
			break;
		case TOP:
			fLegend.setBounds(margins.left+(horizontalAxisWidth-legendSize.width())/2f, 0f,legendSize.width(), legendSize.height());
			break;
		case BOTTOM:
			fLegend.setBounds(margins.left+(horizontalAxisWidth-legendSize.width())/2f,getBounds().height() - legendSize.height(),legendSize.width(), legendSize.height());
			break;
		}
	}	
	
	private void drawClear(Canvas c)
	{
		PaintUtils.drawFullRect(c, this.Paint(), fAreaAppearance,c.getClipBounds());
	}
}
