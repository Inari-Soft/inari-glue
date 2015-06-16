package com.inari.glue.test;

import com.inari.commons.config.Configured;
import com.inari.commons.config.ConfigObject;

public class TestY extends TestX implements ConfigObject {
    
    @Configured
    protected String valueY;
    
    private String configId;

    @Override
    public String configId() {
        return configId;
    }

    @Override
    public void configId( String id ) {
        configId = id;
    }

    @Override
    public String toString() {
        return "TestY : valueY=" + valueY; 
    }

}
