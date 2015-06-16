package com.inari.glue.test;

import com.inari.commons.config.Configured;

public class TestB extends TestA {
    
    @Configured
    protected String valueB;

    @Override
    public String toString() {
        return "TestB : valueA=" + valueA + ", valueB=" + valueB; 
    }

}
