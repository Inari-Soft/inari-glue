package com.inari.glue.test.factories;

public class A {
    
    public static final String NOT_BUILT = "NOT_BUILT";
    
    public String configId = NOT_BUILT;

    public String configId() {
        return configId;
    }

    public void configId( String id ) {
        configId = id;
    }
    
    public void build() {
        configId = "A";
    }

}
