package com.inari.glue.test;

import com.inari.commons.config.Configured;
import com.inari.commons.config.ConfigObject;

public class TestObjectMandatory implements ConfigObject {
    
    private String id;
    
    @Configured( required=true )
    public String mandatoryValue = null;
    
    
    public TestObjectMandatory() {}
    
    public TestObjectMandatory( String id ) {
        this.id = id;
    }

    @Override
    public String configId() {
        return id;
    }

    @Override
    public void configId( String id ) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "TestObjectMandatory:" + id;
    }

}
