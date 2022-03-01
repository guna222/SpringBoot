/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;
import com.hpe.onelytics.uncompress.utilities.UncompressAdc;

public class UncompressAdcTest
{
    private final Logger log = LoggerFactory.getLogger(UncompressAdcTest.class.getSimpleName());
    private File decryptedAdc;
    private KafkaMessage kafkaMessage;
    private final long offset = 0;
    private File destinationDir;
    private UncompressAdc uncompressUtil;

	@BeforeEach
    public void setup()
            throws IOException
    {
        this.getClass().getClassLoader();
        decryptedAdc = new File(ClassLoader.getSystemResource("unencrypted_adc.tar.gz").getFile());
        //TODO:Add proper arguments
        kafkaMessage = new KafkaMessage(
                "14770ae2-aa53-43a7-b769-b1fd742265a8",
                "VMware-42 3f 06 9d 0d 1d 6a ea-80 44 25 a1 ef 9e c4 f5",
                "data-file",
                "encrypted_adc.tar.gz",
                "830333",
                "/adc/encrypted_adc.tar.gz",
                "37ccc038-2207-4713-8fc2-c786e96d1f8e",
                "oneview",
                "ai-cicvc3-126.vse.rdlabs.hpecorp.net",
                new Date());
        uncompressUtil = new UncompressAdc(kafkaMessage);
    }

    @Test
	public void testUncompressAdcPassed(@TempDir File destinationDir)
            throws InterruptedException,
            IOException
    {
		final File adc = new File(ClassLoader.getSystemResource("unencrypted_adc.tar.gz").getFile());
        final boolean isUncompressSuccess = uncompressUtil.uncompressAdc(adc, destinationDir);
        log.info("uncompress status:" + isUncompressSuccess);
        assertTrue(isUncompressSuccess);
    }

    @Test
	public void testUncompressAdcFailed(@TempDir File destinationDir)
            throws InterruptedException,
            IOException
    {
		final File adc = null;
        final boolean isUncompressSuccess = uncompressUtil.uncompressAdc(adc, destinationDir);
        log.info("uncompress status:" + isUncompressSuccess);
        assertFalse(isUncompressSuccess);
    }

    //	/**
    //	 * Test to check if the kafka message to next service is converted to string and
    //	 * contains uncompressed destination directory in the kafka message string.
    //	 *
    //	 * @throws JsonProcessingException
    //	 */
    //	@Test
    //	public void testUpdateKafkaMessage() throws JsonProcessingException {
    //		UncompressActor.UncompressMessage uncompressMessage = new UncompressActor.UncompressMessage(decryptedAdc,
    //				destinationDir, kafkaMessage);
    //		String updatedKafkaMessage = uncompressUtil.updateKafkaMessage(uncompressMessage);
    //		Assert.assertNotNull(updatedKafkaMessage);
    //		Assert.assertTrue(updatedKafkaMessage.contains(uncompressMessage.getDestinationDir().getName()));
    //	}

    //	/**
    //	 * Test by passing null arguments. NPE test.
    //	 *
    //	 * @throws JsonProcessingException
    //	 */
    //	@Test
    //	public void testUpdateKafkaMessageFailed() throws JsonProcessingException {
    //		String updatedKafkaMessage = uncompressUtil.updateKafkaMessage(null);
    //		Assert.assertNull(updatedKafkaMessage);
    //	}
}
