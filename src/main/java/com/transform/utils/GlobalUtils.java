package com.transform.utils;

import com.alibaba.fastjson.JSONObject;
import com.transform.exception.TsException;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalUtils {
	/**
	 * 当前线程暂停
	 * @param millis
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			throw new TsException(e);
		}
	}
	private static InetAddress addr;
	/**
	 * 获取本机的IP信息
	 * @return
	 */
	public static InetAddress getHost() {
		if(addr != null) {
			return addr;
		}
		try {
			addr = InetAddress.getLocalHost();
			return addr;
		} catch (final UnknownHostException e) {
			throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!");
		}
	}

	/**
	 * 格式化字段串 示例：format("Hello %s", user.getName());
	 * 
	 * @param format
	 * @param objects
	 * @return
	 */
	public static String format(String format, Object... objects) {
		if(format == null || format.isEmpty())
			return "";
		return String.format(format, objects);
	}

	/**
	 * 检查对象是否为空或空串, 为空时将抛出异常
	 * 
	 * @param obj
	 * @param msg
	 * @return
	 */
	public static <T> T checkNotEmpty(T obj, String msg) {
		if (obj == null)
			throw new TsException(msg == null ? "" : msg);
		if ((obj instanceof String) && ((String) obj).trim().isEmpty())
			throw new TsException(msg == null ? "" : msg);
		return obj;
	}
	/**
	 * 批量检查参数是否为空
	 * @param objs， 格式为[obj, msg, obj, msg]
	 * @return
	 */
	public static void checkNotEmpties(Object ... objs) {
		if(objs == null)
			return;
		if(objs.length % 2 != 0)
			throw new TsException("参数长度必需为偶数");
		for(int i=0; i<objs.length; i=i+2) {
			Object obj = objs[i];
			String msg = (String)objs[i+1];
			if((obj == null)||((obj instanceof String) && ((String) obj).trim().isEmpty()))
				throw new TsException(msg + "值不能为空");
		}
	}
	/**
	 * 批量检查参数是否为空或空串
	 * @param params 参数Map对象
	 * @param objs， 格式为[obj, msg, obj, msg]
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static void checkNotEmptiesFromParamObject(Map params, Object ... objs) {
		if(objs == null)
			return;
		if(objs.length % 2 != 0)
			throw new TsException("参数长度不符,当前长度为" + objs.length);
		for(int i=0; i<objs.length; i=i+2) {
			Object obj = params.get(objs[i]);
			String msg = (String)objs[i+1];
			if((obj == null)||((obj instanceof String) && ((String) obj).trim().isEmpty()))
				throw new TsException(msg + "值不能为空");
		}
	}
	/**
	 * 检查对象是否为空或空串，如果是抛出异常
	 * @param obj
	 * @param msg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  static <T> T checkNotEmptyAndTrim(T obj, String msg) {
		if (obj == null)
			throw new TsException(msg == null ? "" : msg);
		if ((obj instanceof String) && ((String) obj).isEmpty())
			throw new TsException(msg == null ? "" : msg);
		if (obj instanceof String) {
			return ((T) ((String) obj).trim());
		}
		return obj;
	}

	/**
	 * 检查json参数对象的值是否为空
	 * 
	 * @param paramsJson
	 * @param params
	 * @return
	 */
	public static void checkParamByJson(JSONObject paramsJson, String... params) {
		checkNotEmpty(paramsJson, "params is null");
		for (String param : params) {
			checkNotEmpty(paramsJson.get(param), format("%s must not null", param));
		}
	}

	/**
	 * 是否为空，如果是字符串时空白字符也算为空
	 * 
	 * @param val
	 * @return
	 */
	public static boolean isEmpty(Object val) {
		if(val == null)
			return true;
		if(val instanceof String) {
			String s = (String)val;
			return s.isEmpty();
		}
		return false;
	}

	/**
	 * 判断字符串是否为空或是空白字符串
	 *
	 * @param strobj
	 * @return
	 */
	public static boolean isTrimEmpty(Object strobj) {
		if (strobj instanceof String) {
			String str = (String) strobj;
			return str == null || str.equals("") || str.trim().isEmpty();
		}
		return strobj == null;
	}

	/**
	 * 转换异常
	 * @param e
	 * @return
	 */
	public static TsException convertException(Exception e) {
		if(e instanceof TsException) {
			throw (TsException)e;
		}
		throw new TsException(e);
	}
	/**
	 * 转换异常
	 * @param e
	 * @param defaultMessage
	 * @return
	 */
	public static TsException convertException(Exception e, String defaultMessage) {
		if(e instanceof TsException) {
			return (TsException)e;
		}
		return new TsException(defaultMessage, e);
	}
	/**
	 * 转换异常
	 * @param e
	 * @param defaultMessage
	 * @return
	 */
	public static TsException convertException(String defaultMessage, Throwable e) {
		if(e instanceof TsException) {
			return (TsException)e;
		}
		return new TsException(defaultMessage, e);
	}
	/**
	 * 转换异常
	 * @param code
	 * @param defaultMessage
	 * @param e
	 * @return
	 */
	public static TsException convertException(String code, String defaultMessage, Throwable e) {
		if(e instanceof InvocationTargetException) {
			e = ((InvocationTargetException)e).getCause();
		}
		if(e instanceof TsException) {
			return (TsException)e;
		}
		return new TsException(code, defaultMessage, e);
	}
	/**
	 * 转换异常
	 * @param e
	 * @param defaultMessage
	 * @return
	 */
	public static TsException convertException(Exception e, String errorCode, String defaultMessage) {
		if(e instanceof TsException) {
			return (TsException)e;
		}
		return new TsException(errorCode, defaultMessage, e);
	}
	/**
	 * 检查两个对象是否equal
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isEquals(Object a, Object b) {
		if(a == null && b == null) {
			return true;
		}
		if((a instanceof String)&&(b instanceof String)) {
			if(isBlank(a) && isBlank(b)) {
				return true;
			}
		}
		if(a != null) {
			return a.equals(b);
		}
		if(b != null) {
			return b.equals(a);
		}
		return false;
	}
	/**
	 * 检查两个字符串不区分大小写是否一致
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static boolean isEqualsIgnoreCase(String obj1, String obj2) {
		if(obj1 == null && obj2 == null)
			return true;
		if(obj1 != null && obj2 != null)
			return obj1.equalsIgnoreCase(obj2);
		return false;
	}
	/**
	 * 获取异常栈信息
	 * @param e
	 * @return
	 */
	public static String getStack(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static Long convertToLong(Object object) {
		if(object == null || (object instanceof Long))
			return (Long)object;
		return Long.valueOf(object.toString());
	}
	public static Double convertToDouble(Object object) {
		if(object == null || (object instanceof Double))
			return (Double)object;
		return Double.valueOf(object.toString());
	}

	public static Integer convertToInteger(Object object) {
		if(object == null || (object instanceof Integer))
			return (Integer)object;
		return Integer.valueOf(object.toString());
	}
	public static int converToIntValue(Object object, int def) {
		if(object == null) {
			return def;
		}
		if(object instanceof Integer) {
			return (Integer)object;
		}
		return Integer.valueOf(object.toString());
	}

	public static Boolean converToBoolean(Object object) {
		if(object == null || (object instanceof Boolean))
			return (Boolean)object;
		//数字不为0表示true
		if(object instanceof Number) {
			return ((Number)object).intValue() != 0;
		}
		return Boolean.valueOf(object.toString());
	}

	public static boolean converToBoolean(Object object, boolean b) {
		Boolean flag = converToBoolean(object);
		if(flag == null)
			return b;
		return flag;
	}

	public static TsException createException(String message) {
		return new TsException("UNKOWN_CODE", message, null);
	}
	public static TsException createException(String code, String message) {
		return new TsException(code, message, null);
	}
	public static TsException createException(String code, String message, Throwable e) {
		return new TsException(code, message, e);
	}

	public static boolean isBlank(Object val) {
		if(val == null)
			return true;
		if(val instanceof String) {
			String s = (String)val;
			return StringUtils.isBlank(s);
		}
		return false;
	}
	public static java.sql.Date convertToSqlDate(Object obj){
		Date date = convertToDate(obj);
		if(date != null) {
			if(date instanceof java.sql.Date) {
				return (java.sql.Date)date;
			}
			return new java.sql.Date(date.getTime()); 
		}
		return null;
	}
	public static java.sql.Timestamp convertToTimestamp(Object obj){
		Date date = convertToDate(obj);
		if(date != null) {
			if(date instanceof java.sql.Timestamp) {
				return (java.sql.Timestamp)date;
			}
			return new java.sql.Timestamp(date.getTime()); 
		}
		return null;
	}
	public static Date convertToDate(Object obj) {
		return convertToDate(obj, null);
	}
	public static Date convertToDate(Object obj, String format) {
		if(obj == null)
			return null;
		if(obj instanceof Date) {
			return (Date)obj;
		}
		if(obj instanceof String) {
			String s = (String)obj;
			//数字
			if(s.matches("\\d+")) {
				return new Date(Long.valueOf(s));
			}
			if(format == null) {
				if(s.indexOf(":") < 0) {
					return java.sql.Date.valueOf(s);
				}
				//yyyy-MM-dd HH:mm 或 yyyy-MM-dd HH:mm:ss
				if(s.indexOf(":") == s.lastIndexOf(":")) {
					s = s + ":00";
				}
				return java.sql.Timestamp.valueOf(s);
			}else {
				try {
					return new SimpleDateFormat(format).parse(s);
				} catch (ParseException e) {
					throw GlobalUtils.createException("CONVERT_DATE_ERROR", "不能将" + obj + "按" + format+"格式转成日期对象", e);
				}
			}
		}
		if(obj instanceof Long) {
			return new Date((Long)obj);
		}
		throw createException("不能将" + obj + "转成日期对象");
	}

	private static final Object UNDERLINE = "_";

	/**
	 * 驼峰转下划线
	 *
	 * @param param
	 * @return
	 */
	public static String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append(UNDERLINE);
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 下划线转驼峰
	 *
	 * @param param
	 * @return
	 */
	public static String underlineToCamel(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		StringBuilder sb = new StringBuilder(param);
		Matcher mc = Pattern.compile(UNDERLINE.toString()).matcher(param);
		int i = 0;
		while (mc.find()) {
			int position = mc.end() - (i++);
			sb.replace(position - 1, position + 1,
					sb.substring(position, position + 1).toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 下划线转驼峰
	 *
	 * @param param
	 * @return
	 */
	public static String underlineToCamel(String param, String split) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		StringBuilder sb = new StringBuilder(param);
		Matcher mc = Pattern.compile(split).matcher(param);
		int i = 0;
		while (mc.find()) {
			int position = mc.end() - (i++);
			// String.valueOf(Character.toUpperCase(sb.charAt(position)));
			sb.replace(position - 1, position + 1,
					sb.substring(position, position + 1).toUpperCase());
		}
		return sb.toString();
	}
	/**
	 * 转成数组
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] getArray(T ...args) {
		T[] pls = args;
		return pls;
	}

}
