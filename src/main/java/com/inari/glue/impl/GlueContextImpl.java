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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.inari.glue.GlueContext;
import com.inari.glue.GlueException;
import com.inari.glue.IConfigObjectFactory;
import com.inari.commons.config.ConfigObject;
import com.inari.commons.lang.TypedKey;

public class GlueContextImpl implements GlueContext {
    
    private static final AtomicInteger GENERIC_CONFIG_KEY_COUNT = new AtomicInteger();
    
    private String name;

    
    private Map<String, Alias> aliasMap = new HashMap<String, Alias>();
    private Map<Class<? extends ConfigObject>, Alias> aliasByType = new HashMap<Class<? extends ConfigObject>, Alias>();
    
    private Map<String, ConfigData> configs = new HashMap<String, ConfigData>();
    
    private Map<String, ConfigObject> instances = new HashMap<String, ConfigObject>();
    private Map<Class<?>, IConfigObjectFactory<?>> factories = new  HashMap<Class<?>, IConfigObjectFactory<?>>();
    
    GlueContextImpl( String name ) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }
    
    
    
    @SuppressWarnings("unchecked")
    public Alias defineAlias( String typeName, String type ) throws GlueException {
        Class<? extends ConfigObject> typeClass;
        try {
            typeClass = (Class<? extends ConfigObject>) Class.forName( type );
            return defineAlias( typeName, typeClass );
        } catch ( ClassNotFoundException cnfe ) {
            throw new GlueException( "No type: " + type + " found on classpath!", cnfe );
        } catch ( ClassCastException cce ) {
            throw new GlueException( "The type: " + type + " is not a valid IConfigObject type!", cce );
        }
    }

    @Override
    public Alias defineAlias( String typeName, Class<? extends ConfigObject> type ) throws GlueException {
        if ( aliasMap.containsKey( typeName ) ) {
            throw new GlueException( "There is already a TypeDef for type-name: " + typeName + " registerd!" );
        }
        
        if ( aliasByType.containsKey( type ) ) {
            throw new GlueException( "There is already a TypeDef for type: " + type + " registered" );
        }
            
        Alias typeDef = new Alias( typeName, type );
        aliasMap.put( typeName, typeDef );
        aliasByType.put( type, typeDef );
        return typeDef;
    }

    public Alias getAlias( String typeName ) {
        return aliasMap.get( typeName );
    }

    public Alias getAlias( Class<? extends ConfigObject> type ) {
        return aliasByType.get( type );
    }

    public Collection<Alias> getAlias() {
        return aliasMap.values();
    }
    
    public void removeAlias( Class<? extends ConfigObject> type ) {
        Alias toRemove = aliasByType.remove( type );
        if ( toRemove != null ) {
            aliasMap.remove( toRemove.alias() );
        }
    }
    
    
    
    

    @Override
    public Collection<String> getAllConfigIds() {
        return configs.keySet();
    }
    
    @Override
    public ConfigData createConfig( TypedKey<? extends ConfigObject> key ) throws GlueException {
        return createConfig( key.id(), key.type() );
    }

    @Override
    public ConfigData createConfig( String id, Class<? extends ConfigObject> type ) throws GlueException {
        // TODO allow override!?
//        if ( configs.containsKey( id ) ) {
//            throw new GlueException( "There is already a Config with id: " + id + "!" );
//        }
        
        ConfigData config = new ConfigData( id, type );
        
        configs.put( id, config );
        return config;
    }

    @Override
    public ConfigData getOrCreateConfig( String id, Class<? extends ConfigObject> type ) {
        if ( configs.containsKey( id ) ) {
            return configs.get( id );
        }
        return createConfig( id, type );
    }

    @Override
    public ConfigData getConfig( String id ) {
        return configs.get( id );
    }

    @Override
    public ConfigData getConfig( ConfigObject obj ) {
        return configs.get( obj.configId() );
    }
    
    public Collection<ConfigData> getConfigs() {
        return configs.values();
    }

    @Override
    public boolean hasConfig( String configId ) {
        return configs.containsKey( configId );
    }

    @Override
    public void clearConfig( String id ) {
        configs.remove( id );
    }
    
    public final String getNextConfigId() {
        return name + "_" + GENERIC_CONFIG_KEY_COUNT.get();
    }

    @Override
    public <T extends ConfigObject> T getInstance( TypedKey<T> key ) {
        if ( key == null ) {
            return null;
        }
        ConfigObject untypedInstance = getUntypedInstance( key.id() );
        if ( untypedInstance == null ) {
            return null;
        }
        return key.cast( untypedInstance );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ConfigObject> T getInstance( String id )  {
        return (T) getUntypedInstance( id );
    }
    
    public ConfigObject getUntypedInstance( String id ) throws GlueException {
        if ( id == null ) {
            return null;
        }
        
        if ( instances.containsKey( id ) ) {
            return instances.get( id );
        }
        
        if ( configs.containsKey( id ) ) {
            ( new InstanceFactory( this ) ).build( id );
            return  instances.get( id );
        }
        
        throw new GlueException( "No Configuration with id: " + id + " found for context: " + name );
    }

    @Override
    public boolean hasInstance( String id ) {
        return instances.containsKey( id );
    }
    
    @SuppressWarnings("unchecked")
    public <T extends ConfigObject> T newInstance( String id ) {
        if ( !configs.containsKey( id ) ) {
            throw new GlueException( "No Configuration with id:" + id + " found for context: " + name );
        }
        
        if ( instances.containsKey( id ) ) {
            throw new GlueException( "The instance with id: " + id + " exists already!" );
        }
        
        ConfigData config = configs.get( id );
        ConfigObject instance = config.newInstance();
        instances.put( id, instance );
        return (T) instance;
    }
    
    @Override
    public void removeInstance( String id ) {
        instances.remove( id );
    }

    @Override
    public void clearInstances() {
        instances.clear();
    }

    
    @Override
    public void registerInstanceFactory( IConfigObjectFactory<?> factory ) {
        if ( factory == null ) {
            return;
        }
        factories.put( factory.type(), factory );
    }

    @Override
    public void unregisterInstanceFactory( Class<? extends ConfigObject> type ) {
        factories.remove( type );
    }
    
    protected IConfigObjectFactory<?> getInstanceFactory( Class<? extends ConfigObject> type ) {
        if ( type == null ) {
            return null;
        }
        IConfigObjectFactory<?> factory = factories.get( type );
        if ( factory != null ) {
            return factory;
        }
        
        Class<?> superclass = type.getSuperclass();
        if ( ( superclass != null ) && ConfigObject.class.isAssignableFrom( superclass ) ) {
            @SuppressWarnings("unchecked")
            Class<? extends ConfigObject> next = (Class<? extends ConfigObject>) superclass;
            return getInstanceFactory( next );
        }
        
        return null;
    }

    @Override
    public void clearInstanceFactories() {
        factories.clear();
    }

    @Override
    public void clear() {
        instances.clear();
        configs.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "GlueContextImpl: name=" ).append( name ).append( " [\n" );
        for ( Alias typedef : aliasMap.values() ) {
            builder.append( "  " ).append( typedef ).append( "\n" );
        }
        for ( ConfigData configData : configs.values() ) {
            builder.append( "  " ).append( configData.toString( "  " ) ).append( "\n" );
        }
        builder.append( "]" );
        return builder.toString();
    }

}
