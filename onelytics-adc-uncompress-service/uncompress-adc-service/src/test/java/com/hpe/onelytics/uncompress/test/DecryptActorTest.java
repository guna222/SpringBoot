/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.test;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;

public class DecryptActorTest
{
    File encryptedAdc;
    File destinationDir;
    KafkaMessage kafkaMessage;
    Long offset;
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
	@Disabled
    public void testDecryptMessage()
            throws IOException
    {
        this.getClass().getClassLoader();
        encryptedAdc = new File(ClassLoader.getSystemResource("encrypted_adc.tar.gz").getFile());
        destinationDir = tempFolder.newFolder("adc_data_test");
        //kafkaMessage = new KafkaMessage();
        offset = 0l;
        //		DecryptActor.DecryptMessage decryptMessage = new DecryptActor.DecryptMessage(encryptedAdc, destinationDir,
        //				kafkaMessage, offset);
        //		Assert.assertNotNull(decryptMessage);
        //		Assert.assertNotNull(decryptMessage.getAdcLocation());
        //		Assert.assertNotNull(decryptMessage.getDestinationDir());
        //		Assert.assertNotNull(decryptMessage.getKafkaMessage());
        //		Assert.assertEquals(0l, decryptMessage.getOffset());
    }
}
