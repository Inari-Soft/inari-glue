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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import com.inari.glue.impl.GlueContextImpl;
import com.inari.glue.impl.IConfigWriter;

public class XMLConfigWriter implements IConfigWriter {
    
    public static final String WRITER_VERSION = "0.1";
    public static final String XML_ENCODING = "UTF-8";
    public static final String XML_VERSION = "1.0";
    
    private Date date = new Date( System.currentTimeMillis() );
    private String description = "";

    @Override
    public XMLConfigWriter setDate( Date date ) {
        this.date = date;
        return this;
    }

    @Override
    public XMLConfigWriter setDescription( String description ) {
        this.description = description;
        return this;
    }


    @Override
    public void writeConfig( GlueContextImpl context, OutputStream out ) {
        
        String xmlConfigString = new XMLStringBuilder()
            .setConfigVersion( WRITER_VERSION )
            .setContext( context )
            .setDate( date )
            .setDescription( description )
            .setXmlEncoding( XML_ENCODING )
            .setXmlVersion( XML_VERSION )
            .toXMLString();
        
        PrintWriter pw = new PrintWriter( out );
        pw.write( xmlConfigString );
        pw.flush();
    }

}
