//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.actors;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;
import com.hpe.onelytics.kafka.subscriber.impl.StreamRepository;
import com.hpe.onelytics.uncompress.utilities.ApplicationConstants;
import com.typesafe.config.Config;

import akka.actor.AbstractActor;
import akka.event.DiagnosticLoggingAdapter;
import akka.kafka.ProducerSettings;

/**
 * A base class for all actors. This class provides common methods for
 * publishing kafka messages, update the DB, set logger attributes.
 */
public abstract class BaseActor extends AbstractActor
{
    private final static Logger log = LoggerFactory.getLogger(BaseActor.class.getSimpleName());

    private final StreamRepository repository;

    /**
     * Public constructor
     *
     * @param config
     * @param repository
     */
    public BaseActor(final StreamRepository repository)
    {
        this.repository = Preconditions.checkNotNull(repository, "repository");
    }

    /**
     * A protected method to publish error messages to error topic.
     *
     * @param errorMessage
     */
    protected void publishErrorMessage(final String errorMessage)
    {
        final String topic = getContext().getSystem().settings().config().getString(ApplicationConstants.ERROR_TOPIC);
        publish(errorMessage, topic);
    }

    /**
     * A protected method to publish kafka messages
     *
     * @param successMessage
     */
    protected void publishKafkaMessage(final String successMessage)
    {
        final String topic = getContext().getSystem().settings().config().getString(ApplicationConstants.OUT_KAFKA_TOPIC);
        publish(successMessage, topic);
    }

    /**
     * A utility method to publish the kafka message.
     * TODO: Needs to be optimised. We need to create producer once and reuse it
     * if possible.
     *
     * @param kafkaMessage, kafka topic for publishing the message
     */
    private void publish(final String kafkaMessage, final String topic)
    {
        final String kafkaBroker = getContext().getSystem().settings().config().getString(ApplicationConstants.KAFKA_BROKER);
        log.debug("Publishing to topic:" + topic);
        log.debug("Kafka broker :" + kafkaBroker);

        final Config kafkaProducerConfig = getContext().getSystem().settings().config()
                .getConfig(ApplicationConstants.KAFKA_PRODUCER_CONFIG);
        final ProducerSettings<String, String> producerSettings = ProducerSettings
                .create(kafkaProducerConfig, new StringSerializer(), new StringSerializer())
                .withBootstrapServers(kafkaBroker);

        final org.apache.kafka.clients.producer.Producer<String, String> kafkaProducer = producerSettings.createKafkaProducer();

        kafkaProducer.send(new ProducerRecord<String, String>(topic, kafkaMessage));
        kafkaProducer.close();
        log.info("Published to:" + topic);
    }

    /**
     * A protected method to store the message in database
     *
     * @param kafkaMessage message to store in database
     */
    protected void updateOutMessage(final KafkaMessage kafkaMessage)
    {
        repository.updateOutMessage(kafkaMessage);
        log.debug("Updated the DB:" + kafkaMessage.toString());
    }

    /**
     * A protected method to update task progress in database
     *
     * @param kafkaMessage message to store in database
     */
    protected void updateTask(final KafkaMessage kafkaMessage, final String taskState)
    {
        repository.updateTask(kafkaMessage, getSelf().toString(), taskState);
        log.debug("Updated the task tracker DB:" + kafkaMessage.toString() + ", with task state:" + taskState);
    }

    /**
     * A protected method to create task in database
     *
     * @param kafkaMessage message to store in database
     */
    protected void createTask(final KafkaMessage kafkaMessage)
    {
        final String taskState = "TASK_CREATED";
        repository.updateTask(kafkaMessage, getSelf().toString(), taskState);
        log.debug("Created task for:" + kafkaMessage.toString() + ", with task state:" + taskState);
    }

    /**
     * A protected method to complete task progress in database
     *
     * @param kafkaMessage message to store in database
     */
    protected void completeTask(final KafkaMessage kafkaMessage)
    {
        final String taskState = "TASK_COMPLETED";
        repository.updateTask(kafkaMessage, getSelf().toString(), taskState);
        log.debug("Created task for:" + kafkaMessage.toString() + ", with task state:" + taskState);
    }

    /**
     * Sets required Kafka message attributes in log object. The attributes are
     * logged to configured log file.
     *
     * @param kafkaMsg
     * @param log
     */
    protected void setLogMDC(final KafkaMessage kafkaMsg, final DiagnosticLoggingAdapter log)
    {
        final Map<String, Object> mdcMap = new HashMap<String, Object>();
        mdcMap.put("fileUuid", kafkaMsg.getFileUUID());
        mdcMap.put("stationId", kafkaMsg.getDeviceSid());
        log.setMDC(mdcMap);
    }
}
