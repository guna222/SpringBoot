//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hpe.onelytics.uncompress.integration_tests.assertconditions.AssertActionFactory;
import com.hpe.onelytics.uncompress.utilities.ApplicationConstants;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

/**
 * Test class for testing sanity of uncompress service.
 * The integration test follows a flow
 *
 * <p>
 * Produce Kafka message as defined in the testdata file
 * Recive the message
 * Assert the message attributes as defined in testdata file for produced
 * message
 *
 * <p>
 * Following assert conditions are supported for Kafka message
 * <ul>
 * <li>payloadElementsSize
 * <li>Null
 * <li>notNull
 * <li>Equal
 * <li>notEqual
 * <li>noMessageProduced
 * </ul>
 *
 * @see assertconditions package for implementation of above asserts.
 *
 *      All asserts are created via a factory class.
 *
 */
class Uncompress_integration_test
{
    private final static Logger log = LoggerFactory.getLogger(Uncompress_integration_test.class.getSimpleName());

    private final ObjectMapper mapper = new ObjectMapper();
    private ActorSystem system;

    /**
     * Variable to hold test environment - Dev/integration test but not ITG or
     * prod. This can be configured via int.test.env attribute of
     * application.conf
     */
    String testEnvironment;

    /**
     * Variable to hold test data object.
     */
    JsonNode testData;

    /**
     * Assert condition creation factory
     */
    AssertActionFactory assertActionFactory;

    /**
     * Kafka producer
     */
    KafkaTestPublisher kafkaTestPublisher;

    /**
     * Kafka consumer
     */
    KafkaTestConsumer kafkaTestConsumer;

    @BeforeEach
    public void testSetup()
            throws JsonParseException,
            JsonMappingException,
            IOException
    {
        final File applicationConffile = new File("src/test/resources/application.conf");
        final Config config = ConfigFactory
                .parseFile(applicationConffile)
                .resolve();

        // Create an Akka system
        system = ActorSystem.create("ClusterSystem", config);
        testEnvironment = system.settings().config().getString("application.int.test.env");

        final File testDataFile = new File("src/test/resources/testdata.json");
        testData = mapper.readValue(testDataFile, JsonNode.class);

        assertActionFactory = new AssertActionFactory();

        kafkaTestPublisher = new KafkaTestPublisher();
        kafkaTestConsumer = new KafkaTestConsumer();
    }

    @Test
    public void uncompress_service_int_test()
    {
        assumeTrue("uncompress_int_test".equals(testEnvironment));

        final ArrayNode testDataArray = (ArrayNode) testData.get("testdata");

        testDataArray.forEach(element -> {
            kafkaTestPublisher.publish(
                    system,
                    element.get("kafkapayload").toString(),
                    system.settings().config().getString(ApplicationConstants.IN_TOPIC));

            final List<String> rcvdElementsList = kafkaTestConsumer.receive(system);
            if (rcvdElementsList.size() != 0)
            {
                log.info("Rcvd msg:" + rcvdElementsList.get(0));
            }

            verifyResult(element, rcvdElementsList);
            log.info("all tests have passed");

        });
    }

    private void verifyResult(final JsonNode testElement, final List<String> rcvdElementsList)
    {
        validateReceivedMessage(testElement, rcvdElementsList);

        try
        {
            final JsonNode asserts = testElement.get("kafkaAsserts");
            final JsonNode rcvdData = mapper.readValue(rcvdElementsList.get(0), JsonNode.class);

            //An example: "assertAll": [{"payloadElementsSize" : 10},{"notNull":"Device-SID"},{"equal":"Device-SID,14770ae2-aa53-43a7-b769-b1fd742265a8"},{"notEqual":"Product-Class,abc"}],
            asserts.forEach(
                    assertCondition -> assertTrue(
                            assertActionFactory.getAssertCondition(assertCondition, rcvdData).apply(assertCondition, rcvdData),
                            "Check logs for more information on failure")

            );
        }

        catch (final IOException e)
        {
            fail("Test failed:" + e.getMessage());
        }

    }

    private void validateReceivedMessage(final JsonNode testElement, final List<String> rcvdElementsList)
    {
        if (rcvdElementsList.isEmpty() || rcvdElementsList.size() > 1)
        {
            fail(
                    "Expected one Kafka messaage, but received invalid number of message for test:"
                            + testElement
                            + ",Elements:"
                            + rcvdElementsList);
        }
    }
}
