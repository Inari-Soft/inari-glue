package com.inari.glue.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.inari.glue.GlueContext;
import com.inari.glue.GlueException;
import com.inari.glue.GlueService;
import com.inari.glue.IConfigObjectFactory;
import com.inari.glue.test.TestA;
import com.inari.glue.test.TestC;
import com.inari.glue.test.TestObject1;
import com.inari.glue.test.TestObject2;
import com.inari.glue.test.factories.AA;
import com.inari.glue.test.factories.AB;
import com.inari.glue.test.factories.AC;
import com.inari.glue.test.factories.B;
import com.inari.glue.test.factories.C;
import com.inari.glue.test.factories.D;
import com.inari.glue.test.factories.E;
import com.inari.glue.test.factories.TestFactory;
import com.inari.commons.config.IConfigObject;
import com.inari.commons.lang.TypedKey;

public class GlueContextImplTest {
    
    @Test
    public void testCreateConfig() {
        GlueService service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "testContext" );
        
        assertNotNull( context );
        assertEquals( "testContext", context.name() );
        
        ConfigData data = context.createConfig( "test1", TestObject1.class );
        assertNotNull( context );
        assertEquals( 
            "ConfigData: id:test1 {\n" + 
            "}", 
            data.toString()
        );
        assertTrue( context.hasConfig( "test1" ) );
        
        Class<? extends IConfigObject> type = data.type();
        assertNotNull( type );
        assertEquals( 
            "class com.inari.glue.test.TestObject1", 
            type.toString()
        );
        
        // with typdef set:
        context.clear();
        context.defineAlias( "testObj1", TestObject1.class  );
        data = context.createConfig( "test1", TestObject1.class );
        type = data.type();
        assertNotNull( type );
        assertEquals( 
            "class com.inari.glue.test.TestObject1", 
            type.toString()
        );

// TODO: allowing override?  
//        try {
//            data = context.createConfig( "test1", TestObject1.class );
//            fail( "should thrown an exception!" ); 
//        } catch ( GlueException ge ) {
//            assertEquals( "There is already a Config with id: test1!", ge.getMessage() );
//        }
    }
    
    @Test
    public void testGetOrCreateConfigData() {
        GlueService service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "testContext" );
        
        ConfigData data = context.getOrCreateConfig( "test1", TestObject1.class );
        assertNotNull( context );
        assertEquals( 
            "ConfigData: id:test1 {\n" + 
            "}", 
            data.toString()
        );
        
        data.put( "number1", 1L );
        
        assertEquals( 
            "ConfigData: id:test1 {\n" + 
            "  number1:1\n" + 
            "}", 
            data.toString()
        );
        
        data = context.getOrCreateConfig( "test1", TestObject1.class );
        assertNotNull( context );
        assertEquals( 
            "ConfigData: id:test1 {\n" + 
            "  number1:1\n" + 
            "}", 
            data.toString()
        );
        
        data = context.getConfig( "test1" );
        assertNotNull( context );
        assertEquals( 
            "ConfigData: id:test1 {\n" + 
            "  number1:1\n" + 
            "}", 
            data.toString()
        );
        
        data = context.getConfig( new TestObject1( "test1" ) );
        assertNotNull( context );
        assertEquals( 
            "ConfigData: id:test1 {\n" + 
            "  number1:1\n" + 
            "}", 
            data.toString()
        );
    }
    
    @Test
    public void testGetInstance() {
        GlueService service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "testContext" );
        
        IConfigObject obj = context.getInstance( (TypedKey<IConfigObject>) null );
        assertNull( obj );
        
        try {
            obj = context.getInstance( "testObj1" );
            fail( "this must throw an exception!" );
        } catch ( GlueException ge ) {
            assertEquals( "No Configuration with id: testObj1 found for context: testContext", ge.getMessage() );
        }
        
        context.getOrCreateConfig( "testObj1", TestObject2.class );
        
        assertTrue( context.hasConfig( "testObj1" ) );
        assertFalse( context.hasInstance( "testObj1" ) );
        
        obj = context.getInstance( "testObj1" );
        
        assertNotNull( obj );
        assertTrue( context.hasConfig( "testObj1" ) );
        assertTrue( context.hasInstance( "testObj1" ) );
    }
    
    @Test
    public void testGetSimpleInstance() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContext context = service.createContext( "test" );
        
        ConfigData config = context.createConfig( "testConfig", TestA.class );
        config.put( "valueA", "TestValueA" );
        
        TestA instance = context.getInstance( "testConfig" );
        
        assertNotNull( instance );
        assertEquals(
            "TestA : valueA=TestValueA",
            instance.toString()
        );
    }
    
    @Test
    public void testGetSimpleInstanceWithClassHirarchy() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContext context = service.createContext( "test" );
        
        ConfigData config = context.createConfig( "testConfig", TestC.class );
        config.put( "valueA", "TestValueA" );
        config.put( "valueB", "TestValueB" );
        config.put( "valueC", "TestValueC" );
        
        TestA instance = context.getInstance( "testConfig" );
        
        assertNotNull( instance );
        assertEquals(
            "TestC : valueA=TestValueA, valueB=TestValueB, valueC=TestValueC",
            instance.toString()
        );
    }
    
    @Test
    public void testGetInstanceWithReferences() {

        // TODO
        
    }
    
    @Test
    public void testGetInstanceWithReferenceCircle() {
        
        // TODO
        
    }
    
    @Test
    public void testGetInstanceWithComplex() {

        // TODO
        
    }
    
    @Test
    public void testGetInstanceWithComplexReferences() {
        
        // TODO
        
    }
    
    @Test
    public void testGetInstanceFactory() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "test" );
        
        TestFactory<B> bFactory = new TestFactory<B>( B.class );
        TestFactory<C> cFactory = new TestFactory<C>( C.class );
        
        context.registerInstanceFactory( bFactory );
        context.registerInstanceFactory( cFactory );
        
        IConfigObjectFactory<?> factory = context.getInstanceFactory( E.class );
        assertNull( factory );
        
        factory = context.getInstanceFactory( D.class );
        assertNotNull( factory );
        assertEquals( factory, cFactory );
        
        factory = context.getInstanceFactory( C.class );
        assertNotNull( factory );
        assertEquals( factory, cFactory );
        
        factory = context.getInstanceFactory( B.class );
        assertNotNull( factory );
        assertEquals( factory, bFactory );
    }
    
    @Test
    public void testGetInstanceFactoryWithAbstractSuperClass() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "test" );
        
        TestFactory<AA> aaFactory = new TestFactory<AA>( AA.class );
        TestFactory<AC> acFactory = new TestFactory<AC>( AC.class );
        
        context.registerInstanceFactory( aaFactory );
        context.registerInstanceFactory( acFactory );
        
        IConfigObjectFactory<?> factory = context.getInstanceFactory( E.class );
        assertNull( factory );
        
        factory = context.getInstanceFactory( AC.class );
        assertNotNull( factory );
        assertEquals( factory, acFactory );
        
        factory = context.getInstanceFactory( AB.class );
        assertNotNull( factory );
        assertEquals( factory, aaFactory );
        
        factory = context.getInstanceFactory( AA.class );
        assertNotNull( factory );
        assertEquals( factory, aaFactory );
    }

}
