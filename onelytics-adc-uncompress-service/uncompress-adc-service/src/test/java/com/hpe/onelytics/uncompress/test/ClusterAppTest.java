/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.test;

import org.junit.jupiter.api.Test;

import com.hpe.onelytics.uncompress.actors.ClusterApp;

import junit.framework.TestCase;

/**
 * Test for ClusterApp
 *
 * @author Onelytics
 *
 */
public class ClusterAppTest extends TestCase
{

    @Test
    public void testClusterAppCreated()
    {
        final String configFile = this.getClass().getClassLoader().getResource("application-test.conf").getFile();
        final String kafkaConsumerProps = "kafka-consumer.properties";
        final String kafkaProducerProps = "kafka-producer-test.properties";
        final String args[] = {
                configFile, kafkaConsumerProps, kafkaProducerProps
        };
        final boolean isStarted = ClusterApp.start(args);
        assertTrue(isStarted);
    }

    @Test
    public void testActorSystemWithNullArguments()
    {
        final String configFile = null;
        final String kafkaConsumerProps = null;
        final String kafkaProducerProps = null;
        final String args[] = {
                configFile, kafkaConsumerProps, kafkaProducerProps
        };
        final boolean isStarted = ClusterApp.start(args);
        assertFalse(isStarted);
    }

    @Test
    public void testActorSystemWithLessArguments()
    {
        final String configFile = null;
        final String kafkaConsumerProps = null;
        final String args[] = {
                configFile, kafkaConsumerProps
        };
        ClusterApp.start(args);
    }

    @Test
    public void testClusterAppStop()
            throws InterruptedException
    {
        final String configFile = this.getClass().getClassLoader().getResource("application-test.conf").getFile();
        final String kafkaConsumerProps = "kafka-consumer.properties";
        final String kafkaProducerProps = "kafka-producer-test.properties";
        final String args[] = {
                configFile, kafkaConsumerProps, kafkaProducerProps
        };
        final boolean isStarted = ClusterApp.start(args);
        assertTrue(isStarted);
        final boolean isStopped = ClusterApp.stop();
        assertTrue(isStopped);
    }
}
