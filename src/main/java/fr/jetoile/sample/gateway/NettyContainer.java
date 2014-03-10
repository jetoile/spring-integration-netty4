/*
 * Copyright (c) 2011 Khanh Tuong Maudoux <kmx.petals@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package fr.jetoile.sample.gateway;

import java.util.Arrays;

import com.google.common.collect.Lists;
import com.wordnik.swagger.jaxrs.config.BeanConfig;

import fr.jetoile.sample.resteasy.MyNettyJaxrsServer;
import fr.jetoile.sample.service.SimpleService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NettyContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyContainer.class);

    public static final String CONF_PROPERTIES = "conf.properties";

    private static Configuration config;

    private SimpleService simpleService;

    public NettyContainer(SimpleService simpleService) {
        try {
            config = new PropertiesConfiguration(CONF_PROPERTIES);

        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("bad config");
        }
        this.simpleService = simpleService;
        initServer();
    }


    private void initServer() {
        ResteasyDeployment deployment = new ResteasyDeployment();

        initSwagger(deployment);

        int nettyPort = 8081;

        if (config != null) {
            deployment.setAsyncJobServiceEnabled(config.getBoolean("netty.asyncJobServiceEnabled", false));
            deployment.setAsyncJobServiceMaxJobResults(config.getInt("netty.asyncJobServiceMaxJobResults", 100));
            deployment.setAsyncJobServiceMaxWait(config.getLong("netty.asyncJobServiceMaxWait", 300000));
            deployment.setAsyncJobServiceThreadPoolSize(config.getInt("netty.asyncJobServiceThreadPoolSize", 100));

            nettyPort = config.getInt("netty.port", TestPortProvider.getPort());
        } else {
            LOGGER.warn("is going to use default netty config");
        }

        deployment.setResources(Arrays.<Object>asList(simpleService));

        MyNettyJaxrsServer netty = new MyNettyJaxrsServer();

        netty.setDeployment(deployment);
        netty.setPort(nettyPort);
        netty.setRootResourcePath("");
        netty.setSecurityDomain(null);
        netty.start();
    }

    private void initSwagger(ResteasyDeployment deployment) {
        BeanConfig swaggerConfig = new BeanConfig();
        swaggerConfig.setVersion(config.getString("swagger.version", "1.0.0"));
        swaggerConfig.setBasePath("http://" + config.getString("swagger.host", "localhost") + ":" + config.getString("swagger.port", "8081"));
        swaggerConfig.setTitle(config.getString("swagger.title", "jetoile sample app"));
        swaggerConfig.setScan(true);
        swaggerConfig.setResourcePackage("fr.jetoile.sample.service");

        deployment.setProviderClasses(Lists.newArrayList(
                "com.wordnik.swagger.jaxrs.listing.ResourceListingProvider",
                "com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider"));
        deployment.setResourceClasses(Lists.newArrayList("com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON"));
        deployment.setSecurityEnabled(false);
    }

}