package com.inari.glue.test;

import java.util.Collection;
import java.util.Map;

import com.inari.commons.config.Configured;
import com.inari.commons.config.IConfigObject;
import com.inari.commons.config.IStringConfigurable;

public class TestObject3 implements IConfigObject {
    
    private String id;
    
    @Configured( required=false )
    public Collection<Object> list;
    
    @Configured( required=false )
    public Collection<IStringConfigurable> stringConfigurableList;
    
    @Configured( required=false )
    public Collection<IConfigObject> configObjectList;
    
    @Configured( required=false )
    public Map<String, Object> map;
    
    @Configured( required=false )
    public Map<String, IStringConfigurable> stringConfigurableMap;
    
    @Configured( required=false )
    public Map<String, IConfigObject> configObjectMsp;
    
    
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
