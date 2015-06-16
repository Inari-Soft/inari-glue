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

import com.inari.glue.GlueContext;
import com.inari.glue.GlueException;
import com.inari.glue.GlueService;
import com.inari.commons.config.IConfigObject;

public class GlueServiceImpl implements GlueService {

    private Map<String, GlueContext> contexts = new HashMap<String, GlueContext>();


    @Override
    public GlueContext createContext( String name ) throws GlueException {
        if ( contexts.containsKey( name ) ) {
            throw new GlueException( "There is already a GlueContext with the name: " + name + "!" );
        }
        GlueContext context = new GlueContextImpl( name );
        contexts.put( name, context );
        return context;
    }

    @Override
    public boolean hasContext( String name ) {
        return contexts.containsKey( name );
    }

    @Override
    public GlueContext getContext( String name ) {
        return contexts.get( name );
    }

    @Override
    public Collection<GlueContext> getContexts() {
        return contexts.values();
    }

    @Override
    public void addConfig( GlueContext context, IConfigObject obj ) throws GlueException {
        if ( context == null ) {
            throw new GlueException( "IGlueContext has null reference!" );
        }
        if ( obj == null ) {
            throw new GlueException( "IConfigObject has null reference!" );
        }
        
        String id = obj.configId();
        if ( context.hasConfig( id ) ) {
            throw new GlueException( "The Configuration with id: " + id + " exists already in the context: " + context.name() );
        }
        
        ConfigBuilder builder = new ConfigBuilder( context );
        builder.build( obj );
    }

}
