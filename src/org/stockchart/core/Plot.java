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

import org.stockchart.StockChartView.StickerInfo;
import org.stockchart.series.SeriesBase;
import org.stockchart.stickers.AbstractSticker;
import org.stockchart.utils.CustomObjects;
import org.stockchart.utils.GridPainter;
import org.stockchart.utils.PaintUtils;
import org.stockchart.utils.GridPainter.GridType;

import android.graphics.Canvas;
import android.graphics.Paint;


public class Plot extends ChartElement
{	
	private final SeriesPaintInfo fSeriesPaintInfo = new SeriesPaintInfo();

	private final Appearance fPlotAppearance = new Appearance();
	
	private final PaintInfo fPaintInfo = new PaintInfo();
	
	Plot(Area parent)
	{
		super(parent);
		
		Theme.fillAppearanceFromCurrentTheme(Plot.class, fPlotAppearance);
	}
	
	public Area getArea()
	{
		return (Area)this.getParent();
	}
	
	public Appearance getAppearance()
	{
		return fPlotAppearance;
	}
	
	@Override
	protected void innerDraw(Canvas c,CustomObjects customObjects) 
	{
		c.getClipBounds(this.TempRect());
		
		fPlotAppearance.applyFill(this.Paint(), this.TempRect());
		
		c.drawRect(this.TempRect(), this.Paint());
		
		if(getArea().isVerticalGridVisible())
		{					
			Axis a = getArea().getVerticalGridAxis();
			fPaintInfo.loadFrom(a);
			
			Double[] values = a.getScaleValues(fPaintInfo);
			if(null != values)
				GridPainter.drawGrid(values, this.TempRect(), c, a.getAppearance(), this.Paint(), fPaintInfo, GridType.VERTICAL);
		}
		
		if(getArea().isHorizontalGridVisible())
		{
			Axis a = getArea().getHorizontalGridAxis();
			fPaintInfo.loadFrom(a);
			
			Double[] values = a.getScaleValues(fPaintInfo);
			
			if(null != values)
				GridPainter.drawGrid(values, this.TempRect(), c, a.getAppearance(), this.Paint(), fPaintInfo, GridType.HORIZONTAL);
		}

		this.fSeriesPaintInfo.reset();
		for(SeriesBase s: this.getArea().getSeries())
		{				
			fSeriesPaintInfo.loadFrom(this.getArea().getAxis(s.getXAxisSide()), this.getArea().getAxis(s.getYAxisSide()));

			c.save();
			s.draw(c, fSeriesPaintInfo);
			c.restore();							
		}
		
		for(Line l: this.getArea().getLines())
		{
			Axis a = this.getArea().getAxis(l.getAxisSide());
			fPaintInfo.loadFrom(a);
			l.draw(this.TempRect(), c, fPaintInfo);
		}
		
		fSeriesPaintInfo.reset();
		for(AbstractSticker s: this.getArea().getStickers())
		{
			fSeriesPaintInfo.loadFrom(this.getArea().getAxis(s.getHorizontalAxis()),
									  this.getArea().getAxis(s.getVerticalAxis()));
			
			s.draw(fSeriesPaintInfo, c);
		}
		
		
		StickerInfo sticker = (StickerInfo)customObjects.get(CustomObjects.STICKER);
		
		if(null != sticker && sticker.area == (Area)this.getParent())
		{
			fSeriesPaintInfo.loadFrom(this.getArea().getAxis(sticker.sticker.getHorizontalAxis()),
									  this.getArea().getAxis(sticker.sticker.getVerticalAxis()));
			
			sticker.sticker.draw(fSeriesPaintInfo, c);
		}
		
		Crosshair ch = (Crosshair)customObjects.get(CustomObjects.CROSSHAIR);
		
		if(null != ch && ch.isVisible())
		{
			c.getClipBounds(this.TempRect());
			ch.draw(this, c, this.TempRect());
		}
		
		fPlotAppearance.applyText(this.Paint());
		
		String title = this.getArea().getTitle();
		if(null != title)
		{
			this.Paint().getTextBounds(title, 0, title.length(),this.TempRect());
			this.Paint().setTextAlign(Paint.Align.LEFT);
			c.drawText(title, 2, this.TempRect().height()+1,this.Paint());
		}
		
		
		// draw border
		c.getClipBounds(this.TempRect());
		PaintUtils.drawBorder(c, this.Paint(), fPlotAppearance,this.TempRect());
	}
	
}