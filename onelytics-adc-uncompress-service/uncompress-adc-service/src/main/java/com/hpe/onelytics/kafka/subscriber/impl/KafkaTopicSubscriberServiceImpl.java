//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber.impl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.onelytics.kafka.subscriber.Event;
import com.hpe.onelytics.kafka.subscriber.KafkaTopicSubscriberService;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import akka.japi.Pair;
import play.api.Configuration;

/**
 * Implementation of subscriber service
 */
public class KafkaTopicSubscriberServiceImpl implements KafkaTopicSubscriberService
{
    private final Logger log = LoggerFactory.getLogger(KafkaTopicSubscriberServiceImpl.class.getSimpleName());
    private final PersistentEntityRegistry persistentEntityRegistry;
    private final Configuration config;

    @Inject
    public KafkaTopicSubscriberServiceImpl(final PersistentEntityRegistry persistentEntityRegistry, final Configuration config)
    {
        this.config = config;
        this.persistentEntityRegistry = persistentEntityRegistry;
        persistentEntityRegistry.register(OutgoingEventEntity.class);
        log.info("Registered OutgoingEventEntity.class");
    }

    @PostConstruct
    private void testing()
    {
        log.info("testing @PostConstruct in lagom");
    }

    //
    @Override
    public Topic<Event> receiveEvents()
    {
        return TopicProducer.taggedStreamWithOffset(
                EventTagAggregater.TAG.allTags(),
                (tag, offset) ->

                // Load the event stream for the passed in shard tag
                persistentEntityRegistry.eventStream(tag, offset).map(eventAndOffset -> {

                    // Now we want to convert from the persisted event to the published event.
                    // Although these two events are currently identical, in future they may
                    // change and need to evolve separately, by separating them now we save
                    // a lot of potential trouble in future.
                    com.hpe.onelytics.kafka.subscriber.Event eventToPublish;
                    System.out.println(eventAndOffset.toString() + ", offset:" + offset);

                    if (eventAndOffset
                            .first() instanceof EventTagAggregater)
                    {
                        final EventTagAggregater messageChanged = eventAndOffset
                                .first();

                        System.out.println(messageChanged.toString() + ", offset:" + offset);
                        eventToPublish = new com.hpe.onelytics.kafka.subscriber.Event();

                    }
                    else
                    {
                        throw new IllegalArgumentException("Unknown event: " + eventAndOffset.first());
                    }

                    // We return a pair of the translated event, and its offset, so that
                    // Lagom can track which offsets have been published.
                    return Pair.create(eventToPublish, eventAndOffset.second());
                }));
    }
}
