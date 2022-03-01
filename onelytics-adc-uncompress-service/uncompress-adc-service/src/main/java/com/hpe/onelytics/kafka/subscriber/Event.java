//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.typesafe.config.Config;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event
{
    String deviceSid;
    String deviceSn;
    String fileClass;
    String fileName;
    String fileSize;
    String fileUrl;
    String fileUUID;
    String productClass;
    String productID;
    Date uploadDate;

    @JsonProperty("Device-SID")
    public String getDeviceSid()
    {
        return deviceSid;
    }

    @JsonProperty("Device-SID")
    public void setDeviceSid(final String deviceSid)
    {
        this.deviceSid = deviceSid;
    }

    @JsonProperty("Device-SN")
    public String getDeviceSn()
    {
        return deviceSn;
    }

    @JsonProperty("Device-SN")
    public void setDeviceSn(final String deviceSn)
    {
        this.deviceSn = deviceSn;
    }

    @JsonProperty("File-Class")
    public String getFileClass()
    {
        return fileClass;
    }

    @JsonProperty("File-Class")
    public void setFileClass(final String fileClass)
    {
        this.fileClass = fileClass;
    }

    @JsonProperty("File-Name")
    public String getFileName()
    {
        return fileName;
    }

    @JsonProperty("File-Name")
    public void setFileName(final String fileName)
    {
        this.fileName = fileName;
    }

    @JsonProperty("File-Size")
    public String getFileSize()
    {
        return fileSize;
    }

    @JsonProperty("File-Size")
    public void setFileSize(final String fileSize)
    {
        this.fileSize = fileSize;
    }

    @JsonProperty("File-URL")
    public String getFileUrl()
    {
        return fileUrl;
    }

    @JsonProperty("File-URL")
    public void setFileUrl(final String fileUrl)
    {
        this.fileUrl = fileUrl;
    }

    @JsonProperty("File-UUID")
    public String getFileUUID()
    {
        return fileUUID;
    }

    @JsonProperty("File-UUID")
    public void setFileUUID(final String fileUUID)
    {
        this.fileUUID = fileUUID;
    }

    @JsonProperty("Product-Class")
    public String getProductClass()
    {
        return productClass;
    }

    @JsonProperty("Product-Class")
    public void setProductClass(final String productClass)
    {
        this.productClass = productClass;
    }

    @JsonProperty("Product-ID")
    public String getProductID()
    {
        return productID;
    }

    @JsonProperty("Product-ID")
    public void setProductID(final String productID)
    {
        this.productID = productID;
    }

    @JsonProperty("Upload-Date")
    public Date getUploadDate()
    {
        return uploadDate;
    }

    @JsonProperty("Upload-Date")
    public void setUploadDate(final Date uploadDate)
    {
        this.uploadDate = uploadDate;
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
