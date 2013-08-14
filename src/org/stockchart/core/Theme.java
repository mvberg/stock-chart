/**
 * Copyright 24.07.2013 Alex Vikulov (vikuloff@gmail.com)

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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.stockchart.utils.IOUtils;

import android.content.Context;

public class Theme 
{	
	private static Theme fCurrentTheme;
		
	private final TreeMap<String,Appearance> fMap =  new TreeMap<String,Appearance>();
	
	public static void setDefaultTheme()
	{
		fCurrentTheme = null;
	}
	
	public static Theme getCurrentTheme()
	{
		return fCurrentTheme;
	}
	
	public static void setCurrentThemeFromResources(Context c,int resId) throws IOException, JSONException
	{
		String text = IOUtils.readText(c, resId);
		
		JSONObject obj = new JSONObject(text);
		
		Theme theme = new Theme();
		theme.fromJSONObject(obj);
		
		fCurrentTheme = theme;
	}
	
	
	public static void fillAppearanceFromCurrentTheme(Class<?> c,Appearance dst)
	{
		fillAppearanceFromCurrentTheme(c,null,dst);
	}
	
	public static void fillAppearanceFromCurrentTheme(Class<?> c,String tag,Appearance dst)
	{
		Theme t = getCurrentTheme();
		
		if(null != t)
			t.fillAppearance(c,tag, dst);
	}
	
	public void fillAppearance(Class<?> c,String tag,Appearance dst)
	{
		Appearance src = null;
		if(null != tag)
			src = fMap.get(getKey(c,tag));
		
		if(src == null)
			src =  fMap.get(getKey(c,null));
				
		if(null != src)
		{
			dst.fill(src);
		}
	}	
	
	private static String getKey(Class<?> c,String tag)
	{
		String key = c.getSimpleName();
		if(tag != null)
			key += "#"+tag;
		
		return key;
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject obj = new JSONObject();		
		
		Iterator<Entry<String, Appearance>> i = this.fMap.entrySet().iterator();
		
		while(i.hasNext())
		{
			Entry<String, Appearance> e = i.next();
			obj.put(e.getKey(), e.getValue().toJSONObject());
		}
		
		return obj;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{	
		fMap.clear();
		
		Iterator<?> i = j.keys();
		
		while(i.hasNext())
		{
			String key = (String)i.next();
			JSONObject jsonAppearance = j.getJSONObject(key);
			
			Appearance a = new Appearance();
			a.fromJSONObject(jsonAppearance);
			
			fMap.put(key, a);
		}
	}
}

