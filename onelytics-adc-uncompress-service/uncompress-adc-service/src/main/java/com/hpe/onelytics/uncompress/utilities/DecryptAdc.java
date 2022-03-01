/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.utilities;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.hp.ci.mgmt.supportdump.util.DecryptionUtil;
import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;
import com.typesafe.config.Config;

/**
 * This class will decrypt the ADC. Input: Encrypted ADC Ouput: Decrypted ADC.
 * The class first copies the ADC to destination Dir and uses Decryption Utility
 * to decrypt the ADC.
 *
 * @author Onelytics
 *
 */
public class DecryptAdc
{
    // TODO pass this logger from actor class.
    private final Logger log = LoggerFactory.getLogger(DecryptAdc.class.getSimpleName());
    private final KafkaMessage kafkaMessage;

    public DecryptAdc(final KafkaMessage kafkaMessage)
    {
        this.kafkaMessage = kafkaMessage;
        setLogMDC(kafkaMessage);
    }

    /*
     * Must for diagnostic logging of "fileUUID" and "stationID" to track the
     * request across all microservices.
     */
    private void setLogMDC(final KafkaMessage kafkaMessage)
    {
        if (kafkaMessage == null)
        {
            log.warn("Skipping to set Diagnostic MDC for:" + this.getClass().getName());
        }
        else
        {
            MDC.put("fileUuid", kafkaMessage.getFileUUID());
            MDC.put("stationId", kafkaMessage.getDeviceSid());
        }
    }

    /**
     * decrypt the ADC(Appliance Data Collection).
     *
     * @param adc ADC file
     * @param destinationDir Destination Directory
     * @param config application configuration
     * @return
     * @throws IOException
     */
	public File decrypt(final File adc, final File destinationDir, final Config config, DecryptionUtil decryptionTool)
            throws IOException
    {
		File decryptedFile = null;
        log.info("Started decrypting-------------------------------------------");
		if (adc == null || destinationDir == null || config == null) {
			log.warn("Arguments adc file, destination Dir and condig might be null");
			return null;
		}
		if (adc.exists() && destinationDir.exists())
        {
            final File destinationAdcFile = new File(destinationDir.getAbsolutePath() + File.separator + adc.getName());
            log.info("destination adcFile:" + destinationAdcFile);
            if (copyFileUsingChannel(adc, destinationAdcFile))
            {
                final String[] args = {
                        destinationAdcFile.getAbsolutePath()
                };
                log.info("destinationDir:" + destinationDir);
				decryptionTool.mainDumpDecrypt(args);
                log.info("destinationDir contents:" + destinationDir.list());
                decryptedFile = checkDecryptionFileExists(destinationDir, config);
            }
		} else {
			log.warn(
					"Failed for one of the following reasons 1. No ADC file exists to decrypt. 2. No destination directory exists. ADC file:"
							+ adc.getAbsolutePath() + ", desitnation directory:");
        }
        log.info("completed decrypting--------------------------------------");
        return decryptedFile;
    }

    /**
     * Check decrypted ADC file exists.
     *
     * @param destinationDir
     * @param config
     * @return
     */
    public File checkDecryptionFileExists(final File destinationDir, final Config config)
    {
        final File decryptedFile = null;
        if (destinationDir.exists())
        {
            log.info("destinationDir:" + destinationDir);
            final File[] files = destinationDir.listFiles();
            for (final File filename : files)
            {
                if (config != null)
                {
                    final String decrypted_filename = config.getString("application.decrypted.filename");
                    if (filename.getName().contains(decrypted_filename))
                    {
                        return filename;
                    }
                }
            }
        }
        return decryptedFile;
    }

    /**
     * Copy file from source to destination directory.
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public boolean copyFileUsingChannel(final File source, final File dest)
            throws IOException
    {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try
        {
            sourceChannel = new RandomAccessFile(source, "rw").getChannel();
            destChannel = new RandomAccessFile(dest, "rw").getChannel();
            final long bytesTransfered = destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            log.info("total bytes transferred vs source bytes" + bytesTransfered + ":" + sourceChannel.size());
            if (dest.exists() && bytesTransfered > 0)
            {
                return true;
            }
        }
        finally
        {
            if (sourceChannel != null)
            {
                sourceChannel.close();
            }
            if (destChannel != null)
            {
                destChannel.close();
            }
        }
        return false;
    }
}
