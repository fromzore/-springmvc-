package com.qunshuo.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.qunshuo.exam.AppContext;
import com.qunshuo.exam.exception.DBException;
import com.qunshuo.exam.util.DBUtil;

public class JDBCTemplate<T> {

    public List<T> query(String sql, JDBCCallBack<T> jdbcCallback) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<T> data = new ArrayList<T>();
        boolean needMyClose = false;

        try {
            ConnectionHolder connectionHolder = (ConnectionHolder) AppContext.getContext()
                    .getObject("APP_REQUEST_THREAD_CONNECTION");

            //如果线程池中有现成的conn
            if (connectionHolder != null) {
                conn = connectionHolder.getConn();
            }
            if (conn == null) {
                conn = DBUtil.getConnection();
                needMyClose = true;
            }

            stmt = conn.prepareStatement(sql);
            jdbcCallback.setParams(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                T object = jdbcCallback.rsToObject(rs);
                data.add(object);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.close(rs, stmt, null);
            if (needMyClose) {
                DBUtil.close(null, null, conn);
            }
        }
        return data;
    }

    public T queryOne(String sql, JDBCCallBack<T> jdbcCallback) {

        List<T> data = query(sql, jdbcCallback);
        if (data != null && !data.isEmpty()) {
            return data.get(0);
        }
        return null;
    }

    public int update(String sql, JDBCCallBack<T> jdbcCallback) {

        Connection conn = null;
        PreparedStatement stmt = null;
        int count = 0;
        boolean needMyClose = false;

        try {
            //同获取线程池链接
            ConnectionHolder connectionHolder = (ConnectionHolder) AppContext.getContext().getObject("APP_REQUEST_THREAD_CONNECTION");
            if (connectionHolder != null) {
                conn = connectionHolder.getConn();
            }
            if (conn == null) {
                conn = DBUtil.getConnection();
                needMyClose = true;
            }
            stmt = conn.prepareStatement(sql);
            jdbcCallback.setParams(stmt);
            count = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.close(null, stmt, null);
            if (needMyClose) {
                DBUtil.close(null, null, conn);
            }
        }

        return count;
    }

    public int update(String sql) {
        return this.update(sql, new JDBCAbstractCallBack<T>() {
        });

    }

    public int insert(String sql, JDBCCallBack<T> jdbcCallback) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int id = 0;
        boolean needMyClose = false;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            jdbcCallback.setParams(stmt);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
        return id;
    }

    public void insertWithoutKey(String sql, JDBCCallBack<T> jdbcCallback) {

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            jdbcCallback.setParams(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.close(null, stmt, conn);
        }
    }

    public int getCount(String sql, JDBCCallBack<T> jdbcCallback) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.close(rs, stmt, conn);
        }

        return count;
    }

    public int getCount(String sql) {
        return this.getCount(sql, new JDBCAbstractCallBack<T>() {
        });

    }
}
