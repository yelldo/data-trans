package com.transform.jdbc;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class Converts extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;
	public static Converts get() {
		return new Converts();
	}
	/**
	 * 设置字典所属的项目
	 * @param project
	 * @return
	 */
	public Converts setProject(String project) {
		this.put("project", project);
		return this;
	}
	/**
	 * 添加字典显示列
	 * @param column
	 * @param group
	 * @return
	 */
	public Converts addCodeText(String column, String group) {
		this.put(column + "_display", "code:" + group + ":" + column);
		return this;
	}
	/**
	 * 设置包含的字段以,号隔开
	 * @param includes
	 * @return
	 */
	public Converts setIncludes(String... includes) {
		this.put("includes", StringUtils.join(includes, ","));
		return this;
	}
	/**
	 * 设置排除的字段以,号隔开
	 * @param excludes
	 * @return
	 */
	public Converts setExcludes(String... excludes) {
		this.put("excludes", StringUtils.join(excludes, ","));
		return this;
	}
	/**
	 * 添加其它表的列作为显示，取对方表的id=当前column，以对方表的displayColumn作为显示
	 * @param column
	 * @param table
	 * @param displayColumn
	 * @return
	 */
	public Converts addForeignKey(String column, String table, String displayColumn) {
		this.put(column + "_display", "dataset:" + table + "."+displayColumn+":id=" + column);
		return this;
	}
	/**
	 * 通过回调函数来产生列的显示
	 * @param column
	 * @param callback
	 * @return
	 */
	@SuppressWarnings("serial")
	public Converts addDisplay(String column, ConvertCallback callback) {
		this.put(column + "_display", new groovy.lang.Closure<Object>(this) {
			@Override
			public Object call(Object... args) {
				return callback.convert(args[0]);
			}
		});
		return this;
	}
	/**
	 * 通过回调函数来产生新的列值
	 * @param column
	 * @param callback
	 * @return
	 */
	@SuppressWarnings("serial")
	public Converts add(String column, ConvertCallback callback) {
		this.put(column, new groovy.lang.Closure<Object>(this) {
			@Override
			public Object call(Object... args) {
				return callback.convert(args[0]);
			}
		});
		return this;
	}
}
