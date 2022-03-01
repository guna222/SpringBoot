//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import akka.Done;

/**
 * A class to perform CRUD operation
 */
@Singleton
public class StreamRepository
{
    private final static Logger log = LoggerFactory.getLogger(StreamRepository.class.getSimpleName());
    private final CassandraSession uninitialisedSession;

    // Will return the session when the Cassandra tables have been successfully created
    private volatile CompletableFuture<CassandraSession> initialisedSession;

    @Inject
    public StreamRepository(final CassandraSession uninitialisedSession)
    {
        this.uninitialisedSession = uninitialisedSession;
        // Eagerly create the session
        session();
    }

    private CompletionStage<CassandraSession> session()
    {
        // If there's no initialised session, or if the initialised session future completed
        // with an exception, then reinitialise the session and attempt to create the tables
        if (initialisedSession == null || initialisedSession.isCompletedExceptionally())
        {
            createPublishMessageTable();

            createReceiveMessageTable();

            createTaskTrackerTable();
        }

        return initialisedSession;
    }

    private void createTaskTrackerTable()
    {
        try
        {
            if (initialisedSession != null || !initialisedSession.isCompletedExceptionally())
            {
                initialisedSession.get().executeCreateTable("CREATE TABLE IF NOT EXISTS task_tracker ("
                        + "fileUUID text , "
                        + "deviceSid text, "
                        + "deviceSn text, "
                        + "fileName text, "
                        + "fileUrl text, "
                        + "uploadDate text, "
                        + "updatingEntity text, "
                        + "updateTime text, "
                        + "task_progress text, PRIMARY KEY (fileUUID, updateTime, task_progress))");
            }
            else
            {
                log.error("Invalid initialisedSession object while creating task_tracker table.");
            }
        }
        catch (final InterruptedException e)
        {
            log.error("Failed to create task_tracker table due to InterruptedException. Additional info:" + e.getMessage());
            //TODO:Handle these exceptions more gracefully
        }
        catch (final ExecutionException e)
        {

            log.error("Failed to create task_tracker table due to ExecutionException. Additional info:" + e.getMessage());
            //TODO:Handle these exceptions more gracefully
        }
    }

    private void createReceiveMessageTable()
    {
        try
        {
            if (initialisedSession != null || !initialisedSession.isCompletedExceptionally())
            {
                initialisedSession.get().executeCreateTable("CREATE TABLE IF NOT EXISTS received_message ("
                        + "deviceSid text, "
                        + "deviceSn text, "
                        + "fileClass text,"
                        + "fileName text, "
                        + "fileSize text, "
                        + "fileUrl text, "
                        + "fileUUID text, "
                        + "uploadDate text, "
                        + "productClass text, "
                        + "productID text, PRIMARY KEY (deviceSid, fileUUID, uploadDate))");
            }
            else
            {
                log.error("Invalid initialisedSession object while creating task_tracker table.");
            }
        }
        catch (final InterruptedException e)
        {
            log.error("Failed to create received_message table due to InterruptedException. Additional info:" + e.getMessage());
            //TODO:Handle these exceptions more gracefully
        }
        catch (final ExecutionException e)
        {
            log.error("Failed to create received_message table due to ExecutionException. Additional info:" + e.getMessage());
            //TODO:Handle these exceptions more gracefully
        }
    }

    private void createPublishMessageTable()
    {
        initialisedSession = uninitialisedSession.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS published_message ("
                        + "deviceSid text, "
                        + "deviceSn text, "
                        + "fileClass text,"
                        + "fileName text, "
                        + "fileSize text, "
                        + "fileUrl text, "
                        + "fileUUID text, "
                        + "uploadDate text, "
                        + "productClass text, "
                        + "productID text, PRIMARY KEY (deviceSid, fileUUID, uploadDate))")
                .thenApply(done -> uninitialisedSession).toCompletableFuture();
    }

    /**
     * DONOT USE THIS METHOD
     * Stores the incoming Kafka message in DB
     *
     * @param receivedMessage
     * @return
     */
    public CompletionStage<Done> updateInMessage(final com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage receivedMessage)
    {
        log.debug("In updateInMessage:" + receivedMessage.toString());

        return session().thenCompose(session -> session.executeWrite(
                "INSERT INTO received_message ("
                        + "deviceSid, "
                        + "deviceSn, "
                        + "fileClass, "
                        + "fileName, "
                        + "fileSize, "
                        + "fileUrl, "
                        + "fileUUID, "
                        + "uploadDate, "
                        + "productClass, "
                        + "productID) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                receivedMessage.getDeviceSid(),
                receivedMessage.getDeviceSn(),
                receivedMessage.getFileClass(),
                receivedMessage.getFileName(),
                receivedMessage.getFileSize(),
                receivedMessage.getFileUrl(),
                receivedMessage.getFileUUID(),
                receivedMessage.getFormattedDate(),
                receivedMessage.getProductClass(),
                receivedMessage.getProductID()));
    }

    /**
     * Stores the outgoing Kafka message
     *
     * @param publishedMessage
     * @return
     */
    public CompletionStage<Done> updateOutMessage(final com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage publishedMessage)
    {
        log.debug("In updateOutMessage:" + publishedMessage.toString());

        return session().thenCompose(session -> session.executeWrite(
                "INSERT INTO published_message ("
                        + "deviceSid, "
                        + "deviceSn, "
                        + "fileClass, "
                        + "fileName, "
                        + "fileSize, "
                        + "fileUrl, "
                        + "fileUUID, "
                        + "uploadDate, "
                        + "productClass, "
                        + "productID) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                publishedMessage.getDeviceSid(),
                publishedMessage.getDeviceSn(),
                publishedMessage.getFileClass(),
                publishedMessage.getFileName(),
                publishedMessage.getFileSize(),
                publishedMessage.getFileUrl(),
                publishedMessage.getFileUUID(),
                publishedMessage.getFormattedDate(),
                publishedMessage.getProductClass(),
                publishedMessage.getProductID()));
    }

    /**
     * Stores task progress
     *
     * @param kafkaMessage
     * @param taskState
     * @return
     */
    public CompletionStage<Done> updateTask(
            final com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage kafkaMessage,
            final String updatingEntityName,
            final String taskState)
    {
        log.info(
                "In updateTask:"
                        + kafkaMessage.toString()
                        + ", updating entity name:"
                        + updatingEntityName
                        + ", state:"
                        + taskState);

        return session().thenCompose(session -> session.executeWrite(
                "INSERT INTO task_tracker ("
                        + "fileUUID, "
                        + "deviceSid, "
                        + "deviceSn, "
                        + "fileName, "
                        + "fileUrl, "
                        + "uploadDate, "
                        + "updatingEntity, "
                        + "updateTime, "
                        + "task_progress) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                kafkaMessage.getFileUUID(),
                kafkaMessage.getDeviceSid(),
                kafkaMessage.getDeviceSn(),
                kafkaMessage.getFileName(),
                kafkaMessage.getFileUrl(),
                kafkaMessage.getFormattedDate(),
                updatingEntityName,
                (System.nanoTime() + ""),
                taskState));
    }
}
