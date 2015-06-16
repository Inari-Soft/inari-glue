package com.inari.glue.test.factories;

import com.inari.glue.IConfigObjectFactory;
import com.inari.commons.config.IConfigObject;

public class TestFactory<T extends IConfigObject> implements IConfigObjectFactory<T> {
    
    private final Class<T> type;
    public TestFactory( Class<T> type ) {
        this.type = type;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public void build( T configObject ) {
        ( (A) configObject ).build();
    }

}
