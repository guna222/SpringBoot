//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.onelytics.uncompress.utilities.ApplicationConstants;
import com.typesafe.config.Config;

import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;

public class KafkaTestPublisher
{
    private final static Logger log = LoggerFactory.getLogger(KafkaTestPublisher.class.getSimpleName());

    /**
     * A utility method to publish the Kafka message.
     *
     * @param kafkaMessage, kafka topic for publishing the message
     */
    public void publish(final ActorSystem system, final String kafkaMessage, final String topic)
    {
        final String kafkaBroker = system.settings().config().getString(ApplicationConstants.KAFKA_BROKER);

        final Config kafkaProducerConfig = system.settings().config()
                .getConfig(ApplicationConstants.KAFKA_PRODUCER_CONFIG);
        final ProducerSettings<String, String> producerSettings = ProducerSettings
                .create(kafkaProducerConfig, new StringSerializer(), new StringSerializer())
                .withBootstrapServers(kafkaBroker);

        final org.apache.kafka.clients.producer.Producer<String, String> kafkaProducer = producerSettings.createKafkaProducer();

        kafkaProducer.send(new ProducerRecord<String, String>(topic, kafkaMessage));

        //Just a small pause as safety net before closing the producer
        sleep(1000);

        kafkaProducer.close();

        log.info("Published to topic:" + topic);
        log.info("published message:" + kafkaMessage);
    }

    private void sleep(final int duration)
    {
        try
        {
            Thread.sleep(duration);
        }
        catch (final InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
