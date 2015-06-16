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


import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.inari.glue.GlueException;
import com.inari.glue.IConfigObjectFactory;
import com.inari.commons.StringUtils;
import com.inari.commons.config.Configured;
import com.inari.commons.config.ConfigObject;
import com.inari.commons.config.StringConfigurable;

public class InstanceFactory {

    private GlueContextImpl context;
    
    InstanceFactory( GlueContextImpl context ) {
        this.context = context;
    }

    public void build( String id ) throws GlueException {
        if ( context == null ) {
            throw new GlueException( "No Context ( NULL reference )!" );
        }
        
        ConfigData config = context.getConfig( id );
        if ( config == null ) {
            throw new GlueException( "No ConfigData for id: " + id + " exists for context: " + context.name() + "!" );
        }

        ConfigObject instance = context.newInstance( id );
        
        Collection<Field> fields = GlueUtils.getAllConfiguredFields( instance );
        for ( Field field : fields ) {
            setFieldValue( field, instance, config );
        }
        
        @SuppressWarnings("unchecked")
        IConfigObjectFactory<ConfigObject> instanceFactory = (IConfigObjectFactory<ConfigObject>) context.getInstanceFactory( instance.getClass() );
        if ( instanceFactory != null ) {
            instanceFactory.build( instance );
        }
    }
    
    

    protected void setFieldValue( Field field, ConfigObject instance, ConfigData configData ) {
        Configured configProperty = field.getAnnotation( Configured.class );
        if ( configProperty == null ) {
            return;
        }
        
        boolean mandatory = configProperty.required();
        String referenceId = configProperty.name();
        if ( StringUtils.isBlank( referenceId ) ) {
            referenceId = field.getName();
        }
        if ( mandatory ) {
            if ( !configData.containsKey( referenceId ) ) {
                throw new GlueException( "Configuration failure: No Configuration parameter with name: " + referenceId + " found in configuration data: " + configData.id() + " for instance-type: " + instance.getClass() + " and field: " + field );
            }
        }
        if ( !configData.containsKey( referenceId ) ) {
            return;
        }
        
        if ( ConfigObject.class.isAssignableFrom( field.getType() ) ) {
            String reference = configData.get( referenceId );
            if ( 
                StringConfigurable.class.isAssignableFrom( field.getType() ) && 
                !configData.containsKey( reference ) 
            ) {
                
                setProperty( configProperty, field, instance, configData, referenceId );
                return;
            }
            
            setReference( field, instance, reference );
            return;
        } 
        
        setProperty( configProperty, field, instance, configData, referenceId );
    }
    
    protected void setReference( Field field, ConfigObject instance, String configDataId ) {
        Class<?> type = field.getType();
        if ( !ConfigObject.class.isAssignableFrom( type ) ) {
            throw new GlueException( "Configuration Annotation failure: A ConfigReference annotated field must be of type IConfigObject! instance: " + instance + " field: " + field ); 
        }
        
        ConfigObject value = context.getInstance( configDataId );
        boolean accessible = field.isAccessible();
        field.setAccessible( true );
        try { 
            field.set( instance, value );
        } catch ( IllegalAccessException iae ) {
            throw new GlueException( "Failed to access field: " + field, iae );
        } finally {
            field.setAccessible( accessible );
        }
    }
    
    protected void setProperty( Configured configured, Field field, ConfigObject instance, ConfigData configData, String configDataId ) {
        Class<?> type = field.getType();
        if ( configured.type() != Object.class ) {
            type = configured.type();
        }
        Object value = null;
        Type genericFieldType = field.getGenericType();
        
        if ( genericFieldType instanceof ParameterizedType ) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for ( int i = 0; i < fieldArgTypes.length; i++ ) {
                if ( fieldArgTypes[ i ] instanceof ParameterizedType ) {
                    fieldArgTypes[ i ] = ( (ParameterizedType) fieldArgTypes[ i ] ).getRawType();
                }
            }
            
            value = getTypedPropertyValue( configDataId, configData, type, fieldArgTypes );
        } else {
            String stringValue = configData.get( configDataId );
            value = getPropertyValue( type, stringValue );
        }
        
        boolean accessible = field.isAccessible();
        field.setAccessible( true );
        try { 
            field.set( instance, value );
        } catch ( IllegalAccessException iae ) {
            throw new GlueException( "Failed to access field: " + field, iae );
        } finally {
            field.setAccessible( accessible );
        }
    }
    
    protected Object getTypedPropertyValue( String configDataId, ConfigData configData, Class<?> type, Type... genTypes ) {
        
        if ( Collection.class.isAssignableFrom( type ) ) {
            Collection<String> stringCollection = configData.getCollection( configDataId );
            if ( stringCollection == null ) {
                return null;
            }
            try {
                Class<?> genType = (Class<?>) genTypes[ 0 ];
                @SuppressWarnings("unchecked")
                Collection<Object> typeInstance = (Collection<Object>) type.newInstance();
                for ( String stringValue : stringCollection ) {
                    Object value = getPropertyValue( genType, stringValue );
                    typeInstance.add( value );
                }
                return typeInstance;
            } catch ( Exception e ) {
                throw new GlueException( "Failed to create value instance for " + configDataId + " type: " + type, e );
            }
        }
        
        if ( Map.class.isAssignableFrom( type ) ) {
            Map<String, String> stringMap = configData.getMap( configDataId );
            if ( stringMap == null ) {
                return null;
            }
            try {
                Class<?> genKeyType = (Class<?>) genTypes[ 0 ];
                Class<?> genValueType = (Class<?>) genTypes[ 1 ];
                @SuppressWarnings("unchecked")
                Map<Object, Object> typeInstance = (Map<Object, Object>) type.newInstance();
                for ( Map.Entry<String, String> stringEntry : stringMap.entrySet() ) {
                    Object key = getPropertyValue( genKeyType, stringEntry.getKey() );
                    Object value = getPropertyValue( genValueType, stringEntry.getValue() );
                    typeInstance.put( key, value );
                }
                return typeInstance;
            } catch ( Exception e ) {
                throw new GlueException( "Failed to create value instance for type: " + type, e );
            }
        }
        
        String stringValue = configData.get( configDataId );
        return getPropertyValue( type, stringValue );
    }
    
    
    protected Object getPropertyValue( Class<?> type, String value ) {
        // for primitive types
        if ( value == null ) {
            return null;
        }
        if ( String.class.isAssignableFrom( type ) ) {
            return value;
        }
        if ( Integer.class.isAssignableFrom( type ) || int.class.isAssignableFrom( type ) ) {
            return Integer.valueOf( value );
        }
        if ( Long.class.isAssignableFrom( type ) || long.class.isAssignableFrom( type ) ) {
            return Long.valueOf( value );
        }
        if ( Float.class.isAssignableFrom( type ) || float.class.isAssignableFrom( type ) ) {
            return Float.valueOf( value );
        }
        if ( Double.class.isAssignableFrom( type ) || double.class.isAssignableFrom( type ) ) {
            return Double.valueOf( value );
        }
        if ( Byte.class.isAssignableFrom( type ) || byte.class.isAssignableFrom( type ) ) {
            return Byte.valueOf( value );
        }
        if ( Boolean.class.isAssignableFrom( type ) || boolean.class.isAssignableFrom( type ) ) {
            return Boolean.valueOf( value );
        }
        // for enums
        if ( Enum.class.isAssignableFrom( type ) ) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Object enumValue = Enum.valueOf( (Class<? extends Enum>) type, value );
            return enumValue;
        }
        // for IConfigObject references that has a valid configuration
        if ( context.hasConfig( value ) ) {
            return context.getInstance( value );
        }
        // for IStringConfigurable types 
        if ( StringConfigurable.class.isAssignableFrom( type ) ) {
            return getStringConfigurable( type, value );
        }

        throw new GlueException( "Not able to create value ( " + value + " ) for property type:" + type );
    }
    
    private Object getStringConfigurable( Class<?> type, String value ) {
        try {
            StringConfigurable valueInstance = (StringConfigurable) type.newInstance(); 
            valueInstance.fromConfigString( value );
            return valueInstance;
        } catch ( Exception e ) {
            throw new GlueException( "Failed to create value instance for type: " + type, e );
        }
    }

}
