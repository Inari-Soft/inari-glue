package com.inari.glue.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.inari.glue.test.TestC;
import com.inari.glue.test.TestEnum;
import com.inari.glue.test.TestInterfaceImpl;
import com.inari.glue.test.TestInterfaceMapping;
import com.inari.glue.test.TestObject1;
import com.inari.glue.test.TestObject2;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;

public class InstanceFactoryTest {
    
    @Test
    public void setFieldValue() throws Exception {
        InstanceFactory instanceFactory = createFactory();
        TestObject1 testObject = new TestObject1( "object1" );
        
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=null\n" + 
            "  value2=null\n" + 
            "  number1=0\n" + 
            "  number2=0\n" + 
            "  number3=0.0\n" + 
            "  number4=0.0\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
        ConfigData config = new ConfigData( "object1", TestObject1.class );
        config.put( "value1", "aStringValue" );
        config.put( "otherNameForValue2", "aStringValue2" );
        config.put( "number1", "123" );
        config.put( "number2", "123574747565" );
        config.put( "number3", "123.654" );
        config.put( "number4", "123.4839480458204" );
        
        Field field = TestObject1.class.getDeclaredField( "value1" );
        assertNotNull( field );
        instanceFactory.setFieldValue( field, testObject, config );
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=aStringValue\n" + 
            "  value2=null\n" + 
            "  number1=0\n" + 
            "  number2=0\n" + 
            "  number3=0.0\n" + 
            "  number4=0.0\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
        field = TestObject1.class.getDeclaredField( "value2" );
        assertNotNull( field );
        instanceFactory.setFieldValue( field, testObject, config );
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=aStringValue\n" + 
            "  value2=aStringValue2\n" + 
            "  number1=0\n" + 
            "  number2=0\n" + 
            "  number3=0.0\n" + 
            "  number4=0.0\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
        field = TestObject1.class.getDeclaredField( "number1" );
        assertNotNull( field );
        instanceFactory.setFieldValue( field, testObject, config );
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=aStringValue\n" + 
            "  value2=aStringValue2\n" + 
            "  number1=123\n" + 
            "  number2=0\n" + 
            "  number3=0.0\n" + 
            "  number4=0.0\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
        field = TestObject1.class.getDeclaredField( "number2" );
        assertNotNull( field );
        instanceFactory.setFieldValue( field, testObject, config );
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=aStringValue\n" + 
            "  value2=aStringValue2\n" + 
            "  number1=123\n" + 
            "  number2=123574747565\n" + 
            "  number3=0.0\n" + 
            "  number4=0.0\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
        field = TestObject1.class.getDeclaredField( "number3" );
        assertNotNull( field );
        instanceFactory.setFieldValue( field, testObject, config );
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=aStringValue\n" + 
            "  value2=aStringValue2\n" + 
            "  number1=123\n" + 
            "  number2=123574747565\n" + 
            "  number3=123.654\n" + 
            "  number4=0.0\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
        field = TestObject1.class.getDeclaredField( "number4" );
        assertNotNull( field );
        instanceFactory.setFieldValue( field, testObject, config );
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=aStringValue\n" + 
            "  value2=aStringValue2\n" + 
            "  number1=123\n" + 
            "  number2=123574747565\n" + 
            "  number3=123.654\n" + 
            "  number4=123.4839480458204\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
        // if config is not available and property is not mandatory: 
        config.remove( "value1" );
        field = TestObject1.class.getDeclaredField( "value1" );
        assertNotNull( field );
        instanceFactory.setFieldValue( field, testObject, config );
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=aStringValue\n" + 
            "  value2=aStringValue2\n" + 
            "  number1=123\n" + 
            "  number2=123574747565\n" + 
            "  number3=123.654\n" + 
            "  number4=123.4839480458204\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
        // and if null is set in the config, null should also set to field
        config.put( "value1", (String) null );
        field = TestObject1.class.getDeclaredField( "value1" );
        assertNotNull( field );
        instanceFactory.setFieldValue( field, testObject, config );
        assertEquals( 
            "TestObject1:object1[\n" + 
            "  value1=null\n" + 
            "  value2=aStringValue2\n" + 
            "  number1=123\n" + 
            "  number2=123574747565\n" + 
            "  number3=123.654\n" + 
            "  number4=123.4839480458204\n" + 
            "  reference1=null\n" + 
            "]\n", 
            testObject.toString() 
        );
        
    }
    
    @Test 
    public void testGetTypedPropertyValue() {
        InstanceFactory instanceFactory = createFactory();
        
        // collection
        Collection<String> stringList = new ArrayList<String>();
        stringList.add( "String1" );
        stringList.add( "String2" );
        stringList.add( "String3" );
        
        ConfigData configData = new ConfigData( "test", TestObject1.class );
        configData.putCollection( "stringList", stringList );
        
        Object configList = instanceFactory.getTypedPropertyValue( "stringList", configData, stringList.getClass(), String.class );
        assertNotNull( configList );
        assertEquals( "[String1, String2, String3]", configList.toString() );
        
        try {
            configList = instanceFactory.getTypedPropertyValue( "stringList", configData, stringList.getClass(), int.class );
            fail( "Wrong Type parameter: Boolean! This should throw an exception." );
        } catch ( Exception e ) {
            assertEquals( "Failed to create value instance for stringList type: class java.util.ArrayList", e.getMessage() );
            Throwable t = e.getCause();
            assertNotNull( t );
            assertTrue( t instanceof NumberFormatException );
            assertEquals( "For input string: \"String1\"", t.getMessage() );
        }
        
        // map
        Map<String, String> stringMap = new LinkedHashMap<String, String>();
        stringMap.put( "value1", "1" );
        stringMap.put( "value2", "1.5" );
        stringMap.put( "value3", "true" );
        
        configData.putMap( "stringMap", stringMap );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  stringList:String1|String2|String3\n" + 
            "  stringMap:value1=1|value2=1.5|value3=true\n" + 
            "}", 
            configData.toString() 
        );
        
        Object configMap = instanceFactory.getTypedPropertyValue( "stringMap", configData, LinkedHashMap.class, String.class, String.class );
        assertNotNull( configMap );
        assertEquals( 
            "{value1=1, value2=1.5, value3=true}", 
            configMap.toString() 
        );
        
        try {
            configMap = instanceFactory.getTypedPropertyValue( "stringMap", configData, LinkedHashMap.class, String.class );
            fail( "Missing value Type parameter: String! This should throw an exception." );
        } catch ( Exception e ) {
            assertEquals( "Failed to create value instance for type: class java.util.LinkedHashMap", e.getMessage() );
            Throwable t = e.getCause();
            assertNotNull( t );
            assertTrue( t instanceof ArrayIndexOutOfBoundsException );
            assertEquals( "1", t.getMessage() );
        }
        
        try {
            configMap = instanceFactory.getTypedPropertyValue( "stringMap", configData, LinkedHashMap.class, String.class, int.class );
            fail( "Missing value Type parameter: String! This should throw an exception." );
        } catch ( Exception e ) {
            assertEquals( "Failed to create value instance for type: class java.util.LinkedHashMap", e.getMessage() );
            Throwable t = e.getCause();
            assertNotNull( t );
            assertTrue( t instanceof NumberFormatException );
            assertEquals( "For input string: \"1.5\"", t.getMessage() );
        }
        
        configMap = instanceFactory.getTypedPropertyValue( "stringMap", configData, LinkedHashMap.class, String.class, boolean.class );
        assertNotNull( configMap );
        assertEquals( 
            "{value1=false, value2=false, value3=true}", 
            configMap.toString() 
        );
    }
    
    @Test
    public void testGetPropertyValue() {
        InstanceFactory instanceFactory = createFactory();
        
        Object value = instanceFactory.getPropertyValue( String.class, "A String" );
        assertNotNull( value );
        assertTrue( value instanceof String );
        assertEquals( "A String", value );
        
        value = instanceFactory.getPropertyValue( Integer.class, "1" );
        assertNotNull( value );
        assertTrue( value instanceof Integer );
        assertEquals( Integer.valueOf( 1 ), value );
        
        value = instanceFactory.getPropertyValue( Long.class, "1564564546" );
        assertNotNull( value );
        assertTrue( value instanceof Long );
        assertEquals( Long.valueOf( 1564564546 ), value );
        
        value = instanceFactory.getPropertyValue( Float.class, "1.4" );
        assertNotNull( value );
        assertTrue( value instanceof Float );
        assertEquals( Float.valueOf( 1.4f ), value );
        
        value = instanceFactory.getPropertyValue( Double.class, "1.4467467467474" );
        assertNotNull( value );
        assertTrue( value instanceof Double );
        assertEquals( Double.valueOf( 1.4467467467474d ), value );
        
        value = instanceFactory.getPropertyValue( Position.class, "10,20" );
        assertNotNull( value );
        assertTrue( value instanceof Position );
        assertEquals( "Position: 10,20", value.toString() );
        
        value = instanceFactory.getPropertyValue( Rectangle.class, "10,20,100,100" );
        assertNotNull( value );
        assertTrue( value instanceof Rectangle );
        assertEquals( "Rectangle: 10,20,100,100", value.toString() );
        
        value = instanceFactory.getPropertyValue( Boolean.class, "true" );
        assertNotNull( value );
        assertEquals( Boolean.TRUE, value );
        
        value = instanceFactory.getPropertyValue( boolean.class, "false" );
        assertNotNull( value );
        assertEquals( Boolean.FALSE, value );
        
        try {
            value = instanceFactory.getPropertyValue( Integer.class, "A String" );
            fail( "This should throw an Exception" );
        } catch ( Exception e ) {
            assertEquals( "For input string: \"A String\"", e.getMessage() );
        }
    }
    
    @Test
    public void testBuildSimpleObject() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "test" );
        InstanceFactory instanceFactory = new InstanceFactory( context );
        
        ConfigData configData = context.createConfig( "testC1", TestC.class );
        configData.put( "valueA", "A" );
        configData.put( "valueB", "B" );
        configData.put( "valueC", "C" );
        
        instanceFactory.build( "testC1" );
        
        TestC testC = context.getInstance( "testC1" );
        assertNotNull( testC );
        assertEquals( 
            "TestC : valueA=A, valueB=B, valueC=C", 
            testC.toString() 
        );
    }
    
    @Test
    public void testEnumObject() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "test" );
        InstanceFactory instanceFactory = new InstanceFactory( context );
        
        ConfigData configData = context.createConfig( "testEnum", TestEnum.class );
        configData.put( "dir1", Direction.NORTH.toString() );
        configData.put( "dir2", Direction.SOUTH.toString() );
        
        instanceFactory.build( "testEnum" );
        
        TestEnum testObject = context.getInstance( "testEnum" );
        assertNotNull( testObject );
        assertEquals( 
            "TestEnum: Direction1=NORTH Direction2=SOUTH", 
            testObject.toString() 
        );
        
    }
    
    @Test
    public void testBuildReferencedObject() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "test" );
        InstanceFactory instanceFactory = new InstanceFactory( context );
        
        ConfigData configData1 = context.createConfig( "testObj1", TestObject2.class );
        configData1.put( "reference1", "testObj2" );
        configData1.put( "reference2", "testObj3" );
        context.createConfig( "testObj2", TestObject2.class );
        context.createConfig( "testObj3", TestObject2.class );
        
        instanceFactory.build( "testObj1" );
        
        TestObject2 testObj = context.getInstance( "testObj1" );
        assertNotNull( testObj );
        assertEquals( 
            "TestObject2:testObj1[\n" + 
            "  reference1=TestObject2:testObj2[\n" + 
            "  reference1=null\n" + 
            "  reference2=null\n" + 
            "]\n" + 
            "  reference2=TestObject2:testObj3[\n" + 
            "  reference1=null\n" + 
            "  reference2=null\n" + 
            "]\n" + 
            "]", 
            testObj.toString() 
        );
    }
    
    @Test
    public void testBuildInterfaceReferenced() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "test" );
        InstanceFactory instanceFactory = new InstanceFactory( context );
        
        ConfigData configData1 = context.createConfig( "testObj1", TestInterfaceMapping.class );
        configData1.put( "interfacedReference", "testObj2" );
        ConfigData configData2 = context.createConfig( "testObj2", TestInterfaceImpl.class );
        configData2.put( "value1", "value1" );
        
        instanceFactory.build( "testObj1" );
        TestInterfaceMapping testObj = context.getInstance( "testObj1" );
        assertNotNull( testObj );
        assertEquals( 
            "TestInterfaceMapping: interfacedReference=TestInterfaceImpl: value1=value1", 
            testObj.toString() 
        );
    }
    
    private InstanceFactory createFactory() {
        GlueServiceImpl service = new GlueServiceImpl();
        GlueContextImpl context = (GlueContextImpl) service.createContext( "test" );
        return new InstanceFactory( context );
    }

}
