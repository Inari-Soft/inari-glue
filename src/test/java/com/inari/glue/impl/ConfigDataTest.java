package com.inari.glue.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.inari.glue.GlueException;
import com.inari.glue.test.TestObject1;
import com.inari.commons.config.IConfigObject;
import com.inari.commons.config.IStringConfigurable;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;

public class ConfigDataTest {
    
    @Test
    public void testConfigData() {
        ConfigData cData = new ConfigData( "test", IConfigObject.class );
        
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "}", 
            cData.toString() 
        );
        
        cData.put( "stringValue", "A String Value" );
        cData.put( "Integer", "4" );
        cData.put( "Long", "67567567" );
        cData.put( "Float", "6.6" );
        cData.put( "Double", "4574.46746" );
        cData.put( "Byte", "5" );
        
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  stringValue:A String Value\n" + 
            "  Integer:4\n" + 
            "  Long:67567567\n" + 
            "  Float:6.6\n" + 
            "  Double:4574.46746\n" + 
            "  Byte:5\n" + 
            "}", 
            cData.toString() 
        );
        
        assertEquals( "A String Value", cData.get( "stringValue" ) );
        assertEquals( 4, cData.getInt( "Integer" ) );
        assertEquals( 67567567, cData.getLong( "Long" ) );
        assertEquals( Float.valueOf( 6.6f ), Float.valueOf( cData.getFloat( "Float" ) ) );
        assertEquals( Double.valueOf( 4574.46746d ), Double.valueOf( cData.getDouble( "Double" ) ) );
        assertEquals( Byte.valueOf( "5" ), Byte.valueOf( cData.getByte( "Byte" ) ) );
    }
    
    @Test
    public void testGetOptional() {
        ConfigData cData = new ConfigData( "test", IConfigObject.class );
        cData.put( "stringValue", "A String Value" );
        
        assertEquals( "A String Value", cData.getOptional( "stringValue", "defaultValue" ) );
        assertEquals( "defaultValue", cData.getOptional( "notDefined", "defaultValue" ) );
        assertNull( cData.getOptional( "notDefined" ) );
    }
    
    @Test
    public void testGetMandatory() {
        ConfigData cData = new ConfigData( "test", IConfigObject.class );
        cData.put( "stringValue", "A String Value" );
        
        assertEquals( "A String Value", cData.getMandatory( "stringValue" ) );
        try {
            cData.getMandatory( "notDefined" );
            fail( "getMandatory should throw an exception of value do not exist on ConfigData map!" );
        } catch ( GlueException ge ) {
            assertEquals( "No Parameter with name: notDefined found for Config: test", ge.getMessage() );
        }
    }
    
    @Test
    public void testPutAndGetCollection() {
        ConfigData cData = new ConfigData( "test", IConfigObject.class );
        Collection<String> list1 = new ArrayList<String>();
        
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "}", 
            cData.toString() 
        );
        assertNull( cData.getCollection( "list1" ) );
        
        cData.putCollection( "list1", null );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  list1:null\n" + 
            "}", 
            cData.toString() 
        );
        assertNull( cData.getCollection( "list1" ) );
        
        cData.putCollection( "list1", list1 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  list1:\n" + 
            "}", 
            cData.toString() 
        );
        assertEquals( list1, cData.getCollection( "list1" ) );
        
        list1.add( "value1" );
        
        cData.putCollection( "list1", list1 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  list1:value1\n" + 
            "}", 
            cData.toString() 
        );
        assertEquals( list1, cData.getCollection( "list1" ) );
        
        list1.add( "value2" );
        list1.add( "value3" );
        
        cData.putCollection( "list1", list1 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  list1:value1|value2|value3\n" + 
            "}", 
            cData.toString() 
        );
        assertEquals( list1, cData.getCollection( "list1" ) );
        
        // a collection of IConfigObject instances
        cData.clear();
        Collection<IConfigObject> list2 = new ArrayList<IConfigObject>();
        list2.add( new TestObject1( "configObject1" ) );
        list2.add( new TestObject1( "configObject2" ) );
        
        cData.putCollection( "list2", list2 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  list2:configObject1|configObject2\n" + 
            "}", 
            cData.toString() 
        );
        
        // a collection if IStringConfigurable instances
        cData.clear();
        Collection<IStringConfigurable> list3 = new ArrayList<IStringConfigurable>();
        list3.add( new Position( 10, 10 ) );
        list3.add( new Rectangle( 10, 10, 100, 100 ) );
        
        cData.putCollection( "list3", list3 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  list3:10,10|10,10,100,100\n" + 
            "}", 
            cData.toString() 
        );
        
        Collection<String> _list3 = cData.getCollection( "list3" );
        assertNotNull( _list3 );
        assertEquals( 2, _list3.size() );
        
    }
    
    @Test
    public void testPutGetMap() {
        ConfigData cData = new ConfigData( "test", IConfigObject.class );
        Map<String, String> map1 = new LinkedHashMap<String, String>();
        
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "}", 
            cData.toString() 
        );
        assertNull( cData.getCollection( "map1" ) );
        
        cData.putMap( "map1", null );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  map1:null\n" + 
            "}", 
            cData.toString() 
        );
        assertNull( cData.getMap( "map1" ) );
        
        cData.putMap( "map1", map1 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  map1:\n" + 
            "}", 
            cData.toString() 
        );
        assertEquals( map1, cData.getMap( "map1" ) );
        
        map1.put( "value1", "value1" );
        
        cData.putMap( "map1", map1 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  map1:value1=value1\n" + 
            "}", 
            cData.toString() 
        );
        assertEquals( map1, cData.getMap( "map1" ) );
        
        map1.put( "value2", "value2" );
        map1.put( "value3", "value3" );
        
        cData.putMap( "map1", map1 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  map1:value1=value1|value2=value2|value3=value3\n" + 
            "}", 
            cData.toString() 
        );
        assertEquals( map1, cData.getMap( "map1" ) );
        
        // a map of IConfigObject instances
        cData.clear();
        Map<String, IConfigObject> map2 = new LinkedHashMap<String, IConfigObject>();
        map2.put( "value1", new TestObject1( "configObject1" ) );
        map2.put( "value2", new TestObject1( "configObject2" ) );
        
        cData.putMap( "map2", map2 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  map2:value1=configObject1|value2=configObject2\n" + 
            "}", 
            cData.toString() 
        );
        
        // a collection if IStringConfigurable instances
        cData.clear();
        Map<String, IStringConfigurable> map3 = new LinkedHashMap<String, IStringConfigurable>();
        map3.put( "value1", new Position( 10, 10 ) );
        map3.put( "value2", new Rectangle( 10, 10, 100, 100 ) );
        
        cData.putMap( "map3", map3 );
        assertEquals( 
            "ConfigData: id:test {\n" + 
            "  map3:value1=10,10|value2=10,10,100,100\n" + 
            "}", 
            cData.toString() 
        );
    }
    
    @Test
    public void testAppendValue() {
        // TODO
    }
    


}
