//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.hpe.onelytics.kafka.subscriber.KafkaTopicSubscriberService;
import com.hpe.onelytics.uncompress.actors.DecryptActor;
import com.hpe.onelytics.uncompress.actors.UncompressActor;
import com.hpe.onelytics.uncompress.utilities.ApplicationConstants;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.typesafe.config.Config;

import akka.routing.FromConfig;
import play.Environment;
import play.libs.akka.AkkaGuiceSupport;

/**
 * The module that binds the KafkaTopicSubscriberService so that it can be
 * served when a kafka message received.
 */
public class KafkaTopicSubscriberServiceModule extends AbstractModule implements ServiceGuiceSupport, AkkaGuiceSupport
{
    private final Logger log = LoggerFactory.getLogger(KafkaTopicSubscriberServiceModule.class.getSimpleName());

    private final Environment environment;
    private final Config config;
    private static Config privateConfig;

    public KafkaTopicSubscriberServiceModule(final Environment environment, final Config config)
    {
        this.environment = environment;
        this.config = config;
        privateConfig = config;
    }

    @Override
    protected void configure()
    {
        if (environment.isProd())
        {
            log.info("Starting " + config.getString(ApplicationConstants.SERVICE_NAME) + " service in production mode");
            bindService(KafkaTopicSubscriberService.class, KafkaTopicSubscriberServiceImpl.class);
            bind(KafkaTopicSubscriber.class).asEagerSingleton();

            //Prepare for Actor pool inject
            bindActor(DecryptActor.class, "Decrypt", FromConfig.getInstance()::props);
            bindActor(UncompressActor.class, "Uncompress", FromConfig.getInstance()::props);

            log.info("Started " + config.getString(ApplicationConstants.SERVICE_NAME) + " service in production mode");
        }
        else
        {
            log.info("Starting " + config.getString(ApplicationConstants.SERVICE_NAME) + " service in dev mode");
            bindService(KafkaTopicSubscriberService.class, KafkaTopicSubscriberServiceImpl.class);
            bind(KafkaTopicSubscriber.class).asEagerSingleton();

            //Prepare for Actor pool inject
            bindActor(DecryptActor.class, "Decrypt", FromConfig.getInstance()::props);
            bindActor(UncompressActor.class, "Uncompress", FromConfig.getInstance()::props);
            log.info("Started " + config.getString(ApplicationConstants.SERVICE_NAME) + " service in dev mode");
        }
    }

    public static Config getConfig()
    {
        return privateConfig;
    }

}
