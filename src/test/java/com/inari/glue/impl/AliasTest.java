package com.inari.glue.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inari.glue.test.TestObject1;


public class AliasTest {
    
    @Test
    public void testAlias() {
        Alias alias1 = new Alias( TestObject1.class );
        Alias alias2 = new Alias( "testObj1", TestObject1.class );
        
        assertEquals( 
            "Alias[ name:com.inari.glue.test.TestObject1 type:class com.inari.glue.test.TestObject1 ]", 
            alias1.toString() 
        );
        assertEquals( 
            "Alias[ name:testObj1 type:class com.inari.glue.test.TestObject1 ]", 
            alias2.toString() 
        );
        
        assertEquals( "com.inari.glue.test.TestObject1", alias1.alias() );
        assertEquals( "com.inari.glue.test.TestObject1", alias1.fullClassName() );
        assertEquals( TestObject1.class, alias1.type() );
        
        assertEquals( "testObj1", alias2.alias() );
        assertEquals( "com.inari.glue.test.TestObject1", alias2.fullClassName() );
        assertEquals( TestObject1.class, alias2.type() );
    }

}
