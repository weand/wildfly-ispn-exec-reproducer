/*
 * Copyright 2022 by AVM GmbH
 *
 * This software contains free software; you can redistribute it and/or modify
 * it under the terms of the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the copy of the
 * License you received along with this software for more details.
 */

package com.github.weand.wildfly.ispn.exec.reproducer;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andreas Weise (a.weise@avm.de)
 *
 */
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class FooEJB {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FooEJB.class);

    public void test() {
        LOGGER.info("FooEJB");
        Thread.dumpStack();
    }
    
}
