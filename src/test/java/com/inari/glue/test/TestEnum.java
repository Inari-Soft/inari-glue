package com.inari.glue.test;

import com.inari.commons.config.Configured;
import com.inari.commons.config.ConfigObject;
import com.inari.commons.geom.Direction;

public class TestEnum implements ConfigObject {
    
    private String configId;
    @Configured
    public Direction dir1;
    @Configured
    public Direction dir2;

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
        return "TestEnum: Direction1="+dir1+" Direction2="+dir2;
    }
    
    

}
