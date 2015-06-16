package com.inari.glue.impl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Collection;

import org.junit.Test;

import com.inari.glue.test.TestA;
import com.inari.glue.test.TestB;
import com.inari.glue.test.TestC;
import com.inari.glue.test.TestY;

public class GlueUtilsTest {
    
    @Test
    public void testGetAllConfiguredFields() throws Exception {
        TestA testA = new TestA();
        TestB testB = new TestB();
        TestC testC = new TestC();
        
        Collection<Field> fieldsA = GlueUtils.getAllConfiguredFields( testA ); 
        Collection<Field> fieldsB = GlueUtils.getAllConfiguredFields( testB ); 
        Collection<Field> fieldsC = GlueUtils.getAllConfiguredFields( testC ); 
        
        assertEquals( 
            "[protected java.lang.String com.inari.glue.test.TestA.valueA]", 
            fieldsA.toString() 
        );
        assertEquals( 
            "[protected java.lang.String com.inari.glue.test.TestB.valueB, protected java.lang.String com.inari.glue.test.TestA.valueA]", 
            fieldsB.toString() 
        );
        assertEquals( 
            "[private java.lang.String com.inari.glue.test.TestC.valueC, protected java.lang.String com.inari.glue.test.TestB.valueB, protected java.lang.String com.inari.glue.test.TestA.valueA]", 
            fieldsC.toString() 
        );
        
        TestY testY = new TestY();
        Collection<Field> fieldsY = GlueUtils.getAllConfiguredFields( testY ); 
        assertEquals( 
            "[protected java.lang.String com.inari.glue.test.TestY.valueY]", 
            fieldsY.toString() 
        );
    }

}
