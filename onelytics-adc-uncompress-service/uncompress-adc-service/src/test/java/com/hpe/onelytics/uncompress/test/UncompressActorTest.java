/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;
import com.hpe.onelytics.uncompress.actors.UncompressActor;

public class UncompressActorTest
{
    File decryptedAdc;
    KafkaMessage kafkaMessage;
    long offset = 0;

	@BeforeEach
    public void setup()
            throws IOException
    {
        this.getClass().getClassLoader();
        decryptedAdc = new File(ClassLoader.getSystemResource("unencrypted_adc.tar.gz").getFile());
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
    }

    @Test
	public void testUncompressMessage(@TempDir File destinationDir)
    {
        final UncompressActor.UncompressMessage uncompressMessage = new UncompressActor.UncompressMessage(
                decryptedAdc,
                destinationDir,
                kafkaMessage);
        assertNotNull(uncompressMessage.getDecryptedAdc());
        assertNotNull(uncompressMessage.getDestinationDir());
        assertNotNull(uncompressMessage.getKafkaMessage());
    }
}
