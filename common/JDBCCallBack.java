package com.qunshuo.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface JDBCCallBack<T> {

    T rsToObject(ResultSet rs) throws SQLException;

    void setParams(PreparedStatement stmt) throws SQLException;

}
