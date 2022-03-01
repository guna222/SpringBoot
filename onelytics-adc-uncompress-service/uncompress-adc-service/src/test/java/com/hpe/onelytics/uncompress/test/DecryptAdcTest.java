/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.hp.ci.mgmt.supportdump.util.DecryptionUtil;
import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;
import com.hpe.onelytics.uncompress.utilities.DecryptAdc;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Test for DecryptAdc Utility.
 *
 * @author Onelytics
 *
 */
public class DecryptAdcTest
{
    File adc_unencrypted;
    File adc_encrypted;
    DecryptAdc decryptAdc;
    Config config;
    KafkaMessage kafkaMessage;
	DecryptionUtil decryptionTool = new DecryptionUtil();

	@BeforeEach
	public void setup()
            throws IOException
    {
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
        this.getClass().getClassLoader();
        adc_unencrypted = new File(ClassLoader.getSystemResource("unencrypted_adc.tar.gz").getFile());
        this.getClass().getClassLoader();
        adc_encrypted = new File(
                ClassLoader.getSystemResource("encrypted_adc.tar.gz").getFile());
        decryptAdc = new DecryptAdc(kafkaMessage);
        config = ConfigFactory.defaultApplication();
    }

    @Test
	@DisplayName("Negative test case - Tries to decypt an already unencrypted file.")
	public void failDecrypt(@TempDir File destinationDir)
            throws IOException
    {
		final File decryptedFile = decryptAdc.decrypt(adc_unencrypted, destinationDir, config, decryptionTool);
        assertNull(decryptedFile);
    }

    @Test
	@DisplayName("Positive test case - decrypting an unencrypted file.")
	public void passDecrypt(@TempDir File destinationDir)
            throws IOException
    {
		final File decryptedFile = decryptAdc.decrypt(adc_encrypted, destinationDir, config, decryptionTool);
        assertNotNull(decryptedFile);
        final String adcFilename = config.getString("application.decrypted.filename");
        assertTrue(decryptedFile.getAbsolutePath().contains(adcFilename));
    }

    @Test
	@DisplayName("Negative test case - Wrong Destination Directory.")
	public void failDecryptWithWrongDestinationPath(@TempDir File destinationDir)
            throws IOException
    {
        destinationDir = new File("wrong_destination_path");
		final File decryptedFile = decryptAdc.decrypt(adc_encrypted, destinationDir, config, decryptionTool);
        assertNull(decryptedFile);
    }

    @Test
	@DisplayName("Negative test case - No encrypted ADC file exists")
	public void failDecryptWithWrongAdcPath(@TempDir File destinationDir)
            throws IOException
    {
        adc_encrypted = new File("wrong_adc_file");
		final File decryptedFile = decryptAdc.decrypt(adc_encrypted, destinationDir, config, decryptionTool);
        assertNull(decryptedFile);
    }

    @Test
	@DisplayName("Negative test case - Application configuration missing.")
	public void decryptWithNoConfig(@TempDir File destinationDir)
            throws IOException
    {
        config = null;
		final File decryptedFile = decryptAdc.decrypt(adc_encrypted, destinationDir, config, decryptionTool);
        assertNull(decryptedFile);
    }

    @Test
	@DisplayName("Negative test case - No destination file exists for copying ADC encrypted source file.")
    public void testcopyFileUsingChannelWithNoDestinationFile()
            throws IOException
    {
        final File source = new File(this.getClass().getClassLoader().getResource("source.txt").getFile());
        final File dest = new File("no_file");
        assertTrue(decryptAdc.copyFileUsingChannel(source, dest));
    }
}
