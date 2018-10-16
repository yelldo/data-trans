package com.transform.jdbc;

import java.io.Serializable;
import java.util.Calendar;


public class SelDateRange implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//选择的时间类型
	private String dateType;
	private Long startTime;
	private Long endTime;
	@Override
	public String toString() {
		return dateType;
	}
	public String getDateType() {
		return dateType;
	}
	/**
	 * 设置日期
	 * null或空串表示置空
	 * d1 当天数据
	 * d2 昨天
	 * d3 前天
	 * a3 近三天
	 * w1 本周
	 * m1 本月
	 * @param dateType
	 */
	public void setDateType(String dateType) {		
		if(dateType == null || dateType.trim().equals("")){
			startTime = null;
			endTime = null;
			this.dateType = null;
			return;
		}
		this.dateType = dateType;
		long todaystart = System.currentTimeMillis();
		long num = todaystart % (3600*1000*24);
		todaystart = todaystart - num - 8*3600*1000;
		long perday = 24*3600*1000;
		if(dateType.toLowerCase().equals("d1")){
			startTime = todaystart;
			endTime = todaystart + perday;
			return;
		}
		if(dateType.toLowerCase().equals("d2")){
			startTime = todaystart - perday;
			endTime = todaystart;
			return;
		}
		if(dateType.toLowerCase().equals("d3")){
			startTime = todaystart - 2*perday;
			endTime = todaystart - 1*perday;
			return;
		}
		if(dateType.toLowerCase().equals("a3")){
			startTime = todaystart - 2*perday;
			endTime = todaystart + perday;
			return;
		}
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(todaystart);
		if(dateType.toLowerCase().equals("w1")){
			int week = ca.get(Calendar.DAY_OF_WEEK);
			week = week - 2;			
			startTime = todaystart - week*perday;
			endTime = todaystart + perday;
			return;
		}
		if(dateType.toLowerCase().equals("m1")){
			int day = ca.get(Calendar.DAY_OF_MONTH);
			day--;			
			startTime = todaystart - day*perday;
			endTime = todaystart + perday;
			return;
		}
		if(dateType.indexOf("/") >= 0){
			String[] strs = dateType.split("\\/");			
			if(strs.length == 2){
				if(strs[0] != null && !strs[0].trim().equals("")){
					startTime = java.sql.Date.valueOf(strs[0].trim()).getTime();
				}else{
					startTime = null;
				}
				if(strs[1] != null && !strs[1].trim().equals("")){
					endTime = java.sql.Date.valueOf(strs[1].trim()).getTime();
				}else{
					endTime = null;
				}
			}else{
				startTime = null;
				endTime = null;
			}			
			return;
		}
		throw new RuntimeException("不支持的日期范围" + dateType  + "!");		
	}
	public Long getEndTime() {
		return endTime;
	}
	public Long getStartTime() {
		return startTime;
	}
}
