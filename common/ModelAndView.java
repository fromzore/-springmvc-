package com.qunshuo.common;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private Map<String, Object> sessions = new HashMap<String, Object>();
    private Map<String, Object> requests = new HashMap<String, Object>();

    public String view;
    public Boolean isRedirect = false;

    public Map<String, Object> getRequests() {
        return requests;
    }

    public Map<String, Object> getSessions() {
        return sessions;
    }

    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }

    public void addObject(String key, Object value) {
        requests.put(key, value);
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean isRedirect) {
        this.isRedirect = isRedirect;
    }

    public void addSessionAttribute(String key, Object object) {
        sessions.put(key, object);
    }
}
