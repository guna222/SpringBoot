//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber.impl;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

import akka.Done;
import lombok.Value;

/**
 * TODO: need to re-look at the need to CQRS for this micro service
 * This interface defines all the commands that the Hello entity supports.
 *
 * By convention, the commands should be inner classes of the interface, which
 * makes it simple to get a complete picture of what commands an entity
 * supports.
 */
public interface ServiceCommand extends Jsonable
{

    /**
     * A command to switch the greeting message.
     * <p>
     * It has a reply type of {@link akka.Done}, which is sent back to the
     * caller
     * when all the events emitted by this command are successfully persisted.
     */
    @SuppressWarnings("serial")
    @Value
    @JsonDeserialize
    final class StoreOutgoingMessage implements ServiceCommand, CompressedJsonable, PersistentEntity.ReplyType<Done>
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
        public StoreOutgoingMessage(
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

    /**
     * A command to say hello to someone using the current greeting message.
     * <p>
     * The reply type is String, and will contain the message to say to that
     * person.
     */
    @SuppressWarnings("serial")
    @Value
    @JsonDeserialize
    final class Hello implements ServiceCommand, PersistentEntity.ReplyType<String>
    {

        public final String name;

        @JsonCreator
        public Hello(final String name)
        {
            this.name = Preconditions.checkNotNull(name, "name");
        }
    }

}
