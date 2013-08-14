/**
 * Copyright 19.03.2013 Alex Vikulov (vikuloff@gmail.com)

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

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Axis.Side;
import org.stockchart.utils.PaintUtils;
import org.stockchart.utils.SizeF;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

public class Crosshair 
{
	public interface ILabelFormatProvider
	{
		public String getLabel(Crosshair sender,Plot plot, double xValue,double yValue);
	}

	private final Paint fPaint = new Paint();
	
	private final Appearance fAppearance = new Appearance();
	
	private boolean fIsAuto = false;
	
	private boolean fIsVisible = false;

	private boolean fDrawHorizontal = true;
	private boolean fDrawVertical = true;
	
	private float fX;
	private float fY;
	
	private Axis.Side fHorizontalAxis = Axis.Side.BOTTOM;
	private Axis.Side fVerticalAxis = Axis.Side.RIGHT;
	
	private ILabelFormatProvider fLabelFormatProvider;
	
	public Crosshair()
	{
		fAppearance.setOutlineWidth(3f);
		fAppearance.setOutlineColor(Color.RED);
	}
	
	public Appearance getAppearance()
	{
		return fAppearance;
	}
	
	public void setLabelFormatProvider(ILabelFormatProvider p)
	{		
		fLabelFormatProvider = p;
	}
	
	public ILabelFormatProvider getLabelFormatProvider()
	{
		return fLabelFormatProvider;
	}
	
	public Axis.Side getHorizontalAxis() {
		return fHorizontalAxis;
	}

	public void setHorizontalAxis(Axis.Side v) {
		this.fHorizontalAxis = v;
	}

	public Axis.Side getVerticalAxis() {
		return fVerticalAxis;
	}

	public void setVerticalAxis(Axis.Side v) {
		this.fVerticalAxis = v;
	}

	public void fromJSONObject(JSONObject j) throws JSONException
	{	
		fIsAuto = j.getBoolean("isAuto");
		fIsVisible = j.getBoolean("isVisible");
		fDrawHorizontal = j.getBoolean("drawHorizontal");
		fDrawVertical = j.getBoolean("drawVertical");
		fX = (float)j.getDouble("x");
		fY = (float)j.getDouble("y");
		fHorizontalAxis = Side.valueOf(j.getString("horizontalAxis"));
		fVerticalAxis = Side.valueOf(j.getString("verticalAxis"));
		fAppearance.fromJSONObject(j.getJSONObject("appearance"));
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject r = new JSONObject();
		
		r.put("drawHorizontal", fDrawHorizontal);
		r.put("drawVertical", fDrawVertical);
		r.put("isAuto", fIsAuto);
		r.put("isVisible", fIsVisible);
		r.put("x", fX);
		r.put("y",fY);
		r.put("horizontalAxis", fHorizontalAxis);
		r.put("verticalAxis", fVerticalAxis);
		r.put("appearance", fAppearance.toJSONObject());
		
		return r;
	}
	
	public boolean isVisible()
	{
		return fIsVisible;
	}
	
	public void setVisible(boolean v)
	{
		fIsVisible = v;
	}
	
	public float getXInPercents()
	{
		return fX;
	}
	
	public float getYInPercents()
	{
		return fY;
	}
	
	public void setPositionInPercents(float x,float y)
	{
		fX = x;
		fY = y;
	}
	
	public boolean isAuto() 
	{
		return fIsAuto;
	}
	
	public void setAuto(boolean v)
	{
		fIsAuto = v;
	}
	
	public void draw(Plot plot,Canvas c,Rect clipRect)
	{		
		float xxx = fX * c.getWidth();
		float yyy = fY * c.getHeight();
		
		PointF p = plot.getRelativePosition(xxx, yyy);
		
		this.fAppearance.applyOutline(fPaint);
		
		if(this.fDrawVertical)
			PaintUtils.drawLine(c, p.x,clipRect.top,p.x,clipRect.bottom,fPaint);
		
		if(this.fDrawHorizontal)
			PaintUtils.drawLine(c, clipRect.left,p.y,clipRect.right,p.y,fPaint);
		
		Area parent = (Area)plot.getParent();
		Axis hAxis = parent.getAxis(fHorizontalAxis);
		Axis vAxis = parent.getAxis(fVerticalAxis);
		
		double realXValue = hAxis.getValueByCoordinate(p.x, false);
		double realYValue = vAxis.getValueByCoordinate(p.y, false);

		String text = null;
	
		if(fLabelFormatProvider != null)
			text = fLabelFormatProvider.getLabel(this, plot, realXValue, realYValue);
		else
			text = String.format("X: %.2f, Y: %.2f", realXValue, realYValue);
		
		if(null != text)
		{
			fAppearance.applyText(fPaint);
			SizeF f = fAppearance.measureTextSize(text, fPaint, false);

			float ow = fAppearance.getOutlineWidth() + 1f;

			float textX = clipRect.right - p.x > p.x?p.x + ow:p.x - f.width - ow;
			float textY = clipRect.bottom - p.y > p.y?p.y + f.height + ow:p.y - f.height;

			if(!fDrawVertical)
				textX = 1f;

			if(!fDrawHorizontal)
				textY = f.height + 1f;

			c.drawText(text, textX, textY, fPaint);
		}
	}

	public boolean isDrawHorizontal() {
		return fDrawHorizontal;
	}

	public void setDrawHorizontal(boolean v) {
		this.fDrawHorizontal = v;
	}

	public boolean isDrawVertical() {
		return fDrawVertical;
	}

	public void setDrawVertical(boolean v) {
		this.fDrawVertical = v;
	}
}
