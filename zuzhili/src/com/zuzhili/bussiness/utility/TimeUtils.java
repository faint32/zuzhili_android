package com.zuzhili.bussiness.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

	/**
	 * 返回时间格式：2011-12-28 19:28:11
	 */
	public static String getTime(long time){
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	/**
	 * 返回时间格式：12-28 19:28
	 */
	public static String getTimeMinute(long time){
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(date);
	}
	
	public static String getTimeMinute(String time){
		Date date = new Date(Long.valueOf(time));
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(date);
	}
	/**
	 * 返回时间格式：2011-12-28
	 */
	public static String getTimeDate(long time){
		if(time == 0) {
			return "无";
		}
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	/**
	 * 返回时间格式：12-28 19:28
	 */
	public static String getTimeForCZSelling(String temp_time){
		long time = Long.parseLong(temp_time);
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(date);
	}
	/**
	 * 返回时间格式：12-28 19:28
	 */
	public static String getTimeForCZSelling(long time){
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(date);
	}
	
	/**
	 * 返回时间格式：12-28
	 */
	public static String getTimeForCZStopSelling(String temp_time){
		long time = Long.parseLong(temp_time);
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		return sdf.format(date);
	}
	
	/**
	 * 返回时间格式：201203281122
	 */
	public static String getTimeAsNumber(long time){
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		return sdf.format(date);
	}
	/**
	 * 返回时间格式：20120328
	 */
	public static String getTimeAsNumber(String temp_time){
		long time = Long.parseLong(temp_time);
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}
	/**
	 * 返回时间格式：20111228
	 */
	public static String getTimeDate1(long time){
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}

}
