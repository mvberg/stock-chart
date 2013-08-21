/**
 * Copyright 07.09.2012 Alex Vikulov (vikuloff@gmail.com)

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
import org.stockchart.utils.SizeF;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;

/**
 * @author alexv
 *
 */
public class Appearance 
{
	public enum Gradient
	{
		NONE,
		LINEAR_HORIZONTAL,
		LINEAR_VERTICAL,
	}
	
	public enum OutlineStyle
	{
		SOLID,
		DASH
	}
	
	public enum FontStyle
	{
		BOLD,
		ITALIC,
		BOLD_ITALIC,
		NORMAL
	}
	
	public class Font
	{
		private boolean fIsFromFile;
		private String fFamilyName;
		private String fFileName;
		private FontStyle fFontStyle = FontStyle.NORMAL;

		private Typeface fTypeface;

		private int fColor = Color.BLACK;
		private float fSize = 12f;
		
		public Font()
		{
			fromFamilyName(fFamilyName,fFontStyle);			
		}
		
		public int getColor()
		{
			return fColor;
		}
		
		public float getSize()
		{
			return fSize;
		}
		
		public void setColor(int c)
		{
			fColor = c;
		}
		
		public void setSize(float sz)
		{
			fSize = sz;
		}
		public Typeface getTypeface()
		{
			return fTypeface;
		}
			
		public String getFamilyName() 
		{
			return fFamilyName;
		}

		public String getFileName() 
		{
			return fFileName;
		}

		public FontStyle getFontStyle() 
		{
			return fFontStyle;
		}

		public void fromFile(String fileName)
		{
			fTypeface = Typeface.createFromFile(fileName);
			fFileName = fileName;
			fIsFromFile = true;				
		}
		
		public void fromFamilyName(String familyName,FontStyle fs)
		{
			fTypeface = Typeface.create(familyName,fontStyleToTypefaceConstant(fs));
			fFamilyName = familyName;
			fFontStyle = fs;
			fIsFromFile = false;
		}
		
		public void fill(Font f)
		{
			fIsFromFile = f.fIsFromFile;
			fFamilyName = f.fFamilyName;
			fFileName = f.fFileName;
			fFontStyle = f.fFontStyle;
			fColor = f.fColor;
			fSize = f.fSize;
			
			reloadTypeface();
		}
		
		public JSONObject toJSONObject() throws JSONException
		{
			JSONObject obj = new JSONObject();
			obj.put("isFromFile", fIsFromFile);
			obj.put("familyName", fFamilyName);
			obj.put("fileName", fFileName);
			obj.put("fontStyle", fFontStyle);
			obj.put("color", fColor);
			obj.put("size", fSize);
			return obj;
		}
		
		public void fromJSONObject(JSONObject j) throws JSONException
		{			
			fIsFromFile = j.getBoolean("isFromFile");
			fFamilyName = j.has("familyName")?j.getString("familyName"):null;
			fFileName = j.has("fileName")?j.getString("fileName"):null;
			fFontStyle = FontStyle.valueOf(j.getString("fontStyle"));
			fColor = j.getInt("color");
			fSize = (float)j.getDouble("size");
			
			reloadTypeface();
		}
		
		private void reloadTypeface()
		{
			if(fIsFromFile)
				fromFile(fFamilyName);
			else 
				fromFamilyName(fFamilyName,fFontStyle);
		}

	}
	
	private int fPrimaryFillColor = Color.WHITE;
	private int fSecondaryFillColor = Color.WHITE;
	
	private Gradient fGradient = Gradient.NONE;
	private float fOutlineWidth = 1f;
	private int fOutlineColor = Color.BLACK;
	private OutlineStyle fOutlineStyle = OutlineStyle.SOLID;
	private boolean fIsAntialias = true;

	private final Font fFont = new Font();
	
	private static final DashPathEffect DASH_EFFECT = new DashPathEffect(new float[] {5,3},0);
	
	private static final Rect fTempRect = new Rect();
	
	public void fill(Appearance a)
	{
		if(null == a) return;
		
		fPrimaryFillColor = a.fPrimaryFillColor;
		fSecondaryFillColor = a.fSecondaryFillColor;
		fGradient = a.fGradient;
		fOutlineWidth = a.fOutlineWidth;
		fOutlineColor =a.fOutlineColor;
		fOutlineStyle = a.fOutlineStyle;
		fIsAntialias = a.fIsAntialias;	
		fFont.fill(a.fFont);
	}
	
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject obj = new JSONObject();
		obj.put("primaryFillColor", fPrimaryFillColor);		
		obj.put("secondaryFillColor", fSecondaryFillColor);
		obj.put("gradient", fGradient);
		obj.put("outlineWidth", fOutlineWidth);
		obj.put("outlineColor", fOutlineColor);
		obj.put("outlineStyle", fOutlineStyle);
		obj.put("isAntiAlias", fIsAntialias);
		obj.put("font", fFont.toJSONObject());
		
		return obj;
	}
	
	public void fromJSONObject(JSONObject j) throws JSONException
	{				
		fPrimaryFillColor = j.getInt("primaryFillColor");
		fSecondaryFillColor = j.getInt("secondaryFillColor");
		fGradient = Gradient.valueOf(j.getString("gradient"));
		fOutlineWidth = (float)j.getDouble("outlineWidth");
		fOutlineColor = j.getInt("outlineColor");
		fOutlineStyle = OutlineStyle.valueOf(j.getString("outlineStyle"));
		fIsAntialias = j.getBoolean("isAntiAlias");
		
		fFont.fromJSONObject(j.getJSONObject("font"));
	}
	
	public Font getFont()
	{
		return fFont;
	}	
	
	public int getPrimaryFillColor() {
		return fPrimaryFillColor;
	}
	public void setPrimaryFillColor(int v) {
		this.fPrimaryFillColor = v;
	}
	public int getSecondaryFillColor() {
		return fSecondaryFillColor;
	}
	public void setSecondaryFillColor(int v) {
		this.fSecondaryFillColor = v;
	}
	public Gradient getGradient() {
		return fGradient;
	}
	public void setGradient(Gradient v) {
		this.fGradient = v;
	}
	public float getOutlineWidth() {
		return fOutlineWidth;
	}
	public void setOutlineWidth(float v) {
		this.fOutlineWidth = v;
	}
	public int getOutlineColor() {
		return fOutlineColor;
	}
	public void setOutlineColor(int v) {
		this.fOutlineColor = v;
	}
	public OutlineStyle getOutlineStyle() {
		return fOutlineStyle;
	}
	public void setOutlineStyle(OutlineStyle v) {
		this.fOutlineStyle = v;
	}
	public void setAntiAlias(boolean b)
	{
		this.fIsAntialias = b;
	}
	
	public boolean isAntiAlias()
	{
		return this.fIsAntialias;
	}
	
	public void setFillColors(int color)
	{
		fPrimaryFillColor = color;
		fSecondaryFillColor = color;	
	}
	
	public void setAllColors(int color)
	{
		fPrimaryFillColor = color;
		fSecondaryFillColor = color;
		fOutlineColor = color;
		
		fFont.setColor(color);
	}
	
		
	public SizeF measureTextSize(String text,Paint p,boolean applyText)
	{
		if(applyText)
			this.applyText(p);
		
		p.getTextBounds(text, 0, text.length(),fTempRect);
		
		return new SizeF(fTempRect.width(), fTempRect.height());
	}
	
	public void applyText(Paint p)
	{
		p.reset();
		p.setTextSize(fFont.getSize());
		p.setColor(fFont.getColor());
		p.setTypeface(fFont.getTypeface());
		p.setAntiAlias(fIsAntialias);
		p.setPathEffect(null);
		p.setStyle(Style.FILL);
	}
	
	public void applyOutline(Paint p)
	{
		p.reset();
		p.setShader(null);
		p.setColor(fOutlineColor);
		p.setAntiAlias(fIsAntialias);
		p.setStrokeWidth(fOutlineWidth);
		p.setStyle(Style.STROKE);

		switch(fOutlineStyle)
		{
		case DASH:
			p.setPathEffect(DASH_EFFECT);
			break;
		default:
			p.setPathEffect(null);
		}
	}
	
	public void applyFill(Paint p,Rect rect)
	{
		this.applyFill(p, new RectF(rect));
	}
	
	public void applyFill(Paint p,RectF rect)
	{
		p.reset();
		p.setStyle(Style.FILL);
		p.setColor(fPrimaryFillColor);
		
		switch(fGradient)
		{
		case LINEAR_HORIZONTAL:
			{
				p.setShader(new LinearGradient(rect.left,rect.top,rect.right,rect.top,fPrimaryFillColor,fSecondaryFillColor,TileMode.MIRROR));			
			}
			break;
			
		case LINEAR_VERTICAL:
			{
				p.setShader(new LinearGradient(rect.left,rect.top,rect.left,rect.bottom,fPrimaryFillColor,fSecondaryFillColor,TileMode.MIRROR));			
			}
			break;
		default:
			p.setShader(null);
		}
	}
	
	
	static int fontStyleToTypefaceConstant(FontStyle fs)
	{
		switch(fs)
		{
		case BOLD: return Typeface.BOLD;
		case ITALIC: return Typeface.ITALIC;
		case BOLD_ITALIC: return Typeface.BOLD_ITALIC;
		case NORMAL: return Typeface.NORMAL;
		}
		
		return -1;
	}
}
