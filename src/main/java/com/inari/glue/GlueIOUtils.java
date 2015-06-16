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
package com.inari.glue;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.inari.glue.impl.GlueContextImpl;
import com.inari.glue.impl.IConfigReader;
import com.inari.glue.impl.IConfigWriter;
import com.inari.commons.resource.Resource;

public abstract class GlueIOUtils {
    
    public static final void loadConfiguration( GlueService service, Resource resource, GlueIOType ioType ) throws GlueException {
        if ( ( service == null ) || ( resource == null ) ) {
            throw new IllegalArgumentException( "IGlueService or Resource has null reference!" );
        } 
        if ( !resource.exists() ) {
            throw new GlueException( "The Resource: " + resource + " physically not exists!" );
        }
        
        IConfigReader reader = ioType.getReader();
        reader.loadConfig( service, resource );
    }
    
    
    
    
    public static final void writeConfiguration( 
        GlueService service, 
        String name, String description, 
        File file, GlueIOType ioType 
    ) throws GlueException {

        if ( !file.exists() ) {
            try {
                if ( !file.createNewFile() ) {
                    throw new GlueException( "Failed to create file: " + file );
                }
            } catch ( Exception e ) {
                throw new GlueException( "Failed to create file: " + file, e );
            }
        }
        
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( file );
            writeConfiguration( service, name, description, out, ioType );
        } catch ( Exception e ) {
            throw new GlueException( "Failed to create FileOutputStream for file: " + file, e );
        } finally {
            if ( out != null ) {
                try {
                    out.close();
                } catch ( Exception e ) {
                    throw new GlueException( "Failed to close FileOutputStream: " + out, e );
                }
            }
        }
    }
    
    public static final void writeConfiguration( 
            GlueService service, 
            String name, String description, 
            OutputStream out, GlueIOType ioType 
    ) throws GlueException {
        
        if ( ( service == null ) || ( out == null ) ) {
            throw new IllegalArgumentException( "IGlueService or OutputStream has null reference!" );
        }
        
        GlueContextImpl configuration = (GlueContextImpl) service.getContext( name );
        if ( configuration == null ) {
            throw new GlueException( "No GlueContext configuration found with name: " + name );
        }
        
        IConfigWriter writer = ioType.getWriter();
        writer.setDescription( description );
        writer.writeConfig( configuration, out );
    }
    
    
}
