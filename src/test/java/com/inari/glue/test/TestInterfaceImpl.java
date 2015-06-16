package com.inari.glue.test;

import com.inari.commons.config.Configured;

public class TestInterfaceImpl implements ITestInterface {
    
    private String id;
    
    @Configured
    private String value1;

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
        return "TestInterfaceImpl: value1=" + value1;
    }

}
