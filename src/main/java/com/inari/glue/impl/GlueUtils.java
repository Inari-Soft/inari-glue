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
import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.config.Configured;
import com.inari.commons.config.IConfigObject;

public abstract class GlueUtils {
    
    public static final Collection<Field> getAllConfiguredFields( IConfigObject instance ) {
        Collection<Field> fields = new ArrayList<Field>();
        Class<? extends IConfigObject> currentClass = instance.getClass();
        while ( currentClass != null ) {
            collectConfiguredFields( currentClass, fields );
            currentClass = findSuperIConfigObjectClass( currentClass );
        }

        return fields;
    }
    
    private static final void collectConfiguredFields( Class<? extends IConfigObject> clazz, Collection<Field> fields ) {
        for ( Field field : clazz.getDeclaredFields() ) {
            if ( field.getAnnotation( Configured.class ) != null ) {
                fields.add( field );
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private static final Class<? extends IConfigObject> findSuperIConfigObjectClass( Class<? extends IConfigObject> clazz ) {
        if ( clazz.getSuperclass() == null ) {
            return null;
        }
        
        Class<?> currentClass = clazz.getSuperclass();
        while ( ( currentClass != null ) && ( !IConfigObject.class.isAssignableFrom( currentClass ) ) ) {
            currentClass = currentClass.getSuperclass();
        }
        
        return (Class<? extends IConfigObject>) currentClass;
    } 

}
