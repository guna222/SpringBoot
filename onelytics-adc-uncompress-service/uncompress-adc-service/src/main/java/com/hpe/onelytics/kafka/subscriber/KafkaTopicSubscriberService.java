//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.topic;

import com.hpe.onelytics.kafka.subscriber.impl.KafkaTopicSubscriberServiceModule;
import com.hpe.onelytics.uncompress.utilities.ApplicationConstants;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;

/**
 * Service that defines in and out topics for this micro service
 */
public interface KafkaTopicSubscriberService extends Service
{
    /**
     * This gets received from Kafka for incoming message.
     */
    Topic<Event> receiveEvents();

    @Override
    default Descriptor descriptor()
    {
        // @formatter:off
        return named(KafkaTopicSubscriberServiceModule.getConfig().getString(ApplicationConstants.SERVICE_NAME))
                .withTopics(topic(KafkaTopicSubscriberServiceModule.getConfig().getString(ApplicationConstants.IN_TOPIC), this::receiveEvents)
              // Kafka partitions messages, messages within the same partition will
              // be delivered in order, to ensure that all messages for the same user
              // go to the same partition (and hence are delivered in order with respect
              // to that user), we configure a partition key strategy that extracts the
              // name as the partition key.
                    .withProperty(KafkaProperties.partitionKeyStrategy(), Event::getDeviceSid)
           )
            .withAutoAcl(true);
	  }
}
