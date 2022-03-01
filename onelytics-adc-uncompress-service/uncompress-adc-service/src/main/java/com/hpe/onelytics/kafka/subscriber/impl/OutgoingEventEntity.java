//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.kafka.subscriber.impl;

import java.util.Date;
import java.util.Optional;

import com.hpe.onelytics.kafka.subscriber.KafkaEvent;
import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;
import com.hpe.onelytics.kafka.subscriber.impl.ServiceCommand.Hello;
import com.hpe.onelytics.kafka.subscriber.impl.ServiceCommand.StoreOutgoingMessage;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import akka.Done;

/**
 * TODO: need to re-look at the need to CQRS for this micro service
 * This is an event sourced entity. It has a state, {@link OutgoingEventState},
 * which
 * stores the outgoing message.
 * <p>
 * Event sourced entities are interacted with by sending them commands. This
 * entity supports two commands, a {@link StoreOutgoingMessage} command, which
 * is
 * used to change the greeting, and a {@link Hello} command, which is a read
 * only command which returns a greeting to the name specified by the command.
 * <p>
 * Commands get translated to events, and it's the events that get persisted by
 * the entity. Each event will have an event handler registered for it, and an
 * event handler simply applies an event to the current state. This will be done
 * when the event is first created, and it will also be done when the entity is
 * loaded from the database - each event will be replayed to recreate the state
 * of the entity.
 * <p>
 * This entity defines one event, the {@link IncomingEventStateChanged} event,
 * which is emitted when a {@link StoreOutgoingMessage} command is received.
 */
public class OutgoingEventEntity extends PersistentEntity<ServiceCommand, KafkaEvent, OutgoingEventState>
{

    /**
     * An entity can define different behaviours for different states, but it
     * will
     * always start with an initial behaviour. This entity only has one
     * behaviour.
     */
    @Override
    public Behavior initialBehavior(final Optional<OutgoingEventState> snapshotState)
    {

        /*
         * Behaviour is defined using a behaviour builder. The behaviour builder
         * starts with a state, if this entity supports snapshotting (an
         * optimisation that allows the state itself to be persisted to combine
         * many
         * events into one), then the passed in snapshotState may have a value
         * that
         * can be used.
         *
         * Otherwise, the default state is to use the Hello greeting.
         */
        System.out.println("snapshotState:" + snapshotState.toString());

        final BehaviorBuilder b = newBehaviorBuilder(
                snapshotState.orElse(new OutgoingEventState(
                        "Device-SID",
                        "Device-SN",
                        "File-Class",
                        "File-Name",
                        "File-Size",
                        "File-URL",
                        "File-UUID",
                        "Product-Class",
                        "Product-ID",
                        new Date())));

        /*
         * Command handler for the StoreOutgoingMessage command.
         */
        b.setCommandHandler(StoreOutgoingMessage.class, (cmd, ctx) ->
        // In response to this command, we want to first persist it as a
        // KafkaMessage event
        ctx.thenPersist(
                new KafkaMessage(
                        cmd.deviceSid,
                        cmd.deviceSn,
                        cmd.fileClass,
                        cmd.fileName,
                        cmd.fileSize,
                        cmd.fileUrl,
                        cmd.fileUUID,
                        cmd.productClass,
                        cmd.productID,
                        new Date()),
                // Then once the event is successfully persisted, we respond with done.
                evt -> ctx.reply(Done.getInstance())));

        /*
         * Event handler for the KafkaMessage event.
         */
        b.setEventHandler(
                KafkaMessage.class,
                // We simply update the current state to use the KafkaMessage message from
                // the event.
                evt -> new OutgoingEventState(
                        evt.getDeviceSid(),

                        evt.getDeviceSn(),
                        evt.getFileClass(),
                        evt.getFileName(),
                        evt.getFileSize(),
                        evt.getFileUrl(),
                        evt.getFileUUID(),
                        evt.getProductClass(),
                        evt.getProductID(),
                        evt.getUploadDate()));

        /*
         * Command handler for the Hello command.
         */
        b.setReadOnlyCommandHandler(
                Hello.class,
                // Get the greeting from the current state, and prepend it to the name
                // that we're sending
                // a greeting to, and reply with that message.
                (cmd, ctx) -> ctx.reply(state().deviceSid + ", " + cmd.name + "!"));

        /*
         * We've defined all our behaviour, so build and return it.
         */
        return b.build();
    }

}
