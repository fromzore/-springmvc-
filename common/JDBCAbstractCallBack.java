package com.qunshuo.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class JDBCAbstractCallBack<T> implements JDBCCallBack<T> {

    @Override
    public T rsToObject(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public void setParams(PreparedStatement stmt) throws SQLException {

    }
}
