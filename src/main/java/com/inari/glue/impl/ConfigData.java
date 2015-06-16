/**
* Copyright (c) 2009-2013, Andreas Hefti, anhefti@yahoo.de 
* All rights reserved.
*
* This software is licensed to you under the Apache License, Version 2.0
* (the "License"); You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* . Redistributions of source code must retain the above copyright notice, this
* list of conditions and the following disclaimer.
*
* . Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* . Neither the name "InariGlue" nor the names of its contributors may be
* used to endorse or promote products derived from this software without
* specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package com.inari.glue.impl;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.inari.glue.GlueException;
import com.inari.commons.StringUtils;
import com.inari.commons.config.IConfigObject;
import com.inari.commons.config.IStringConfigurable;
import com.inari.commons.lang.TypedKey;

public class ConfigData extends LinkedHashMap<String, String> {
    
private static final long serialVersionUID = 8828190017974356696L;
    
    private String id;
    private Class<? extends IConfigObject> type;
    
    
    public ConfigData( String id, Class<? extends IConfigObject> type ) {
        this.id = id;
        this.type = type;
    }
    
    public String id() {
        return id;
    }
    
    public boolean is( String name ) {
        return "true".equals( get( name ) );
    }
    
    public Class<? extends IConfigObject> type() {
        return type;
    }
    
    public final IConfigObject newInstance() {
        try {
            Constructor<? extends IConfigObject> constructor = type.getDeclaredConstructor();
            boolean isAccessible = constructor.isAccessible();
            if ( !isAccessible ) {
                constructor.setAccessible( true );
            }
            constructor.setAccessible( true );
            
            IConfigObject instance = constructor.newInstance();
            instance.configId( id );
            
            if ( !isAccessible ) {
                constructor.setAccessible( false );
            }
            
            return instance;
        } catch ( Exception e ) {
            throw new GlueException( "Failed to create new instance of Class: " + type + "!", e );
        }
    }
    
    public int getInt( String name ) {
        try {
            return Integer.parseInt( get( name ) );
        } catch ( Exception e ) {
            throw new GlueException( "Failed to convert Parameter with name: " + name + "!", e );
        }
    }
    
    public long getLong( String name ) {
        try {
            return Long.parseLong( get( name ) );
        } catch ( Exception e ) {
            throw new GlueException( "Failed to convert Parameter with name: " + name + "!", e );
        }
    }
    
    public float getFloat( String name ) {
        try {
            return Float.parseFloat( get( name ) );
        } catch ( Exception e ) {
            throw new GlueException( "Failed to convert Parameter with name: " + name + "!", e );
        }
    }
    
    public double getDouble( String name ) {
        try {
            return Double.parseDouble( get( name ) );
        } catch ( Exception e ) {
            throw new GlueException( "Failed to convert Parameter with name: " + name + "!", e );
        }
    }
    
    public byte getByte( String name ) {
        try {
            return Byte.parseByte( get( name ) );
        } catch ( Exception e ) {
            throw new GlueException( "Failed to convert Parameter with name: " + name + "!", e );
        }
    }

    public String getMandatory( String name ) {
        String value = super.get( name );
        if ( value == null ) {
            throw new GlueException( "No Parameter with name: " + name + " found for Config: " + id );  
        }
        return value;
    }
    
    public String getOptional( String name ) {
        return getOptional( name, null );
    }
    
    public String getOptional( String name, String defValue ) {
        if ( super.containsKey( name ) ) {
            return super.get( name );
        }
        return defValue;
    }
    
    public Collection<String> getCollection( String key ) {
        String value = get( key );
        if ( value == null ) {
            return null;
        }
        if ( StringUtils.isBlank( value ) ) {
            return new ArrayList<String>();
        }
        return StringUtils.split( value, StringUtils.LIST_VALUE_SEPARATOR_STRING );
    }
    
    public Map<String, String> getMap( String key ) {
        String value = super.get( key );
        if ( value == null ) {
            return null;
        }
        if ( StringUtils.isBlank( value ) ) {
            return new HashMap<String, String>();
        }
        return StringUtils.splitToMap( value, StringUtils.LIST_VALUE_SEPARATOR_STRING, StringUtils.KEY_VALUE_SEPARATOR_STRING );
    }
    
    public void put( String key, int value ) {
        put( key, String.valueOf( value ) );
    }
    
    public void put( String key, long value ) {
        put( key, String.valueOf( value ) );
    }
    
    public void put( String key, float value ) {
        put( key, String.valueOf( value ) );
    }
    
    public void put( String key, boolean value ) {
        put( key, String.valueOf( value ) );
    }
    
    public void put( String key, TypedKey<?> valueKey ) {
        put( key, valueKey.id() );
    }
    
    public void put( String key, IConfigObject value ) {
        if ( value != null ) {
            put( key, value.configId() );
        }
    }
    
    public void putCollection( String key, Collection<?> values ) {
        if ( values == null ) {
            put( key, (String) null );
            return;
        }
        if ( values.isEmpty() ) {
            put( key, "" );
            return;
        }
        StringBuilder builder = new StringBuilder();
        for ( Object value : values ) {
            appendValue( builder, value );
            builder.append( StringUtils.LIST_VALUE_SEPARATOR );
        }
        builder.deleteCharAt( builder.length() - 1 );
        put( key, builder.toString() );
    }
    
    public void putMap( String key, Map<String, ?> values ) {
        if ( values == null ) {
            put( key, (String) null );
            return;
        }
        if ( values.isEmpty() ) {
            put( key, "" );
            return;
        }
        StringBuilder builder = new StringBuilder();
        for ( Map.Entry<?, ?> entry : values.entrySet() ) {
            builder.append( entry.getKey() );
            builder.append( StringUtils.KEY_VALUE_SEPARATOR );
            appendValue( builder, entry.getValue() );
            builder.append( StringUtils.LIST_VALUE_SEPARATOR );
        }
        builder.deleteCharAt( builder.length() - 1 );
        put( key, builder.toString() );
    }
    
    protected void appendValue( StringBuilder builder, Object value ) {
        if ( value instanceof IConfigObject ) {
            String configId = ( (IConfigObject) value ).configId();
            if ( configId != null ) {
                builder.append( configId );
                return;
            }
            if ( value instanceof IStringConfigurable ) {
                appendStringConfigurableValue( builder, value );
                return;
            }
            throw new GlueException( "Inherited Value Excpetion: The value: " + value + " has no configId and is not of type IStringConfigurable!" );
        } else if ( value instanceof IStringConfigurable ) {
            appendStringConfigurableValue( builder, value );
        } else {
            builder.append( String.valueOf( value ) );
        }
    }
    
    private void appendStringConfigurableValue( StringBuilder builder , Object value ) {
        String stringValue = ( (IStringConfigurable) value ).toConfigString();
        stringValue = StringUtils.escapeSeparatorKeys( stringValue );
        builder.append( stringValue );
    }
    
    public String toString( String pre ) {
        StringBuilder builder = new StringBuilder();
        builder.append( pre ).append( "ConfigData: id:" ).append( id ).append( " {\n" );
        for ( Map.Entry<String, String> entry : entrySet() ) {
            builder.append( pre ).append( "  " ).append( entry.getKey() ).append( ":" ).append( entry.getValue() ).append( "\n" );
        }
        builder.append( pre ).append( "}" );
        return builder.toString();
    }
    
    @Override
    public String toString() {
        return toString( "" );
    }

    

}
