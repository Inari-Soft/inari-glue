package com.inari.glue.test.factories;

import com.inari.commons.config.IConfigObject;

public class B extends A implements IConfigObject {
    
    @Override
    public void build() {
        configId = "B";
    }

}
