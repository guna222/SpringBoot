package com.hpe.onelytics.uncompress.actors;

import java.util.stream.Stream;

import akka.actor.ActorRef;

/**
 * Microapp.
 *
 * @author Onelytics
 *
 */
public class ClusterApp
{
    String configFileLocation;
    String kafkaConsumerProps;
    String kafkaProducerProps;
    static ActorRef consumerActor;

    public ClusterApp(final String configFileLocation, final String kafkaConsumerProps, final String kafkaProducerProps)
    {
        super();
        this.configFileLocation = configFileLocation;
        this.kafkaConsumerProps = kafkaConsumerProps;
        this.kafkaProducerProps = kafkaProducerProps;
    }

    public static boolean start(final String[] args)
    {
        if (args.length == 3)
        {
            final Stream<String> arguments = Stream.of(args[0], args[1], args[2]);
            if (arguments.anyMatch(arg -> arg == null))
            {
                clusterArgumentError();
            }
            else
            {
                final ClusterApp clusterApp = new ClusterApp(args[0], args[1], args[2]);
                clusterApp.startConsumer();
                return true;
            }
        }
        else
        {
            clusterArgumentError();
        }
        return false;
    }

    private static void clusterArgumentError()
    {
        final String argumentsError = "Missing arguments:"
                + System.lineSeparator()
                + "Correct format:"
                + System.lineSeparator()
                + "App-MainClass application.conf consumer.properties producer.properties";
        System.err.println(argumentsError);
    }

    public void startConsumer()
    {
        //		final Config config = ConfigFactory.parseFile(new File(configFileLocation));
        //		// Create an Akka system
        //		ActorSystem system = ActorSystem.create("uncompress-adc-service", config);
        //		// create kafka consumer for ADC data.
        //		consumerActor = system.actorOf(ConsumerActor.props(kafkaConsumerProps, config));
        //		CommonUtil.KAFKA_PRODUCER_PROPS = kafkaProducerProps;
        //		String inTopics = config.getString("application.in.topics");
        //		ConsumerActor.SubscribeTopicMessage subscribeMessage = new ConsumerActor.SubscribeTopicMessage(inTopics);
        //		consumerActor.tell(subscribeMessage, ActorRef.noSender());
        //		ConsumerActor.ListenToTopicMessage listenToTopic = new ConsumerActor.ListenToTopicMessage(inTopics);
        //		consumerActor.tell(listenToTopic, ActorRef.noSender());
    }

    public static boolean stop()
    //            throws InterruptedException
    {
        //		ConsumerActor.runActor = false;
        //		consumerActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
        return true;
    }

    public static void main(final String[] args)
    {
        ClusterApp.start(args);
    }
}
