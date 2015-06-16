package com.inari.glue.test;

import com.inari.commons.config.Configured;

public class TestC extends TestB {
    
    @Configured
    private String valueC;

    @Override
    public String toString() {
        return "TestC : valueA=" + valueA + ", valueB=" + valueB + ", valueC=" + valueC; 
    }

}
