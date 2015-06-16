package com.inari.glue.test.factories;

public class E implements IA {
    
    public String configId = null;

    @Override
    public String configId() {
        return configId;
    }

    @Override
    public void configId( String id ) {
        configId = id;
    }
    
    public void build() {
        configId = "E";
    }

}
