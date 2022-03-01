//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.actors;

import java.io.File;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;
import com.hpe.onelytics.kafka.subscriber.impl.StreamRepository;
import com.hpe.onelytics.uncompress.utilities.UncompressAdc;

import akka.event.DiagnosticLoggingAdapter;
import akka.event.Logging;

/**
 * An actor implementation for uncompressing the ADC data after decrypting.
 */
public class UncompressActor extends BaseActor
{
    final DiagnosticLoggingAdapter log = Logging.getLogger(this);

    public static final class UncompressMessage
    {
        final private File decryptedAdc;
        final private KafkaMessage kafkaMessage;
        final private File destinationDir;

        public KafkaMessage getKafkaMessage()
        {
            return kafkaMessage;
        }

        public File getDecryptedAdc()
        {
            return decryptedAdc;
        }

        public File getDestinationDir()
        {
            return destinationDir;
        }

        public UncompressMessage(final File decryptedAdc, final File destinationDir, final KafkaMessage kafkaMessage)
        {
            this.decryptedAdc = decryptedAdc;
            this.destinationDir = destinationDir;
            this.kafkaMessage = kafkaMessage;
        }
    }

    /**
     * Constructor for injecting StreamRepository
     *
     * @param repository
     */
    @Inject
    public UncompressActor(final StreamRepository repository)
    {
        super(repository);
    }

    @Override
    public Receive createReceive()
    {
        return receiveBuilder().match(UncompressMessage.class, arg -> {
            log.info("UncompressActor address: " + getSelf());
            setLogMDC(arg.getKafkaMessage(), log);
            log.debug("Uncompressing starting ...");
            uncompress(arg);
        }).build();
    }

    public void uncompress(final UncompressMessage message)
    {
        updateTask(message.getKafkaMessage(), "UNCOMPRESS_STARTED");
        log.debug("uncompress started for " + message.getKafkaMessage().toString());
        final UncompressAdc uncompressUtil = new UncompressAdc(message.getKafkaMessage());
        try
        {
            if (uncompressUtil.uncompressAdc(message.getDecryptedAdc(), message.getDestinationDir()))
            {
                updateTask(message.getKafkaMessage(), "UNCOMPRESS_COMPLETED");
                final KafkaMessage messageToNextService = updateKafkaMessageWithNextServiceDetails(message);
                log.debug("message to next service " + messageToNextService.toString());
                updateOutMessage(messageToNextService);
                updateTask(message.getKafkaMessage(), "PUBLISHING_KAFKA_MESSAGE");
                final ObjectMapper obj = new ObjectMapper();
                final String messageToNextServiceStr = obj.writeValueAsString(messageToNextService);
                log.debug("message to next service json format:" + messageToNextServiceStr);
                publishKafkaMessage(messageToNextServiceStr);
            }
            else
            {
                final String error = "Failed to uncompress the ADC data:" + message.getDecryptedAdc();
                log.error(error);
                publishErrorMessage(error);
            }
        }
        catch (final JsonProcessingException e)
        {
            log.error("Failed to convert the kafka message to string" + e.getOriginalMessage());
            publishErrorMessage(e.getMessage());
        }
        log.debug("uncompress completed for:" + message.getKafkaMessage().toString());
        completeTask(message.getKafkaMessage());
    }

    private KafkaMessage updateKafkaMessageWithNextServiceDetails(final UncompressMessage message)
            throws JsonProcessingException
    {
        final KafkaMessage messageToNextService = new KafkaMessage(
                message.getKafkaMessage().getDeviceSid(),
                message.getKafkaMessage().getDeviceSn(),
                message.getKafkaMessage().getFileClass(),
                message.getDestinationDir().getName(),
                message.getKafkaMessage().getFileSize(),
                message.getDestinationDir().getAbsolutePath(),
                message.getKafkaMessage().getFileUUID(),
                message.getKafkaMessage().getFormattedDate(),
                message.getKafkaMessage().getProductClass(),
                message.getKafkaMessage().getUploadDate());

        return messageToNextService;
    }
}
