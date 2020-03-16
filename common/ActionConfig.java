package com.qunshuo.common;

import java.util.HashMap;
import java.util.Map;

public class ActionConfig {

    private String clsName;
    private String methodName;
    private String name;
    private String[] httpMethod;

    public ActionConfig() {

    }

    private Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();

    public void addResult(String key, ResultConfig resultConfig) {
        results.put(key, resultConfig);
    }

    public Map<String, ResultConfig> getResults() {
        return results;
    }

    public ResultConfig getResult(String resultKey) {
        return results.get(resultKey);
    }

    public void setResults(Map<String, ResultConfig> results) {

        if (results == null) {
            results = new HashMap<String, ResultConfig>();
        }
        this.results = results;
    }

    public String[] getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String[] httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
