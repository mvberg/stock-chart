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

package org.stockchart.series;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.core.Appearance;
import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.points.AbstractPoint;

import android.graphics.Canvas;
import android.graphics.Color;

public abstract class AbstractSeries<T extends AbstractPoint> extends SeriesBase
{
	private final ArrayList<T> fPoints = new ArrayList<T>();
	
	private final Appearance fAppearance = new Appearance();
	
	public AbstractSeries()
	{
		fAppearance.getFont().setColor(Color.BLUE);
	}
	
	public ArrayList<T> getPoints()
	{
		return fPoints;
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject j = super.toJSONObject();
		j.put("appearance", fAppearance.toJSONObject());
		
		return j;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{
		super.fromJSONObject(j);
		fAppearance.fromJSONObject(j.getJSONObject("appearance"));
	}
	
	public Appearance getAppearance()
	{
		return fAppearance;
	}
	
	@Override
	public AbstractPoint getPointAt(int i)
	{
		return (AbstractPoint)fPoints.get(i);
	}
	
	@Override
	public void draw(Canvas c, SeriesPaintInfo pinfo)
	{			
		if(!isVisibleOnScreen(pinfo.X.Max, pinfo.X.Min)) return;
		
		int index = convertToArrayIndexZeroBased(pinfo.X.Min);

		preDraw();
		
		for(int i=index;i<getPointCount();i++)
		{
			T p = this.fPoints.get(i);			
			
			float scaleIndex = convertToScaleIndex(i);
			
			if(p.isVisible())
			{							
				float x1 = pinfo.getX(scaleIndex - 0.5f) + 1f;
				float x2 = pinfo.getX(scaleIndex + 0.5f) - 1f;

				drawPoint(c, pinfo, x1, x2, p);
			}
			
			if(scaleIndex > pinfo.X.Max)
				break;
		}
		
		postDraw(c);
	}
		
	@Override
	public int getPointCount()
	{
		return fPoints.size();
	}
	

	protected abstract void drawPoint(Canvas c, SeriesPaintInfo pinfo, float x1,float x2, T p);

	protected void preDraw() {}
	protected void postDraw(Canvas c){}

}
