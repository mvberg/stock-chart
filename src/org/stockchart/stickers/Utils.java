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

import android.graphics.PointF;

public class Utils 
{

	static PointF getRightmost(PointF p1,PointF p2)
	{
		return (p1.x > p2.x?p1:p2);
	}
	
	static PointF getLeftmost(PointF p1,PointF p2)
	{
		return (p1.x < p2.x?p1:p2);
	}
	
	static boolean isRising(PointF p1, PointF p2)
	{
		return getRightmost(p1,p2).y < getLeftmost(p1,p2).y;
	}
	
	static float getY(float x1,float y1,float x2,float y2,float x)
	{
		return (-(x1*y2 - x2*y1) - (y1-y2)*x)/(x2 - x1);
	}
}
