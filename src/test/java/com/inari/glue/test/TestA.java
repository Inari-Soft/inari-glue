package com.inari.glue.test;

import com.inari.commons.config.Configured;
import com.inari.commons.config.IConfigObject;

public class TestA implements IConfigObject {
    
    @Configured
    protected String valueA;
    
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
        return "TestA : valueA=" + valueA; 
    }

}
