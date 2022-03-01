/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;

/**
 * Uncompress utility class.
 *
 * @author Onelytics
 *
 */
public class UncompressAdc
{
    private final Logger log = LoggerFactory.getLogger(UncompressAdc.class.getSimpleName());
    private final KafkaMessage kafkaMessage;

    public UncompressAdc(final KafkaMessage kafkaMessage)
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
     * Uncompress the supplied AD to given destination directory.
     *
     * TODO:Verify whether input streams are closed
     *
     * @param adc decrypted adc file
     * @param destinationDir target directory where uncompressed files are
     *        copied
     * @return boolean whether uncompress is success or not
     */
    public boolean uncompressAdc(final File adc, final File destinationDir)
    {
        try
        {
			if (adc == null || destinationDir == null) {
				return false;
			}
            final TarArchiveInputStream fin = new TarArchiveInputStream(
                    new GzipCompressorInputStream(new FileInputStream((adc))));
            TarArchiveEntry entry = null;
            while ((entry = fin.getNextTarEntry()) != null)
            {
                if (entry.isDirectory())
                {
                    continue;
                }
                final File curfile = new File(destinationDir, entry.getName());
                final File parent = curfile.getParentFile();
                if (!parent.exists())
                {
                    parent.mkdirs();
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
            return true;
        }
		catch (final IOException e)
        {
            log.warn("Failed to extract the ADC:" + adc.getAbsolutePath() + ", failure message:" + e.getMessage());
            return false;
        }
    }
}
