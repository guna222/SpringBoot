/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil
{
    private final static Logger log = LoggerFactory.getLogger(CommonUtil.class.getSimpleName());

    public static File makeDestinatonDir(
            final com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage message,
            final String defaultLocation)
    {
        File destinationDir = null;
        final String subDirectoryWithDateStr = getFormattedDate(message);
		if (subDirectoryWithDateStr != null && message.getDeviceSid() != null && message.getFileUUID() != null)
        {
            destinationDir = new File(
                    defaultLocation
                            + File.separator
                            + message.getDeviceSid()
                            + File.separator
                            + subDirectoryWithDateStr
                            + File.separator
							+ message.getFileUUID());
            destinationDir.mkdirs();
        }
        else
        {
			log.warn(
					"Missing parameters like device-SID, upload date and file UUID. Hence saving data in default location:"
							+ defaultLocation);
            destinationDir = new File(defaultLocation);
        }
        return destinationDir;
    }

    public static String getFormattedDate(final com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage message)
    {
        final Date subDirectoryWithDate = message.getUploadDate();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        final String subDirectoryWithDateStr = dateFormat.format(subDirectoryWithDate);
        return subDirectoryWithDateStr;
    }
}
