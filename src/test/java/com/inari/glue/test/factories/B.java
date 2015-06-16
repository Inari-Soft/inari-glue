package com.inari.glue.test.factories;

import com.inari.commons.config.ConfigObject;

public class B extends A implements ConfigObject {
    
    @Override
    public void build() {
        configId = "B";
    }

}
