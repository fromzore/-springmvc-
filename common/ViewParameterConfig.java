package com.qunshuo.common;

public class ViewParameterConfig {

    private String name;
    private String from;

    public ViewParameterConfig(String name, String from) {
        this.name = name;
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
