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
import static com.inari.glue.impl.xml.XMLConfigConstants.ATTR_VERSION;
import static com.inari.glue.impl.xml.XMLConfigConstants.ID_ALIAS;
import static com.inari.glue.impl.xml.XMLConfigConstants.ID_CONFIG_OBJECT;
import static com.inari.glue.impl.xml.XMLConfigConstants.ID_HEAD;
import static com.inari.glue.impl.xml.XMLConfigConstants.ID_PROPERTY;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.inari.glue.GlueException;
import com.inari.glue.GlueService;
import com.inari.glue.impl.Alias;
import com.inari.glue.impl.ConfigData;
import com.inari.glue.impl.GlueContextImpl;
import com.inari.commons.StringUtils;
import com.inari.commons.config.IConfigObject;

public class XMLConfigHandler extends DefaultHandler {
    
    private GlueService service;
    
    private GlueContextImpl currentContext;
    private ConfigData currentConfig;
    
    private String currentPropertyName = null;
    private String currentPropertyValue = null;
    
    
    XMLConfigHandler( GlueService service ) {
        this.service = service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException {
        
        if ( ID_HEAD.equals( qName ) ) {
            // check version and create new context with given name
            String version = attributes.getValue( ATTR_VERSION );
            if ( !XMLConfigReader.READER_VERSION.equals( version ) ) {
                throw new SAXException( "Wrong Version detected: Reader Version is: " + XMLConfigReader.READER_VERSION + " and current InariConfiguration Version is: " + version );
            }
            
            String name = attributes.getValue( ATTR_NAME );
            if ( StringUtils.isBlank( name ) ) {
                throw new SAXException( "No configuration name found! check configuration." );
            }
            
            currentContext = (GlueContextImpl) service.createContext( name );
            return;
        }
        
        if ( ID_ALIAS.equals( qName ) ) {
            String typeName = attributes.getValue( ATTR_NAME );
            String type = attributes.getValue( ATTR_TYPE );
            
            if ( StringUtils.isBlank( typeName ) ) {
                throw new SAXException( "No 'name' found for Alias! check configuration." );
            }
            if ( StringUtils.isBlank( type ) ) {
                throw new SAXException( "No 'type' found for Alias! check configuration." );
            }
            
            currentContext.defineAlias( typeName, type );
            return;
        }
        
        if ( ID_CONFIG_OBJECT.equals( qName ) ) {
            String id = attributes.getValue( ATTR_ID );
            String typeName = attributes.getValue( ATTR_TYPE );
            
            if ( StringUtils.isBlank( id ) ) {
                throw new SAXException( "No 'id' found for Object! check configuration." );
            }
            if ( StringUtils.isBlank( typeName ) ) {
                throw new SAXException( "No 'type' found for Object! check configuration." );
            }
            
            Alias alias = currentContext.getAlias( typeName );
            Class<? extends IConfigObject> type = null;
            if ( alias != null ) {
                type = alias.type();
            } else {
                try {
                    type = (Class<? extends IConfigObject>) Class.forName( typeName );
                } catch ( Exception e ) {
                    throw new GlueException( "Failed to create Class-Type form typeName: " + typeName, e );
                }
            }
            
            currentConfig = currentContext.createConfig( id, type );
            return;
        }
        
        if ( ID_PROPERTY.equals( qName ) ) {
            String name = attributes.getValue( ATTR_NAME );
            
            if ( StringUtils.isBlank( name ) ) {
                throw new SAXException( "No 'name' found for property! check configuration." );
            }
            
            currentPropertyName = name;
            return;
        }
        
        throw new SAXException( "Unknown start element: " + qName );
    }

    @Override
    public void endElement( String uri, String localName, String qName ) throws SAXException {
        
        if ( ID_CONFIG_OBJECT.equals( qName ) ) {
            currentConfig = null;
        }
        
        if ( ID_PROPERTY.equals( qName ) ) {
            currentConfig.put( currentPropertyName, currentPropertyValue );
            currentPropertyName = null;
            currentPropertyValue = null;
        }
    }

    @Override
    public void characters( char ch[], int start, int length ) throws SAXException {
        
        if ( currentPropertyName != null ) {
            currentPropertyValue = new String( ch, start, length );
        } 
    }

}
