//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.typesafe.config.Config;

import lombok.Value;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KafkaEvent.KafkaMessage.class, name = "inkafka-message-changed")
})

public interface KafkaEvent
{

    public String getDeviceSid();

    public String getDeviceSn();

    public String getFileClass();

    public String getFileName();

    public String getFileSize();

    public String getFileUrl();

    public String getFileUUID();

    public String getProductClass();

    public String getProductID();

    public Date getUploadDate();

    @Value
    public class KafkaMessage implements KafkaEvent
    {
        @JsonProperty("Device-SID")
        String deviceSid;
        @JsonProperty("Device-SN")
        String deviceSn;
        @JsonProperty("File-Class")
        String fileClass;
        @JsonProperty("File-Name")
        String fileName;
        @JsonProperty("File-Size")
        String fileSize;
        @JsonProperty("File-URL")
        String fileUrl;
        @JsonProperty("File-UUID")
        String fileUUID;
        @JsonProperty("Product-Class")
        String productClass;
        @JsonProperty("Product-ID")
        String productID;
        @JsonProperty("Upload-Date")
        Date uploadDate;

        @JsonCreator
        public KafkaMessage(
                final String deviceSid,
                final String deviceSn,
                final String fileClass,
                final String fileName,
                final String fileSize,
                final String fileUrl,
                final String fileUUID,
                final String productClass,
                final String productID,
                final Date uploadDate)
        {

            System.out.println(deviceSid + "," + "deviceSn");
			this.deviceSid = deviceSid;
			this.deviceSn = deviceSn;
			this.fileClass = fileClass;
			this.fileName = fileName;
			this.fileSize = fileSize;
			this.fileUrl = fileUrl;
			this.fileUUID = fileUUID;
			this.productClass = productClass;
			this.productID = productID;
			this.uploadDate = uploadDate;
        }

        @Override
        public String getDeviceSid()
        {
            return deviceSid;
        }

        @Override
        public String getDeviceSn()
        {
            return deviceSn;
        }

        @Override
        public String getFileClass()
        {
            return fileClass;
        }

        @Override
        public String getFileName()
        {
            return fileName;
        }

        @Override
        public String getFileSize()
        {
            return fileSize;
        }

        @Override
        public String getFileUrl()
        {
            return fileUrl;
        }

        @Override
        public String getFileUUID()
        {
            return fileUUID;
        }

        @Override
        public String getProductClass()
        {
            return productClass;
        }

        @Override
        public String getProductID()
        {
            return productID;
        }

        @Override
        public Date getUploadDate()
        {
            return uploadDate;
        }

        @Override
        public String toString()
        {
            return "KafkaMessage [deviceSid="
                    + deviceSid
                    + ", deviceSn="
                    + deviceSn
                    + ", fileClass="
                    + fileClass
                    + ", fileName="
                    + fileName
                    + ", fileSize="
                    + fileSize
                    + ", fileUrl="
                    + fileUrl
                    + ", fileUUID="
                    + fileUUID
                    + ", productClass="
                    + productClass
                    + ", productID="
                    + productID
                    + ", uploadDate="
                    + uploadDate
                    + "]";
        }

        /**
         * Creates a directory structure as follows:
         *
         * @param config
         * @return
         */
        // TODO remove the UUID part for final packaging
        public File makeDestinatonDir(final Config config)
        {
            File destinationDir = null;
            final String subDirectoryWithDateStr = getFormattedDate();
            if (subDirectoryWithDateStr != null && getDeviceSid() != null)
            {
                destinationDir = new File(config.getString("application.adc.umcompress.location")
                        + File.separator
                        + getDeviceSid()
                        + File.separator
                        + subDirectoryWithDateStr
                        + File.separator
                        + UUID.randomUUID().toString());
            }
            else
            {
                destinationDir = new File(config.getString("application.adc.umcompress.location"));
            }
            destinationDir.mkdirs();
            return destinationDir;
        }

        @JsonIgnore
        public String getFormattedDate()
        {
            final Date subDirectoryWithDate = getUploadDate();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            final String subDirectoryWithDateStr = dateFormat.format(subDirectoryWithDate);
            return subDirectoryWithDateStr;
        }
    }

}
