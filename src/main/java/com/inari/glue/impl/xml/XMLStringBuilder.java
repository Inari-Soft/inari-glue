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
package com.inari.glue.impl.xml;

import static com.inari.glue.impl.xml.XMLConfigConstants.ATTR_ID;
import static com.inari.glue.impl.xml.XMLConfigConstants.ATTR_NAME;
import static com.inari.glue.impl.xml.XMLConfigConstants.ATTR_TYPE;
import static com.inari.glue.impl.xml.XMLConfigConstants.ID_ALIAS;
import static com.inari.glue.impl.xml.XMLConfigConstants.ID_CONFIG_OBJECT;
import static com.inari.glue.impl.xml.XMLConfigConstants.ID_HEAD;
import static com.inari.glue.impl.xml.XMLConfigConstants.ID_PROPERTY;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.inari.glue.GlueException;
import com.inari.glue.impl.Alias;
import com.inari.glue.impl.ConfigData;
import com.inari.glue.impl.GlueContextImpl;
import com.inari.commons.config.IConfigObject;

public class XMLStringBuilder {
    
    private GlueContextImpl context;
    private String description = "-- NONE --";
    private Date date;
    private String configVersion = "0.0";
    private String xmlVersion = "1.0";
    private String xmlEncoding = "UTF-8";
    
      
    public XMLStringBuilder setXmlVersion( String xmlVersion ) {
        this.xmlVersion = xmlVersion;
        return this;
    }

    public XMLStringBuilder setXmlEncoding( String xmlEncoding ) {
        this.xmlEncoding = xmlEncoding;
        return this;
    }

    public XMLStringBuilder setConfigVersion( String version ) {
        this.configVersion = version;
        return this;
    }
    
    public XMLStringBuilder setContext( GlueContextImpl context ) {
        this.context = context;
        return this;  
    }
    
    public XMLStringBuilder setDescription( String description ) {
        this.description = description;
        return this;
    }
    
    public XMLStringBuilder setDate( Date date ) {
        this.date = date;
        return this;
    }
    
    public String toXMLString() throws GlueException {
        if ( context == null  ) {
            throw new GlueException( "GlueContext has null reference!" );
        }
        if ( date == null ) {
            date = new Date( System.currentTimeMillis() );
        }
        
        StringBuilder builder = new StringBuilder();
        
        writeXMLHeader( builder );
        startConfig( builder );
        writeAlias( builder );
        writeData( builder );
        endConfig( builder );
        
        return builder.toString();
    }
    
    private void writeXMLHeader( StringBuilder builder ) {
        builder.append( "<?xml version=\"" ).append( xmlVersion ).append( "\"");
        builder.append( " encoding=\"" ).append( xmlEncoding ).append( "\"?>" );
    }
    
    private void startConfig( StringBuilder builder ) {
        builder.append( "\n\n" );
        builder.append(    "<" ).append( ID_HEAD );
        builder.append( " name=\"" ).append( context.name() ).append( "\"" );
        builder.append( " description=\"" ).append( description ).append( "\"" );
        builder.append( " version=\"" ).append( configVersion ).append( "\"" );
        builder.append( " date=\"" ).append( date.toString() ).append( "\">" );
    }
    
    private void endConfig( StringBuilder builder ) {
        builder.append( "\n" );
        builder.append(    "</" ).append( ID_HEAD ).append( ">" );
    }
    
    private void writeAlias( StringBuilder builder ) {
        Collection<Alias> alias = context.getAlias();
        if ( ( alias != null ) && !alias.isEmpty() ) { 
            builder.append( "\n\n" );
            for ( Alias a : alias ) {
                builder.append( "  <" ).append( ID_ALIAS ).append( " " );
                builder.append( ATTR_NAME ).append( "=\"" ).append( a.alias() ).append( "\" " );
                builder.append( ATTR_TYPE ).append( "=\"" ).append( a.fullClassName() ).append( "\"" );
                builder.append( "/>\n" );
            }
        }
        
    }
    
    private void writeData( StringBuilder builder ) {  
        for ( ConfigData configData : context.getConfigs() ) {
            writeConfigData( builder, configData );
        }
    }
    
    private void writeConfigData( StringBuilder builder, ConfigData data ) {
        builder.append( "\n\n" );
        builder.append( "  <" ).append( ID_CONFIG_OBJECT ).append( " " );
        builder.append( ATTR_ID ).append( "=\"" ).append( data.id() ).append( "\" " );
        builder.append( ATTR_TYPE ).append( "=\"" ).append( getType( data.type() ) ).append( "\"" );
        builder.append( ">\n" );
        
        for ( Map.Entry<String, String> prop : data.entrySet() ) {
            writeProperty( builder, prop.getKey(), prop.getValue() );
        }
        builder.append( "\n" );
        builder.append( "  <" ).append( ID_CONFIG_OBJECT ).append( "/>" );
    }
    
    private void writeProperty( StringBuilder builder, String name, String value ) {
        builder.append( "\n" );
        builder.append( "    <" ).append( ID_PROPERTY ).append( " " );
        builder.append( ATTR_NAME ).append( "=\"" ).append( name ).append( "\">" );
        builder.append( value );
        builder.append( "</" ).append( ID_PROPERTY ).append( ">" );
    }
    
    private String getType( Class<? extends IConfigObject> typeClass ) {
        Alias alias = context.getAlias( typeClass );
        if ( alias != null ) {
            return alias.alias();
        }
        return typeClass.getName();
    }
    

}
