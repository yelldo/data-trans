package com.transform.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultRowExtractor<T> {
	public T processRow(ResultSet rs, int index) throws SQLException;
}
