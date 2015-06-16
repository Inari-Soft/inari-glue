package com.inari.glue.test;

import com.inari.commons.config.Configured;
import com.inari.commons.config.ConfigObject;

public class TestInterfaceMapping implements ConfigObject {
    
    private String id;
    
    @Configured
    private ITestInterface interfacedReference;

    @Override
    public String configId() {
        return id;
    }

    @Override
    public void configId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TestInterfaceMapping: interfacedReference=" + interfacedReference;
    }

}
