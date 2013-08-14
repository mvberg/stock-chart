/**
 * Copyright 01.08.2012 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart.utils;

import android.graphics.Color;

/**
 * @author alexv
 *
 */
public class ColorUtils 
{
	public static int darker(int c)
	{		
		int r = Color.red(c);
        int b = Color.blue(c);
        int g = Color.green(c);

        return Color.rgb((int)(r*.7), (int)(g*.7), (int)(b*.7));
	}
	
	public static int lighten(int c,float amount)
	{	
		int r = Color.red(c);
        int b = Color.blue(c);
        int g = Color.green(c);
        
		int red = (int) ((r * (1 - amount) / 255 + amount) * 255);
		int green = (int) ((g * (1 - amount) / 255 + amount) * 255);
		int blue = (int) ((b * (1 - amount) / 255 + amount) * 255);
		    
	    return(Color.rgb(red, green, blue));
	}
}
