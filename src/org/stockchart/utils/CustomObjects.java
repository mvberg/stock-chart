/**
 * Copyright 19.03.2013 Alex Vikulov (vikuloff@gmail.com)

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

import java.util.TreeMap;

public class CustomObjects extends TreeMap<Integer, Object>
{
	private static final long serialVersionUID = -2041424557382695883L;
	
	private static int ID = 0xDABBAD00;
	
	private static int newID()
	{
		return ++ID;
	}
	
	public static final int CROSSHAIR = newID();
	public static final int STICKER = newID();
}
