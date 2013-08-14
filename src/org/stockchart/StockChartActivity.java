/**
 * Copyright 26.12.2012 Alex Vikulov (vikuloff@gmail.com)

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
package org.stockchart;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
/**
 * @author alexv
 *
 */
public abstract class StockChartActivity extends Activity 
{
	private StockChartView fStockChartView;
	
	private final String TAG = "StockChartActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
	
		fStockChartView = new StockChartView(this);
		
		if(null != savedInstanceState)
		{
			restoreChartFromState(savedInstanceState);    
		}
		else
		{
			initChart();
		}
		 
		 setContentView(fStockChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	public StockChartView getStockChartView()
	{
		return fStockChartView;
	}
	
	protected abstract void initChart();
	protected abstract void restoreChart();
	
	protected void onSaveInstanceState (Bundle outState)
	{
		try 
		{
			outState.putString(TAG, fStockChartView.save());
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void restoreChartFromState(Bundle state)
	{
		String s = state.getString(TAG);
		
		try 
		{
			fStockChartView.load(s);
			
			restoreChart();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
