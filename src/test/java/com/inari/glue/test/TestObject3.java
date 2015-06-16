package com.inari.glue.test;

import java.util.Collection;
import java.util.Map;

import com.inari.commons.config.Configured;
import com.inari.commons.config.ConfigObject;
import com.inari.commons.config.StringConfigurable;

public class TestObject3 implements ConfigObject {
    
    private String id;
    
    @Configured( required=false )
    public Collection<Object> list;
    
    @Configured( required=false )
    public Collection<StringConfigurable> stringConfigurableList;
    
    @Configured( required=false )
    public Collection<ConfigObject> configObjectList;
    
    @Configured( required=false )
    public Map<String, Object> map;
    
    @Configured( required=false )
    public Map<String, StringConfigurable> stringConfigurableMap;
    
    @Configured( required=false )
    public Map<String, ConfigObject> configObjectMsp;
    
    
    public TestObject3() {}
    
    public TestObject3( String id ) {
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
        return "TestObject3:" + id;
    }

}
