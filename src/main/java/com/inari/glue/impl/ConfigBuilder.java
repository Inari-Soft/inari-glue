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
import java.util.Collection;
import java.util.Map;

import com.inari.glue.GlueContext;
import com.inari.glue.GlueException;
import com.inari.commons.StringUtils;
import com.inari.commons.config.Configured;
import com.inari.commons.config.ConfigObject;
import com.inari.commons.config.StringConfigurable;

public class ConfigBuilder {
    
    private GlueContext context;
    
    ConfigBuilder( GlueContext context ) {
        this.context = context;
    }
    
    public void build( ConfigObject instance ) {
        String id = instance.configId();
        
        if ( id == null ) {
            throw new GlueException( "The ConfigObject has no Id!" );
        }
        
        if ( context.hasConfig( id ) ) {
            throw new GlueException( "Configuration with id: " + id + " already exists!" );
        }
        
        ConfigData config = context.createConfig( id, instance.getClass() );
        
        // get data from annotated fields
        Collection<Field> fields = GlueUtils.getAllConfiguredFields( instance );
        for ( Field field : fields ) {
            buildFieldValue( field, instance, config );
        }
    }

    protected void buildFieldValue( Field field, ConfigObject instance, ConfigData configData ) {
        Configured configProperty = field.getAnnotation( Configured.class );
        if ( configProperty == null ) {
            return;
        }
        
        boolean mandatory = configProperty.required();
        String valueName = configProperty.name();
        if ( StringUtils.isBlank( valueName ) ) {
            valueName = field.getName();
        }
        
        Object value = null;
        boolean accessible = field.isAccessible();
        field.setAccessible( true );
        try {
            value = field.get( instance );
        } catch ( Exception e ) {
            throw new GlueException( "Failed to get value of field: " + field + " on IConfigObject: " + instance, e );
        } finally {
            field.setAccessible( accessible );
        }

        if ( value == null ) {
            if ( mandatory ) {
                throw new GlueException( "The field: " + field + " on instance: " + instance.configId() + " is mandatory! but has null reference!" );
            }
            return;
        }
        
        if ( ConfigObject.class.isAssignableFrom( field.getType() ) ) {
            buildReference( valueName, (ConfigObject) value, configData );
        } else {
            buildProperty( valueName, value, configData );
        }
    }
    
    protected void buildReference( String name, ConfigObject value, ConfigData configData ) {
        if ( value == null ) {
            return;
        }
        
        String configDataId = value.configId();
        if ( !context.hasConfig( configDataId ) ) {
            build( value );
        }
        configData.put( name, configDataId );

    }

    @SuppressWarnings("unchecked")
    protected void buildProperty( String name, Object value, ConfigData configData ) {
        if ( value == null ) {
            return;
        }
        
        if ( value instanceof Collection ) {
            
            Collection<?> values = (Collection<?>) value;
            checkAndBuildAll( values );
            
            configData.putCollection( name, values );
            return;
        }
        
        if ( value instanceof Map ) {
            
            checkAndBuildAll( ( (Map<String, ?>) value ).values() );
            
            configData.putMap( name, (Map<String, ?>) value );
            return;
        }
        
        if ( value instanceof StringConfigurable ) {
            configData.put( name, ( (StringConfigurable) value ).toConfigString() );
            return;
        }
        
        configData.put( name, value.toString() );
    }

    private void checkAndBuildAll( Collection<?> values ) {
        for ( Object val : values ) {
            if ( val instanceof ConfigObject ) {
                ConfigObject configObj = (ConfigObject) val;
                if ( configObj.configId() != null ) {
                    build( configObj );
                } 
            }
        }
    }

}
