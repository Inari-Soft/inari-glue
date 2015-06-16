package com.inari.glue.test.factories;

import com.inari.commons.config.IConfigObject;

public abstract class AA implements IConfigObject {
    
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
