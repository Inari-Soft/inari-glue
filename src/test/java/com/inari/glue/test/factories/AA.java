package com.inari.glue.test.factories;

import com.inari.commons.config.ConfigObject;

public abstract class AA implements ConfigObject {
    
    private String configId = null;

    @Override
    public String configId() {
        return configId;
    }

    @Override
    public void configId( String id ) {
        configId = id;
    }
    
    public void build() {
        configId = "AA";
    }

}
