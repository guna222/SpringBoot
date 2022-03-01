//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber.impl;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;

import lombok.Value;

/**
 * TODO: need to re-look at the need to CQRS for this micro service
 * 
 */
@SuppressWarnings("serial")
@Value
@JsonDeserialize
public final class OutgoingEventState implements CompressedJsonable
{

    public final String deviceSid;
    public final String deviceSn;
    public final String fileClass;
    public final String fileName;
    public final String fileSize;
    public final String fileUrl;
    public final String fileUUID;
    public final String productClass;
    public final String productID;
    public final Date uploadDate;

    @JsonCreator
    public OutgoingEventState(
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
        this.deviceSid = Preconditions.checkNotNull(deviceSid, "deviceSid");
        this.deviceSn = Preconditions.checkNotNull(deviceSn, "deviceSn");
        this.fileClass = Preconditions.checkNotNull(fileClass, "fileClass");
        this.fileName = Preconditions.checkNotNull(fileName, "fileName");
        this.fileSize = Preconditions.checkNotNull(fileSize, "fileSize");
        this.fileUrl = Preconditions.checkNotNull(fileUrl, "fileUrl");
        this.fileUUID = Preconditions.checkNotNull(fileUUID, "fileUUID");
        this.productClass = Preconditions.checkNotNull(productClass, "productClass");
        this.productID = Preconditions.checkNotNull(productID, "productID");
        this.uploadDate = Preconditions.checkNotNull(uploadDate, "uploadDate");
    }
}
