package com.inari.glue.test;

import com.inari.commons.config.Configured;
import com.inari.commons.config.ConfigObject;

public class TestObject2 implements ConfigObject {
    
    private String id;
    
    @Configured( required=false )
    public TestObject2 reference1;
    @Configured( required=false )
    public TestObject2 reference2;
    
    
    public TestObject2() {}
    
    public TestObject2( String id ) {
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
        return "TestObject2:" + id + "[\n" +
               "  reference1=" + reference1 + "\n" +
               "  reference2=" + reference2 + "\n" +
               "]";
    }
}
