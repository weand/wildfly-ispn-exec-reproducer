package com.github.weand.wildfly.ispn.exec.reproducer.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.weand.wildfly.ispn.exec.reproducer.InfinispanExecutor;

@RunWith(Arquillian.class)
public class InfinispanExecutorIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanExecutorIT.class);

    @ArquillianResource
    private ContainerController controller;

    @Deployment(name = "test1")
    @TargetsContainer("server1")
    public static WebArchive createBasicDeployment1() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackages(true, "com.github.weand.wildfly.ispn.exec.reproducer")
            .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
            .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml"))
            .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    @Deployment(name = "test2")
    @TargetsContainer("server2")
    public static WebArchive createBasicDeployment2() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackages(true, "com.github.weand.wildfly.ispn.exec.reproducer")
            .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
            .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml"))
            .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    @Inject
    public InfinispanExecutor cacheExecBean;

    @OperateOnDeployment("test1")
    @Test
    public void testCacheExecutor1() {

        assertNotNull(cacheExecBean);

        cacheExecBean.test();

    }

    @OperateOnDeployment("test2")
    @Test
    public void testCacheExecutor2() {

        assertNotNull(cacheExecBean);

        cacheExecBean.test();

    }

}
