package com.qunshuo.common;

import java.util.ArrayList;
import java.util.List;

public class ResultConfig {

    private String name;
    private String view;
    private boolean redirect;

    private List<ViewParameterConfig> viewParameterConfigs = new ArrayList<ViewParameterConfig>();

    public void addViewParameterConfig(ViewParameterConfig viewParameterConfig) {
        this.viewParameterConfigs.add(viewParameterConfig);
    }

    public List<ViewParameterConfig> getViewParameterConfigs() {
        return viewParameterConfigs;
    }

    public void setViewParameterConfigs(List<ViewParameterConfig> viewParameterConfigs) {
        if (viewParameterConfigs == null) {
            this.viewParameterConfigs = new ArrayList<ViewParameterConfig>();
        }
        this.viewParameterConfigs = viewParameterConfigs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

}
