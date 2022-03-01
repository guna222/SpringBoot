//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.onelytics.kafka.subscriber.Event;
import com.hpe.onelytics.kafka.subscriber.KafkaTopicSubscriberService;
import com.hpe.onelytics.uncompress.utilities.ApplicationConstants;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.typesafe.config.Config;

import akka.Done;
import akka.actor.ActorRef;
import akka.stream.javadsl.Flow;

/**
 * Kafka topic subscriber. The class implements the subscription and the code to
 * execute for an incoming kafka message. Delegates the processing to actor
 * system.
 */
public class KafkaTopicSubscriber
{
    private final static Logger log = LoggerFactory.getLogger(KafkaTopicSubscriber.class.getSimpleName());
    //private final String KAFKA_GROUPID = "application.kafka.uncompress.topic.groupid";

    @Inject
    public KafkaTopicSubscriber(
            final KafkaTopicSubscriberService service,
            final StreamRepository repository,
            final PersistentEntityRegistry persistentEntityRegistry,
            final Config config,
            @Named("Decrypt") final ActorRef decryptActor)
    {
        log.debug("Config data :" + config.toString());
        // Create a subscriber
        service
                .receiveEvents()
                .subscribe()
                .withGroupId(config.getString(ApplicationConstants.KAFKA_GROUPID))
                // And subscribe to it with at least once processing semantics.
                .atLeastOnce(

                        // Create a flow that emits a Done for each message it processes
                        Flow.<com.hpe.onelytics.kafka.subscriber.Event> create().mapAsync(1, event -> {
                            try
                            {
                                log.info("decryptActor:" + decryptActor);
                                return handleEvent(repository, event, decryptActor);
                            }
                            catch (final Exception jsonExceptions)
                            {
								log.warn("Malformed JSON or missing attributes:" + jsonExceptions);
                                return CompletableFuture.completedFuture(Done.getInstance());
                            }
                        }));
    }

    private CompletionStage<Done> handleEvent(
            final StreamRepository repository,
            final Event event,
            final ActorRef decryptActor)
    {
        log.debug("Incoming event:" + event.toString());

        if (event instanceof com.hpe.onelytics.kafka.subscriber.Event)
        {
            final com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage messageChanged = new com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage(
                    event.getDeviceSid(),
                    event.getDeviceSn(),
                    event.getFileClass(),
                    event.getFileName(),
                    event.getFileSize(),
                    event.getFileUrl(),
                    event.getFileUUID(),
                    event.getFormattedDate(),
                    event.getProductID(),
                    event.getUploadDate());

            log.debug("KafkaTopicSubscriber.Received kafka message" + messageChanged.getDeviceSid());

            //Save incoming message
            repository.updateInMessage(messageChanged);

            //Let decrypt actor to start its work
            decryptActor.tell(messageChanged, ActorRef.noSender());

            return CompletableFuture.completedFuture(Done.getInstance());
        }
        else
        {
            // Ignore all other events
            return CompletableFuture.completedFuture(Done.getInstance());
        }
    }
}
