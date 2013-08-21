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

import java.text.DecimalFormat;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Appearance.OutlineStyle;
import org.stockchart.series.AbstractSeries;
import org.stockchart.series.SeriesBase;
import org.stockchart.utils.CustomObjects;
import org.stockchart.utils.DoubleUtils;
import org.stockchart.utils.GridPainter;
import org.stockchart.utils.GridPainter.GridLabelPosition;
import org.stockchart.utils.GridPainter.GridType;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Axis extends ChartElement implements GridPainter.IGridLabelsProvider
{
	public enum Side
	{
		LEFT,
		RIGHT,
		TOP,
		BOTTOM
	}		
	
	public interface ILabelFormatProvider
	{
		public String getAxisLabel(Axis sender,double value);
	}
	
	public interface IScaleValuesProvider
	{
		public Double[] getScaleValues(PaintInfo info, int valuesCount);
	}
	
	private String fDefaultLabelFormat = "#.#";
	
	private Side fSide;
	private float fSize;
	private boolean fIsVisible = true;
	
	private boolean fDrawLastValue = true;
	private boolean fDrawMaxMin = true;
	private int fLinesCount = 5;

	private Appearance fAppearance = new Appearance();
	
	private Paint fPaint = new Paint();
	
	private final AxisRange fAxisRange = new AxisRange();
	
	private AxisRange fGlobalAxisRange = null;
	
	private ILabelFormatProvider fLabelFormatProvider;
	private IScaleValuesProvider fScaleValuesProvider;
	private final Area fParent;
		
	private final PaintInfo fPaintInfo = new PaintInfo();
	private final Rect fTempRect = new Rect();
	
	private boolean fIsLogarithmic = false;
	
	private DecimalFormat fDecimalFormat = new DecimalFormat();
	
	Axis(Area parent, Side side)
	{
		super(parent);
		
		fParent = parent;
		fSide = side;
		fSize = isHorizontal()?20.0f:50.0f;
		fDrawLastValue = isVertical();	
		
		fAppearance.setOutlineColor(Color.GRAY);
		fAppearance.setOutlineWidth(1f);
		fAppearance.setOutlineStyle(OutlineStyle.DASH);
		fAppearance.setFillColors(Color.TRANSPARENT);
		
		Theme.fillAppearanceFromCurrentTheme(Axis.class, side.toString(), fAppearance);
	}
	
	public void setLogarithmic(boolean v)
	{
		fIsLogarithmic = v;
	}
	
	public boolean isLogarithmic()
	{
		return fIsLogarithmic;
	}
	
	public Appearance getAppearance()
	{
		return fAppearance;
	}
	
	public int getLinesCount()
	{
		return fLinesCount;
	}
	
	public void setLinesCount(int v)
	{
		fLinesCount = v;
	}
	
	public String getDefaultLabelFormat()
	{
		return fDefaultLabelFormat;
	}
	
	public void setDefaultLabelFormat(String fmt)
	{
		fDefaultLabelFormat = fmt;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{
		fSide = Side.valueOf(j.getString("side"));
		fSize = (float)j.getDouble("size");
		fIsVisible = j.getBoolean("visible");
		fDrawLastValue = j.getBoolean("drawLastValue");
		fLinesCount = j.getInt("linesCount");		
		fDrawMaxMin = j.getBoolean("drawMaxMin");
		fDefaultLabelFormat = j.getString("defaultLabelFormat");
		fIsLogarithmic = j.getBoolean("isLogarithmic");
		
		fAxisRange.fromJSONObject(j.getJSONObject("axisRange"));
		fAppearance.fromJSONObject(j.getJSONObject("appearance"));
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject r = new JSONObject();
		r.put("side", fSide);
		r.put("size", fSize);
		r.put("visible",fIsVisible);
		r.put("drawLastValue", fDrawLastValue);
		r.put("linesCount", fLinesCount);
		r.put("axisRange", fAxisRange.toJSONObject());
		r.put("appearance",fAppearance.toJSONObject());
		r.put("drawMaxMin", fDrawMaxMin);
		r.put("defaultLabelFormat", fDefaultLabelFormat);
		r.put("isLogarithmic", fIsLogarithmic);
		
		return r;
	}

	public static boolean isHorizontal(Axis.Side side)	
	{
		return side == Side.TOP || side == Side.BOTTOM;
	}
	
	public static boolean isVertical(Axis.Side side)
	{
		return side == Side.LEFT || side == Side.RIGHT;		
	}
	/**
	 * Sets whether maximum and minimum values should be drawn
	 * 
	 * */
	public void setDrawMaxMin(boolean v)
	{
		fDrawMaxMin = v;
	}

	public boolean getDrawMaxMin()
	{
		return fDrawMaxMin;
	}
	
	public void setDrawLastValue(boolean v)
	{
		fDrawLastValue = v;
	}
	
	public boolean getDrawLastValue()
	{
		return fDrawLastValue;
	}
	
	public Area getParent()
	{
		return fParent;
	}
	
	public void setScaleValuesProvider(IScaleValuesProvider svp)
	{
		fScaleValuesProvider = svp;
	}	
	
	public IScaleValuesProvider getScaleValuesProvider()
	{
		return fScaleValuesProvider;
	}
	
	public void setLabelFormatProvider(ILabelFormatProvider p)
	{		
		fLabelFormatProvider = p;
	}
	
	public ILabelFormatProvider getLabelFormatProvider()
	{
		return fLabelFormatProvider;
	}
	
	public void setGlobalAxisRange(AxisRange range)
	{
		fGlobalAxisRange = range;
	}
	
	public AxisRange getGlobalAxisRange()
	{
		return fGlobalAxisRange;
	}
	
	public AxisRange getAxisRange()
	{
		return fAxisRange;
	}
	
	public AxisRange getAxisRangeOrGlobalAxisRange()
	{
		return (fGlobalAxisRange == null)?fAxisRange:fGlobalAxisRange;
	}
	
	public Double[] getScaleValues(PaintInfo pinfo)
	{
		if(Double.compare(pinfo.Min, pinfo.Max) == 0) return null;
		
		if(null != this.getScaleValuesProvider())
			return this.getScaleValuesProvider().getScaleValues(pinfo,fLinesCount);
		
		double total = (pinfo.Max-pinfo.Min);
		double f = total/((double)fLinesCount+1);
							
		Double[] result = new Double[fLinesCount];
		
		double d = pinfo.Min + f;
		for(int k=0;k<result.length;k++)
		{
			result[k] = d;
			d+=f;
		}
		
		return result;
	}

	public float getSize(float sizeIfInvisible)
	{
		return isVisible()?getSize():sizeIfInvisible;
	}
	
	public float getSize()
	{
		return fSize;
	}

	public void setSize(float v)
	{
		fSize = v;
	}
	
	public boolean isVertical()
	{
		return isVertical(fSide);		
	}
	
	public boolean isHorizontal()
	{
		return isHorizontal(fSide);
	}
	public Side getSide()
	{
		return fSide;
	}
	public boolean isVisible() 
	{
		return fIsVisible;
	}
	
	public void setVisible(boolean value)
	{
		fIsVisible = value;
	}

	public String getLabel(double value) 
	{
		if(null != this.getLabelFormatProvider())
			return this.getLabelFormatProvider().getAxisLabel(this, value);
		
		fDecimalFormat.applyPattern(fDefaultLabelFormat);

		return fDecimalFormat.format(value);
	}
	
	public float getCoordinate(double value)
	{
		float size = isVertical()?height():width();
		boolean isX = isHorizontal();
		AxisRange range = getAxisRangeOrGlobalAxisRange();
		
		double max = range.getMaxViewValueOrAutoValue();
		double min = range.getMinViewValueOrAutoValue();
		double factor = size / range.getViewLength();
		
		return isX?getCoordinate(value,min,factor):getCoordinate(max,value,factor);
	}
	
	
	public double getValueByCoordinate(float coordinate,boolean isAbsolute)
	{
		if(isAbsolute)
		{
			RectF abs = this.getAbsoluteBounds();
			
			if(isHorizontal())
				coordinate-=abs.left;
			else
				coordinate-=abs.top;
		}
		
		double result = Double.NaN;
		AxisRange ar = this.getAxisRangeOrGlobalAxisRange();
		
		if(isHorizontal())
			result = ar.getMinViewValueOrAutoValue() + (coordinate / width())*ar.getViewLength();
		else
			result = ar.getMaxViewValueOrAutoValue() - (coordinate / height())*ar.getViewLength();
		
		return result;
	}
	
	private static float getCoordinate(double value, double min, double factor)
	{
		if(DoubleUtils.equals(value, min)) return 0.0f;
		
		return (float)((value - min) * factor);
	}
	
	private GridType getGridType()
	{
		return isHorizontal()?GridType.VERTICAL:GridType.HORIZONTAL;
	}

	@Override
	protected void innerDraw(Canvas c,CustomObjects customObjects) 
	{
		if(!isVisible()) return;
		
		fPaintInfo.loadFrom(this);

		c.getClipBounds(fTempRect);
		
		fAppearance.applyFill(fPaint, fTempRect);	
		c.drawRect(fTempRect, fPaint);
				
		GridLabelPosition pos = GridLabelPosition.NEAR;		
		
		switch(this.getSide())
		{
			case LEFT:
			{
				pos = GridLabelPosition.FAR;
			}
			break;
			case TOP:
			{
				pos = GridLabelPosition.FAR;
			}
			break;
			case RIGHT:
			{
				pos = GridLabelPosition.NEAR;
			}
			break;
		
			case BOTTOM:
			{
				pos = GridLabelPosition.NEAR;
			}
			break;			
		}
			
		Double[] values = this.getScaleValues(fPaintInfo);
		if(null != values)
			GridPainter.drawGrid(values, fTempRect, c, fAppearance, fPaint, fPaintInfo,getGridType(),pos,this,3.0f);
		
		if(this.fDrawMaxMin)
		{
			GridPainter.drawGridLineAt(fPaintInfo.Max, fTempRect, c, fAppearance, fPaint, fPaintInfo,getGridType(),pos,this,3.0f);
			GridPainter.drawGridLineAt(fPaintInfo.Min, fTempRect, c, fAppearance, fPaint, fPaintInfo,getGridType(),pos,this,3.0f);
		}
		
		if(this.fDrawLastValue)
		{		
			Iterator<SeriesBase> i = fParent.getSeries().iterator();
			while(i.hasNext())
			{
				AbstractSeries<?> ss = (AbstractSeries<?>)i.next();
				
				double lastValue = ss.getLastValue();
				
				if(!Double.isNaN(lastValue) && ss.getYAxisSide() == getSide())
				{
					GridPainter.drawGridLineAt(lastValue, fTempRect, c, ss.getAppearance(),fPaint, fPaintInfo,getGridType(),pos,this,3.0f);
				}		
			}			
		}
		
	}


}
