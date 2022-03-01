//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.utilities;

public class ApplicationConstants
{

    /*
     * The consts defined here maps to the keys in application.conf
     */
    public final static String ERROR_TOPIC = "application.error.topics";
    public final static String OUT_KAFKA_TOPIC = "application.uncompress.out.topic";
    public final static String KAFKA_BROKER = "application.kafka.message.brokers";
    public final static String KAFKA_PRODUCER_CONFIG = "akka.kafka.producer";

    public final static String UNCOMPRESS_DEFAULT_LOCATION = "application.umcompress.default.location";

    public final static String KAFKA_GROUPID = "application.kafka.uncompress.topic.groupid";

    public final static String SERVICE_NAME = "application.uncompress.servicename";

    public final static String IN_TOPIC = "application.uncompress.in.topic";
}
