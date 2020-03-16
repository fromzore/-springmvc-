package com.qunshuo.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

import com.qunshuo.exam.AppContext;
import com.qunshuo.exam.util.DBUtil;

public class ConnectionDynamicProxy implements InvocationHandler {

    private Object target;

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        ConnectionHolder connectionHolder = (ConnectionHolder) AppContext.getContext()
                .getObject("APP_REQUEST_THREAD_CONNECTION");
        boolean needMyClose = false;
        boolean isCommitOrRollbackTran = false;

        if (connectionHolder == null) {
            Connection conn = DBUtil.getConnection();
            connectionHolder = new ConnectionHolder();
            connectionHolder.setConn(conn);
            // 有事务
            if (method.getName().equals("")) {
                DBUtil.setAutoCommit(conn, false);
                connectionHolder.setStartTran(true);
                isCommitOrRollbackTran = true;
            }
            AppContext.getContext().addObject("APP_REQUEST_THREAD_CONNECTION", conn);
            // 是否手动关闭资源
            needMyClose = true;

        } else {
            if (method.getName().equals("")) {
                if (!connectionHolder.isStartTran()) {
                    connectionHolder.setStartTran(true);
                    DBUtil.setAutoCommit(connectionHolder.getConn(), false);
                    isCommitOrRollbackTran = true;
                }
            }
        }
        try {
            result = method.invoke(target, args);
            if (method.getName().equals("")) {
                if (isCommitOrRollbackTran) {
                    DBUtil.commit(connectionHolder.getConn());
                }
            }
        } catch (Throwable throwable) {
            if (method.getName().equals("")) {
                if (isCommitOrRollbackTran) {
                    DBUtil.rollback(connectionHolder.getConn());
                }
            }
            throw throwable.getCause();
        } finally {
            //需要手动关闭资源一定要关闭
            if (needMyClose) {
                connectionHolder = (ConnectionHolder) AppContext.getContext()
                        .getObject("APP_REQUEST_THREAD_CONNECTION");
                DBUtil.close(null, null, connectionHolder.getConn());
                AppContext.getContext().removeObject("APP_REQUEST_THREAD_CONNECTION");
                connectionHolder.setConn(null);
                connectionHolder = null;
            }
        }
        return result;
    }

}
