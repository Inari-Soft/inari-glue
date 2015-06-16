package com.inari.glue.test.factories;

import com.inari.glue.IConfigObjectFactory;
import com.inari.commons.config.ConfigObject;

public class TestFactory<T extends ConfigObject> implements IConfigObjectFactory<T> {
    
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
