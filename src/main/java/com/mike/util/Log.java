package com.mike.util;

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */


public class Log
{
	static private LogImp _d = new LogImp() {
		@Override
		public void d(String tag, String msg) {
			System.out.println(String.format("%30s  %s", tag, msg));
		}
	};

	static private LogImp _e = new LogImp() {
		@Override
		public void d(String tag, String msg) {
			System.out.println(String.format("%30s  ERROR %s", tag, msg));
		}
	};

	static public void set_d(LogImp x) {
		_d = x;
	}
	static public void set_e(LogImp x) {
		_e = x;
	}

	static public void d (String tag, String msg)
	{
		_d.d(tag, msg);
	}

	static public void e (String tag, String msg)
	{
		_e.d(tag, msg);
	}
}
