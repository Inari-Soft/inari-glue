package com.inari.glue;


public interface IConfigObjectFactory<T> {
    
    public Class<T> type();
    
    public void build( T configObject );

}
