/**
 * Copyright 22.03.2013 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart.stickers;

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Appearance;
import org.stockchart.core.Axis;
import org.stockchart.core.Axis.Side;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.utils.PaintUtils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class AbstractSticker 
{
	public static float RADIUS = 10f;
	
	protected final Paint fPaint = new Paint();
	protected final Appearance fAppearance = new Appearance();
	private final Appearance fGuideLineAppearance = new Appearance();
	
	protected double fX1, fY1, fX2, fY2;
	
	private Axis.Side fHorizontalAxis = Axis.Side.BOTTOM;
	private Axis.Side fVerticalAxis = Axis.Side.RIGHT;
	private PointF fTmpP1 = new PointF();
	private PointF fTmpP2 = new PointF();
	
	public AbstractSticker()
	{
		reset();
		
		fGuideLineAppearance.setPrimaryFillColor(0x40FF00FF);
		
		fAppearance.setOutlineWidth(2f);
		fAppearance.setOutlineColor(Color.BLUE);
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{	
		fX1 = j.getDouble("x1");
		fY1 = j.getDouble("y1");
		fX2 = j.getDouble("x2");
		fY2 = j.getDouble("y2");

		fHorizontalAxis = Side.valueOf(j.getString("horizontalAxis"));
		fVerticalAxis = Side.valueOf(j.getString("verticalAxis"));
		
		fAppearance.fromJSONObject(j.getJSONObject("appearance"));
		fGuideLineAppearance.fromJSONObject(j.getJSONObject("guideLineAppearance"));
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject r = new JSONObject();

		r.put("horizontalAxis", fHorizontalAxis);
		r.put("verticalAxis", fVerticalAxis);
		
		r.put("x1", fX1);
		r.put("y1", fY1);
		r.put("x2", fX2);
		r.put("y2", fY2);
		
		r.put("appearance", fAppearance.toJSONObject());
		r.put("guideLineAppearance", fGuideLineAppearance.toJSONObject());
		
		return r;
	}
	
	
	public Appearance getAppearance()
	{
		return fAppearance;
	}
	
	public Appearance getGuideLineAppearance()
	{
		return this.fGuideLineAppearance;
	}
	
	public boolean isValid()
	{
		return !Double.isNaN(fX1) &&
			   !Double.isNaN(fY1) &&
			   !Double.isNaN(fX2) &&
			   !Double.isNaN(fY2);
	}
	
	public void reset()
	{
		fX1 = Double.NaN;
		fX2 = Double.NaN;
		fY1 = Double.NaN;
		fY2 = Double.NaN;
	}
	
	public double getMidX()
	{
		return (fX1 + fX2) * 0.5;
	}
	
	public double getMidY()
	{
		return (fY1 + fY2) * 0.5;
	}
	
	public double getX1()
	{
		return fX1;
	}
	
	public double getX2()
	{
		return fX2;
	}
	
	public double getY1()
	{
		return fY1;
	}
	
	public double getY2()
	{
		return fY2;
	}
	
	public void setFirstPoint(double x, double y)
	{
		fX1 = x;
		fY1 = y;
	}
	
	public void setSecondPoint(double x,double y)
	{
		fX2 = x;
		fY2 = y;
	}
	
	public void draw(SeriesPaintInfo info, Canvas c)
	{
		fTmpP1.x = info.getX(fX1);
		fTmpP1.y = info.getY(fY1);
		fTmpP2.x = info.getX(fX2);
		fTmpP2.y = info.getY(fY2);
		
		draw(info, fTmpP1, fTmpP2, c);
		drawGuideLine(fTmpP1,fTmpP2, c);
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


	protected abstract void draw(SeriesPaintInfo info, PointF p1, PointF p2, Canvas c);
	
	private void drawGuideLine(PointF p1,PointF p2,Canvas c)
	{
		PaintUtils.drawCircle(p1.x, p1.y, RADIUS,c, fPaint, fGuideLineAppearance);
		PaintUtils.drawCircle(p2.x, p2.y, RADIUS,c, fPaint, fGuideLineAppearance);
		
		float midX = (p1.x + p2.x)/2f;
		float midY = (p1.y + p2.y)/2f;
	
		PaintUtils.drawSquare(midX, midY, RADIUS,c, fPaint, fGuideLineAppearance);
		
		fGuideLineAppearance.applyOutline(fPaint);
		
		PaintUtils.drawLine(c, midX - RADIUS, midY, midX + RADIUS, midY, fPaint);
		PaintUtils.drawLine(c, midX, midY - RADIUS, midX, midY + RADIUS, fPaint);
		
		
		
		PaintUtils.drawLine(c, p1.x, p1.y, p2.x, p2.y, fPaint);
	}
}
