/**
 * Copyright 03.08.2012 Alex Vikulov (vikuloff@gmail.com)

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
import org.stockchart.utils.GridPainter;
import org.stockchart.utils.GridPainter.GridType;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author alexv
 *
 */
public class Line 
{	
	private final Paint fPaint = new Paint();
	
	private final Appearance fAppearance = new Appearance();
	
	//public int OutlineColor = Color.BLACK;
	private double fValue = Double.NaN;
	//public float OutlineWidth = 2f;
	private Axis.Side fAxisSide = Axis.Side.LEFT;
	
	public double getValue() {
		return fValue;
	}

	public void setValue(double v) {
		this.fValue = v;
	}

	public Axis.Side getAxisSide() {
		return fAxisSide;
	}

	public void setAxisSide(Axis.Side v) {
		this.fAxisSide = v;
	}

	public Line() {}
	
	public Line(double value, Axis.Side side)
	{
		fValue = value;
		fAxisSide = side;
		
		Theme.fillAppearanceFromCurrentTheme(Line.class, fAppearance);
	}
	
	public Appearance getAppearance()
	{
		return fAppearance;
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject j = new JSONObject();
		j.put("value", fValue);
		j.put("axisSide", fAxisSide);
		j.put("appearance", fAppearance.toJSONObject());
		
		return j;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{
		fValue = j.getDouble("value");
		fAxisSide = Axis.Side.valueOf(j.getString("axisSide"));
		fAppearance.fromJSONObject(j.getJSONObject("appearance"));
	}
	
	public void draw(Rect clipRect, Canvas c,PaintInfo pinfo)
	{		
		GridType gt = Axis.isVertical(fAxisSide)?GridType.HORIZONTAL:GridType.VERTICAL;
		
		GridPainter.drawGridLineAt(fValue, clipRect, c, fAppearance, fPaint, pinfo,gt);
	}
	
}
