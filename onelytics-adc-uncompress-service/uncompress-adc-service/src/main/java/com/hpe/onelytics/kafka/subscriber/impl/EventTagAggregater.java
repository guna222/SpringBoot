//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * A class that provides an immutable, persistent indexed collection for events.
 * This tag is used while creating Kafka topic
 */

public class EventTagAggregater implements Jsonable, AggregateEvent<EventTagAggregater>
{

    static AggregateEventShards<EventTagAggregater> TAG = AggregateEventTag.sharded(EventTagAggregater.class, 1);

    @Override
    public AggregateEventTagger<EventTagAggregater> aggregateTag()
    {
        return TAG;
    }
}
