/**
 * Copyright 04.04.2013 Alex Vikulov (vikuloff@gmail.com)

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

import org.stockchart.core.SeriesPaintInfo;
import org.stockchart.utils.PaintUtils;

import android.graphics.Canvas;
import android.graphics.PointF;

public class SpeedLinesSticker extends AbstractSticker 
{
	@Override
	protected void draw(SeriesPaintInfo info,PointF p1, PointF p2, Canvas c) 
	{
		this.fAppearance.applyOutline(fPaint);
		
		PointF rightMost = Utils.getRightmost(p1, p2);
		PointF leftMost = Utils.getLeftmost(p1, p2);
		
		boolean isRising = Utils.isRising(p1, p2);
		
		float maxY = Math.max(p1.y, p2.y);
		float minY = Math.min(p1.y, p2.y);
		
		float originY = isRising?minY:maxY;
		
		float height = maxY - minY;
		
		float r1 = originY + height* 0.33f * (isRising?1f:-1f);
		float r2 = originY + height* 0.667f * (isRising?1f:-1f);
		float r3 = originY;
		
		float rightMostX = Math.max(rightMost.x, c.getWidth() + 1f);
		
		PaintUtils.drawLine(c, leftMost.x,leftMost.y,rightMostX,Utils.getY(leftMost.x,leftMost.y,rightMost.x,r1,rightMostX),fPaint);
		PaintUtils.drawLine(c, leftMost.x,leftMost.y,rightMostX,Utils.getY(leftMost.x,leftMost.y,rightMost.x,r2,rightMostX),fPaint);
		PaintUtils.drawLine(c, leftMost.x,leftMost.y,rightMostX,Utils.getY(leftMost.x,leftMost.y,rightMost.x,r3,rightMostX),fPaint);
	}

}
