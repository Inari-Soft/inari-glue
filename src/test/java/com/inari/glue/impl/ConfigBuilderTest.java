package com.inari.glue.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.inari.glue.GlueContext;
import com.inari.glue.GlueException;
import com.inari.glue.GlueService;
import com.inari.glue.test.TestEnum;
import com.inari.glue.test.TestObject1;
import com.inari.glue.test.TestObject2;
import com.inari.glue.test.TestObject3;
import com.inari.glue.test.TestObjectMandatory;
import com.inari.commons.config.IConfigObject;
import com.inari.commons.config.IStringConfigurable;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;

public class ConfigBuilderTest {
    
    @Test
    public void textBuildReference() {
        
        ConfigBuilder builder = createConfigBuilder();
        ConfigData configData = new ConfigData( "Test", TestObject1.class );
        
        TestObject2 value1 = new TestObject2( "value1" );
        
        builder.buildReference( "testObj1", value1, configData );
        
        assertEquals( 
            "ConfigData: id:Test {\n" + 
            "  testObj1:value1\n" + 
            "}", 
            configData.toString() 
        );
    }
    
    @Test
    public void testBuildSimpleProperty() {
        
        ConfigBuilder builder = createConfigBuilder();
        ConfigData configData = new ConfigData( "Test", TestObject1.class );
        
        Object nullValue = null;
        Object value1 = "String1";
        Object value2 = 1;
        Object value3 = Long.MAX_VALUE;
        Object value4 = 1.1f;
        Object value5 = Double.MAX_VALUE;
        Object value6 = new Position( 5, 5 );
        
        ArrayList<String> value7 = new ArrayList<String>();
        value7.add( "eins" );
        value7.add( "zwei" );
        value7.add( "drei" );
        ArrayList<Position> value8 = new ArrayList<Position>();
        value8.add( new Position( 2, 2 ) );
        value8.add( new Position( 20, 20 ) );
        
        Map<String, String> value9 = new LinkedHashMap<String, String>();
        value9.put( "1", "aaa" );
        value9.put( "2", "bbb" );
        value9.put( "3", "ccc" );
        Map<String, Rectangle> value10 = new LinkedHashMap<String, Rectangle>();
        value10.put( "rect1", new Rectangle( 1, 1, 1, 1 ) ); 
        value10.put( "rect2", new Rectangle( 1, 10, 61, 71 ) ); 
        
        
        builder.buildProperty( "nullValue", nullValue, configData );
        builder.buildProperty( "value1", value1, configData );
        builder.buildProperty( "value2", value2, configData );
        builder.buildProperty( "value3", value3, configData );
        builder.buildProperty( "value4", value4, configData );
        builder.buildProperty( "value5", value5, configData );
        builder.buildProperty( "value6", value6, configData );
        builder.buildProperty( "value7", value7, configData );
        builder.buildProperty( "value8", value8, configData );
        builder.buildProperty( "value9", value9, configData );
        builder.buildProperty( "value10", value10, configData );
        
        assertEquals( 
            "ConfigData: id:Test {\n" + 
            "  value1:String1\n" + 
            "  value2:1\n" + 
            "  value3:9223372036854775807\n" + 
            "  value4:1.1\n" + 
            "  value5:1.7976931348623157E308\n" + 
            "  value6:5,5\n" + 
            "  value7:eins|zwei|drei\n" + 
            "  value8:2,2|20,20\n" + 
            "  value9:1=aaa|2=bbb|3=ccc\n" + 
            "  value10:rect1=1,1,1,1|rect2=1,10,61,71\n" + 
            "}", 
            configData.toString() 
        );
    }
    
    @Test
    public void testBuildSimpleConfigs() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );

        TestObject1 testObj = new TestObject1( "testObj" );
        testObj.value1 = "value1";
        testObj.value2 = "value2";
        testObj.number1 = 111;
        testObj.number2 = Long.MAX_VALUE;
        testObj.number3 = Float.MAX_VALUE;
        testObj.number4 = Double.MAX_VALUE;
        
        builder.build( testObj );
        
        ConfigData configData = context.getConfig( "testObj" );
        assertNotNull( configData );
        assertEquals( 
            "ConfigData: id:testObj {\n" + 
            "  value1:value1\n" + 
            "  otherNameForValue2:value2\n" + 
            "  number1:111\n" + 
            "  number2:9223372036854775807\n" + 
            "  number3:3.4028235E38\n" + 
            "  number4:1.7976931348623157E308\n" + 
            "}", 
            configData.toString() 
        );
    }
    
    @Test
    public void testEnumConfig() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );
        
        TestEnum testObj = new TestEnum();
        testObj.configId( "testObj" );
        testObj.dir1 = Direction.NORTH;
        testObj.dir2 = Direction.SOUTH;
        
        builder.build( testObj );
        
        ConfigData configData = context.getConfig( "testObj" );
        assertNotNull( configData );
        assertEquals( 
            "ConfigData: id:testObj {\n" + 
            "  dir1:NORTH\n" + 
            "  dir2:SOUTH\n" + 
            "}", 
            configData.toString() 
        );
    }
    
    @Test
    public void testMandatoryProperty() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );

        TestObjectMandatory testObj = new TestObjectMandatory( "testObj" );
        testObj.mandatoryValue = "mandatoryValueIsSet";
        
        builder.build( testObj );
        
        ConfigData configData = context.getConfig("testObj" );
        assertNotNull( configData );
        assertEquals( 
            "ConfigData: id:testObj {\n" + 
            "  mandatoryValue:mandatoryValueIsSet\n" + 
            "}", 
            configData.toString() 
        );
        
        context.clear();
        testObj.mandatoryValue = null;
        
        try {
            builder.build( testObj );
            fail( "this should throw a GlueException!" );
        } catch ( Exception e ) {
            assertEquals( 
                "The field: public java.lang.String com.inari.glue.test.TestObjectMandatory.mandatoryValue on instance: testObj is mandatory! but has null reference!", 
                e.getMessage() 
            );
        }
    }
    
    @Test
    public void testBuildConfigWithReference() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );

        TestObject1 testObj = new TestObject1( "testObj" );
        testObj.value1 = "value1";
        testObj.value2 = "value2";
        testObj.number1 = 111;
        testObj.number2 = Long.MAX_VALUE;
        testObj.number3 = Float.MAX_VALUE;
        testObj.number4 = Double.MAX_VALUE;
        
        testObj.reference1 = new TestObject2( "testObj2" );
        
        builder.build( testObj );
        
        ConfigData configData = context.getConfig( "testObj" );
        assertNotNull( configData );
        assertEquals( 
            "ConfigData: id:testObj {\n" + 
            "  value1:value1\n" + 
            "  otherNameForValue2:value2\n" + 
            "  number1:111\n" + 
            "  number2:9223372036854775807\n" + 
            "  number3:3.4028235E38\n" + 
            "  number4:1.7976931348623157E308\n" + 
            "  reference1:testObj2\n" + 
            "}", 
            configData.toString() 
        );
        
        ConfigData configData2 = context.getConfig( "testObj2" );
        assertNotNull( configData2 );
        assertEquals( 
            "ConfigData: id:testObj2 {\n" + 
            "}", 
            configData2.toString() 
        );
    }
    
    @Test
    public void testListReferences() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );

        TestObject3 testObj = new TestObject3( "testObj" );
        testObj.list = new ArrayList<Object>();
        testObj.list.add( "vaue1" );
        testObj.list.add( "vaue2" );
        testObj.list.add( "vaue3" );
        
        builder.build( testObj );
        
        ConfigData configData = context.getConfig( "testObj" );
        assertNotNull( configData );
        assertEquals( 
            "ConfigData: id:testObj {\n" + 
            "  list:vaue1|vaue2|vaue3\n" + 
            "}", 
            configData.toString() 
        );
        
        context.clear();
        testObj.list = new ArrayList<Object>();
        testObj.list.add( 1.1 );
        testObj.list.add( 2.2 );
        testObj.list.add( 3.3 );
        
        testObj.stringConfigurableList = new ArrayList<IStringConfigurable>();
        testObj.stringConfigurableList.add( new Position( 1, 1 )  );
        testObj.stringConfigurableList.add( new Position( 10, 10 )  );
        testObj.stringConfigurableList.add( new Position( 100, 100 )  );
        
        testObj.configObjectList = new ArrayList<IConfigObject>();
        testObj.configObjectList.add( new TestObject2( "listObjRef1" ) );
        testObj.configObjectList.add( new TestObject2( "listObjRef2" ) );
        testObj.configObjectList.add( new TestObject2( "listObjRef3" ) );
        
        builder.build( testObj );
        
        configData = context.getConfig( "testObj" );
        assertNotNull( configData );
        assertEquals( 
            "ConfigData: id:testObj {\n" + 
            "  list:1.1|2.2|3.3\n" + 
            "  stringConfigurableList:1,1|10,10|100,100\n" + 
            "  configObjectList:listObjRef1|listObjRef2|listObjRef3\n" + 
            "}", 
            configData.toString() 
        );
        
        ConfigData listObjRef1Data = context.getConfig( "listObjRef1" );
        assertNotNull( listObjRef1Data );
        assertEquals( 
            "ConfigData: id:listObjRef1 {\n" + 
            "}", 
            listObjRef1Data.toString() 
        );
         
    }
    
    @Test
    public void testMapReferences() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );
        
        TestObject3 testObj = new TestObject3( "testObj" );
        testObj.map = new LinkedHashMap<String, Object>();
        testObj.map.put( "prop1", "val1" );
        testObj.map.put( "prop2", "val2" );
        testObj.map.put( "prop3", "val3" );
        
        testObj.stringConfigurableMap = new LinkedHashMap<String, IStringConfigurable>();
        testObj.stringConfigurableMap.put( "point1", new Position( 1, 1 )  );
        testObj.stringConfigurableMap.put( "point2", new Position( 10, 10 )  );
        testObj.stringConfigurableMap.put( "point3", new Position( 100, 100 )  );
        
        testObj.configObjectMsp = new LinkedHashMap<String, IConfigObject>();
        testObj.configObjectMsp.put( "obj1", new TestObject2( "listObjRef1" ) );
        testObj.configObjectMsp.put( "obj2", new TestObject2( "listObjRef2" ) );
        testObj.configObjectMsp.put( "obj3", new TestObject2( "listObjRef3" ) );
        
        builder.build( testObj );
        
        ConfigData configData = context.getConfig( "testObj" );
        assertNotNull( configData );
        assertEquals( 
            "ConfigData: id:testObj {\n" + 
            "  map:prop1=val1|prop2=val2|prop3=val3\n" + 
            "  stringConfigurableMap:point1=1,1|point2=10,10|point3=100,100\n" + 
            "  configObjectMsp:obj1=listObjRef1|obj2=listObjRef2|obj3=listObjRef3\n" + 
            "}", 
            configData.toString() 
        );
        
        ConfigData listObjRef1Data = context.getConfig( "listObjRef1" );
        assertNotNull( listObjRef1Data );
        assertEquals( 
            "ConfigData: id:listObjRef1 {\n" + 
            "}", 
            listObjRef1Data.toString() 
        );
    }
    
    @Test
    public void testBuildSimple() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );
        
        TestObject1 obj1 = new TestObject1( "testObj1" );
        obj1.number1 = 1;
        obj1.number2 = 2L;
        obj1.number3 = 3.3F;
        obj1.number4 = 4.4D;
        obj1.value1 = "value1";
        obj1.value2 = "value2";
        
        builder.build( obj1 );
        
        ConfigData data = context.getConfig( obj1 );
        assertNotNull( data );
        assertEquals( 
            "ConfigData: id:testObj1 {\n" + 
            "  value1:value1\n" + 
            "  otherNameForValue2:value2\n" + 
            "  number1:1\n" + 
            "  number2:2\n" + 
            "  number3:3.3\n" + 
            "  number4:4.4\n" + 
            "}", 
            data.toString() 
        );
        
        try {
            builder.build( obj1 );
            fail( "this should throw a GlueException!" );
        } catch ( GlueException ge ) {
            assertEquals( "Configuration with id: testObj1 already exists!", ge.getMessage() );
        }
    }
    
    @Test
    public void testBuildWithReferences() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );
        
        TestObject2 obj1 = new TestObject2( "testObj1" );
        TestObject2 obj2 = new TestObject2( "testObj2" );
        TestObject2 obj3 = new TestObject2( "testObj3" );
        
        obj1.reference1 = obj2;
        obj1.reference2 = obj3;
        
        builder.build( obj1 );
        
        assertTrue( context.hasConfig( "testObj1" ) );
        assertTrue( context.hasConfig( "testObj2" ) );
        assertTrue( context.hasConfig( "testObj3" ) );
        
        ConfigData data1 = context.getConfig( obj1 );
        assertNotNull( data1 );
        assertEquals( 
            "ConfigData: id:testObj1 {\n" + 
            "  reference1:testObj2\n" + 
            "  reference2:testObj3\n" + 
            "}", 
            data1.toString() 
        );
        
        ConfigData data2 = context.getConfig( obj2 );
        assertNotNull( data2 );
        assertEquals( 
            "ConfigData: id:testObj2 {\n" + 
            "}", 
            data2.toString() 
        );
        
        ConfigData data3 = context.getConfig( obj3 );
        assertNotNull( data3 );
        assertEquals( 
            "ConfigData: id:testObj3 {\n" + 
            "}", 
            data3.toString() 
        );
    }
    
    @Test
    public void testBuildWithReferenceCircle() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );
        
        TestObject2 obj1 = new TestObject2( "testObj1" );
        TestObject2 obj2 = new TestObject2( "testObj2" );
        TestObject2 obj3 = new TestObject2( "testObj3" );
        
        obj1.reference1 = obj2;
        obj1.reference2 = obj3;
        
        obj2.reference1 = obj3;
        obj3.reference1 = obj1;
        
        builder.build( obj1 );
        
        assertTrue( context.hasConfig( "testObj1" ) );
        assertTrue( context.hasConfig( "testObj2" ) );
        assertTrue( context.hasConfig( "testObj3" ) );
        
        ConfigData data1 = context.getConfig( obj1 );
        assertNotNull( data1 );
        assertEquals( 
            "ConfigData: id:testObj1 {\n" + 
            "  reference1:testObj2\n" + 
            "  reference2:testObj3\n" + 
            "}", 
            data1.toString() 
        );
        
        ConfigData data2 = context.getConfig( obj2 );
        assertNotNull( data2 );
        assertEquals( 
            "ConfigData: id:testObj2 {\n" + 
            "  reference1:testObj3\n" + 
            "}", 
            data2.toString() 
        );
        
        ConfigData data3 = context.getConfig( obj3 );
        assertNotNull( data3 );
        assertEquals( 
            "ConfigData: id:testObj3 {\n" + 
            "  reference1:testObj1\n" + 
            "}", 
            data3.toString() 
        );
    }
    
    @Test
    public void testBuildWithComplex() {

        // TODO
        
    }
    
    @Test
    public void testBuildWithComplexReferences() {
        
        // TODO
        
    }
    
    private ConfigBuilder createConfigBuilder() {
        GlueService service = new GlueServiceImpl();
        GlueContext context = service.createContext( "Test" );
        ConfigBuilder builder = new ConfigBuilder( context );
        return builder;
    }

}
