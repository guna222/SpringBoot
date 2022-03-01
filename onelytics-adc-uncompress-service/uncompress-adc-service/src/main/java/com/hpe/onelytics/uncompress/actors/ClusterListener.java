/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.onelytics.consumer.standalone_java_consumer.KafkaClient;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

/**
 * Microapp listener.
 *
 * @author Onelytics
 *
 */
public class ClusterListener extends AbstractActor
{
    private final Logger log = LoggerFactory.getLogger(ClusterListener.class.getSimpleName());
    Cluster cluster = Cluster.get(getContext().system());

    // subscribe to cluster changes
    @Override
    public void preStart()
    {
        log.info("Subsribing for initialStateAsEvents, MemberEvent, UnreachableMember");

        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
    }

    // re-subscribe when restart
    @Override
    public void postStop()
    {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive()
    {
        return receiveBuilder().match(MemberUp.class, mUp -> {
            log.info("Member is Up: {}", mUp.member());
        }).match(UnreachableMember.class, mUnreachable -> {
            log.info("Member detected as unreachable: {}", mUnreachable.member());
        }).match(MemberRemoved.class, mRemoved -> {
            log.info("Member is Removed: {}", mRemoved.member());
        }).match(MemberEvent.class, message -> {
            // ignore
        }).build();
    }
}
