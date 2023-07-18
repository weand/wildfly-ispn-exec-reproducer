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

import java.util.Set;
import java.util.function.Consumer;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;

import org.infinispan.util.function.SerializableRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andreas Weise (a.weise@avm.de)
 *
 */
public class RemoteCommand implements SerializableRunnable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteCommand.class);

    private final String originHost;

    public RemoteCommand(final String originHost) {
        this.originHost = originHost;
    }

    @Override
    public void run() {
        LOGGER.info("Running Remote Command from " + originHost);
        Thread.dumpStack();
        
        
        safeCDIAction(InfinispanExecutor.class, e -> {
            LOGGER.info(e.toString());
        });
        
        lookupFooEJB().test();
    }
    
    // EJB utils ///////////////////////////
    
    private static FooEJB lookupFooEJB() {
        try {
            final InitialContext ctx = new InitialContext();
            return (FooEJB) ctx.lookup("java:global/test/" + FooEJB.class.getSimpleName());
        } catch (Exception e) {
            throw new RuntimeException("Unable to lookup RateLimiterController.", e);
        }
    }
    
    // CDI utils ///////////////////////////

    private static <T> void safeCDIAction(final Class<T> clazz, final Consumer<T> action) {
        final T obj;
        CreationalContext<T> creationalContext = null;
        try {
            try {

                final BeanManager bm = CDI.current().getBeanManager();
                final Set<Bean<?>> beans = bm.getBeans(clazz);
                @SuppressWarnings("unchecked")
                final Bean<T> bean = (Bean<T>) bm.resolve(beans);
                creationalContext = bm.createCreationalContext(bean);
                obj = select(clazz, bean, creationalContext);
                if (obj != null) {
                    action.accept(obj);
                }
            } catch (Exception e) {
                LOGGER.error("Selecting CDIBean has thrown an Exception", e);
            }
        } finally {
            if (creationalContext != null) {
                try {
                    creationalContext.release();
                } catch (Exception e) {
                    LOGGER.error("Can't release creational context", e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T select(final Class<T> clazz, final Bean<?> bean, final CreationalContext<?> creationalContext) {
        return (T) CDI.current().getBeanManager().getReference(bean, clazz, creationalContext);
    }
}
