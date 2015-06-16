package com.inari.glue.test;

import com.inari.commons.config.Configured;
import com.inari.commons.config.IConfigObject;

public class TestObject1 implements IConfigObject {
    
    private String id;
    
    @Configured( required=false )
    public String value1;
    
    @Configured( name="otherNameForValue2", required=false )
    public String value2;
    
    @Configured
    public int number1;
    @Configured
    public long number2;
    @Configured
    public float number3;
    @Configured
    public double number4;
    
    @Configured( required=false )
    public TestObject2 reference1;
    
    
    public TestObject1() {
        id = "test";
    }
    
    public TestObject1( String id ) {
        this.id = id;
    }
    
    
    

    @Override
    public String configId() {
        return id;
    }

    @Override
    public void configId( String id ) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "TestObject1:" ).append( id ).append( "[\n" );
        builder.append( "  value1=" ).append( value1 ).append( "\n" );
        builder.append( "  value2=" ).append( value2 ).append( "\n" );
        builder.append( "  number1=" ).append( number1 ).append( "\n" );
        builder.append( "  number2=" ).append( number2 ).append( "\n" );
        builder.append( "  number3=" ).append( number3 ).append( "\n" );
        builder.append( "  number4=" ).append( number4 ).append( "\n" );
        builder.append( "  reference1=" ).append( reference1 ).append( "\n" );
//        builder.append( "  stringList=" ).append( stringList ).append( "\n" );
//        builder.append( "  stringMap=" ).append( stringMap ).append( "\n" );
//        builder.append( "  configObjectList=" ).append( configObjectList ).append( "\n" );
//        builder.append( "  configObjectMap=" ).append( configObjectMap ).append( "\n" );
//        builder.append( "  stringObjectList=" ).append( stringObjectList ).append( "\n" );
//        builder.append( "  stringObjectMap=" ).append( stringObjectMap ).append( "\n" );
        builder.append( "]\n" );
        return builder.toString();    
    }
}