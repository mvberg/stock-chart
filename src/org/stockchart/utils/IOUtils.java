/**
 * Copyright 25.07.2013 Alex Vikulov (vikuloff@gmail.com)

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

/**
 * @author alexv
 *
 */
public class IOUtils 
{
	public static String readText(Context c,int resId) throws IOException
	{
		InputStream is = null;
		BufferedReader br = null;
    	try
    	{

    		is = c.getResources().openRawResource(resId);
    		
    		br = new BufferedReader(new InputStreamReader(is));
    		StringBuilder result = new StringBuilder();
    		String line;
    		while ((line = br.readLine()) != null) 
    		{
    			result.append(line);
    		}

    		return result.toString();
    	}
    	finally
    	{
    		if(null != is)
    		{
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}

	}
}
