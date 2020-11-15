/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kfstandard.business.test.parameterized;

import java.lang.reflect.Field;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This code comes from https://gist.github.com/aslakknutsen/1358803
 * Unchanged except for replacing println with LOG
 * 
 */
public class ParameterRule implements MethodRule {
    
    private final static Logger LOG = LoggerFactory.getLogger(ParameterRule.class);

    private final Object[] params;
    private final String fieldName;

    public ParameterRule(String fieldName, Object[]... params) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must be specified");
        }
        if (params == null || params.length == 0) {
            throw new IllegalArgumentException("params must be specified and have more then zero length");
        }
        this.fieldName = fieldName;
        this.params = params;
    }

    public ParameterRule(String fieldName, Object... params) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must be specified");
        }
        if (params == null || params.length == 0) {
            throw new IllegalArgumentException("params must be specified and have more then zero length");
        }
        this.fieldName = fieldName;
        this.params = params;
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                LOG.debug("rule - before " + target.hashCode());
                if (isInContainer()) {
                    for (Object param : params) {
                        Field targetField = target.getClass().getDeclaredField(fieldName);
                        // Need to find a better way to do this with canAccess
                        if (!targetField.isAccessible()) {
                            targetField.setAccessible(true);
                        }
                        targetField.set(target, param);
                        base.evaluate();
                    }
                } else {
                    base.evaluate();
                }
                LOG.debug("rule - after " + target.hashCode());
            }
        };
    }

    private boolean isInContainer() {
        Exception e = new Exception();
        e.fillInStackTrace();
        return e.getStackTrace()[e.getStackTrace().length - 1].getClassName().equals("java.lang.Thread");
    }
}
