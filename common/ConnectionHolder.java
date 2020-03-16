package com.qunshuo.common;

import java.sql.Connection;

public class ConnectionHolder {

    private Connection conn;
    private boolean isStartTran = false;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public boolean isStartTran() {
        return isStartTran;
    }

    public void setStartTran(boolean isStartTran) {
        this.isStartTran = isStartTran;
    }
}
