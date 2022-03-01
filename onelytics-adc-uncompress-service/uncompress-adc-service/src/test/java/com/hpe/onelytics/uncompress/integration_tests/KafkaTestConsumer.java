//(C) Copyright YYYY Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.onelytics.uncompress.utilities.ApplicationConstants;

import akka.actor.ActorSystem;

public class KafkaTestConsumer
{
    private final static Logger log = LoggerFactory.getLogger(KafkaTestConsumer.class.getSimpleName());

    public List<String> receive(final ActorSystem system)
    {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, system.settings().config().getString(ApplicationConstants.KAFKA_BROKER));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, system.settings().config().getString(ApplicationConstants.KAFKA_GROUPID));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        final String topic = system.settings().config().getString(ApplicationConstants.OUT_KAFKA_TOPIC);
        log.info("Consuming on topic:" + topic);
        final Consumer<Long, String> consumer = new KafkaConsumer<Long, String>(props);
        consumer.subscribe(Collections.singletonList(topic));

        final List<String> consumedMessage = new ArrayList<>();

        int retry = 3;

        while (retry > 0)
        {
            final ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofSeconds(5));
            if (consumerRecords.count() == 0)
            {
                retry--;
                continue;
            }

            consumerRecords.forEach(record -> {
                consumedMessage.add(record.value());
            });

            consumer.commitSync();

            break;
        }
        consumer.close();

        if (consumedMessage.size() == 0)
        {
            consumedMessage.add(createNoMessage());
        }
        return consumedMessage;
    }

    private String createNoMessage()
    {
        return "{\"noMessageProduced\":\"true\"}";
    }
}
