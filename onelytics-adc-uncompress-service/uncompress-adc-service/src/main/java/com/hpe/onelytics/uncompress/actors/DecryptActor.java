package com.hpe.onelytics.uncompress.actors;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import com.hp.ci.mgmt.supportdump.util.DecryptionUtil;
import com.hpe.onelytics.kafka.subscriber.KafkaEvent.KafkaMessage;
import com.hpe.onelytics.kafka.subscriber.impl.StreamRepository;
import com.hpe.onelytics.uncompress.utilities.ApplicationConstants;
import com.hpe.onelytics.uncompress.utilities.CommonUtil;
import com.hpe.onelytics.uncompress.utilities.DecryptAdc;

import akka.actor.ActorRef;
import akka.event.DiagnosticLoggingAdapter;
import akka.event.Logging;

/**
 * Implementation of decrypt and uncompress logic implementation
 * Once decrypt it successful, uncompress actor is invoked to uncompress the
 * decrypted ADC.
 */
public class DecryptActor extends BaseActor
{
    ActorRef uncompressActor;
	DecryptionUtil decryptionTool;

    final DiagnosticLoggingAdapter log = Logging.getLogger(this);

    /**
     * Constructor that injects repository and uncompress actor
     *
     * @param repository
     * @param uncompressActor
     */
    @Inject
    public DecryptActor(final StreamRepository repository, @Named("Uncompress") final ActorRef uncompressActor)
    {
        super(repository);
        this.uncompressActor = uncompressActor;
		this.decryptionTool = new DecryptionUtil();
    }

    /**
     * @see AbstractActor.createReceive()
     */
    @Override
    public Receive createReceive()
    {
        return receiveBuilder()
                .match(KafkaMessage.class, arg -> {
                    setLogMDC(arg, log);
                    log.debug("DecryptActor address: " + getSelf());
                    final File destinationDir = getDestinationDir(arg);
                    log.info("destination directory for uncompressed ADC:" + destinationDir.getAbsolutePath());
                    uncompressAdcAfterDecrypt(arg, decryptAdc(arg, destinationDir), destinationDir);
                }).build();

    }

    private File decryptAdc(final KafkaMessage message, final File destinationDir)
    {
        final File adcFileTobeDecrypted = new File(message.getFileUrl());
        log.debug("Decrypting ADC:" + message.toString());
        log.debug("decrypt started for adcLocation:" + destinationDir.getAbsolutePath());

        final DecryptAdc decryptUtil = new DecryptAdc(message);

        try
        {
            updateTask(message, "DECRYPT_STARTED");
            final File decryptedFile = decryptUtil
					.decrypt(adcFileTobeDecrypted, destinationDir, getContext().getSystem().settings().config(),
							decryptionTool);
            updateTask(message, "DECRYPT_COMPLETE");
            return decryptedFile;
        }
        catch (final IOException e)
        {
            final String error = "Failed to decrypt because:" + e.getMessage();
            log.warning(error);
            publishErrorMessage(error);
        }
        finally
        {
            log.clearMDC();
        }

        return null;
    }

    private void uncompressAdcAfterDecrypt(
            final KafkaMessage message,
            final File decryptedFile,
            final File destinationDir)
    {
        if (decryptedFile != null)
        {
            delegateToUncompressActor(message, destinationDir, decryptedFile);
        }
        else
        {
            final String failureMessage = "Failed to uncompress "
                    + message
                    + " to "
                    + destinationDir
                    + ". File to be decrypted is:"
                    + decryptedFile;
            log.warning(failureMessage);
            publishErrorMessage(failureMessage);
        }
    }

    private void delegateToUncompressActor(final KafkaMessage message, final File destinationDir, final File decryptedFile)
    {
        final UncompressActor.UncompressMessage uncompressMessage = new UncompressActor.UncompressMessage(
                decryptedFile,
                destinationDir,
                message);

        log.debug("Calling uncompress actor");
        uncompressActor.tell(uncompressMessage, getSelf());
    }

    private File getDestinationDir(final KafkaMessage message)
    {
        final String defaultLocation = getContext().getSystem().settings().config()
                .getString(ApplicationConstants.UNCOMPRESS_DEFAULT_LOCATION);
        final File destinationDir = CommonUtil.makeDestinatonDir(message, defaultLocation);
        return destinationDir;
    }
}
