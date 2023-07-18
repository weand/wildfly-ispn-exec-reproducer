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

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andreas Weise (a.weise@avm.de)
 *
 */
@ApplicationScoped
public class InfinispanExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanExecutor.class);

    @Resource(name = "reproducer-cache-container")
    private EmbeddedCacheManager cacheManager;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
        LOGGER.info("InfinispanExecutor.onStart()");
    }

    public void test() {
        final Address coordinator = cacheManager.getCoordinator();
        final Address localAddress = cacheManager.getAddress();
        if (coordinator != null) {
            LOGGER.info("Initiating Command of " + localAddress.toString() + " on " + coordinator.toString());
            final CompletableFuture<Void> f = cacheManager.executor().filterTargets(Arrays.asList(coordinator))
                .submit(new RemoteCommand(String.valueOf(localAddress)));
            try {
                f.get();
            } catch (Exception ex) {
                LOGGER.error("Failed to execute Remote Command.", ex);
            }
        }
    }

}
