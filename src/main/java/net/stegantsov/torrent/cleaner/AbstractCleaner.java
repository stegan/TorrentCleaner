package net.stegantsov.torrent.cleaner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import net.stegantsov.torrent.client.Client;
import net.stegantsov.torrent.client.TorrentData;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Abstract cleaner.
 * <p/>
 * User: stegan
 * Date: 20.10.13
 * Time: 21:20
 */
public abstract class AbstractCleaner implements Cleaner {
    private static final Logger LOG = Logger.getLogger(AbstractCleaner.class);

    private final Client client;
    private final long checkPeriod;
    private final long errorPeriod;

    protected AbstractCleaner(Client client,
                              long checkPeriod,
                              long errorPeriod) {
        this.client = client;
        this.checkPeriod = checkPeriod;
        this.errorPeriod = errorPeriod;
    }

    @Override
    public void run() {
        LOG.debug(Thread.currentThread() + ":" + super.toString() + " Started new cleaner. Client: " + client
                + ". Check period: " + checkPeriod);
        while (true) {
            try {
                try {
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + " Start");
                    List<TorrentData> torrents = client.getTorrents();
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + " Torrents for client: " + torrents);
                    List<TorrentData> toDelete = perform(torrents);
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + " Torrents that will be deleted: "
                            + toDelete);
                    for (TorrentData torrent : toDelete) {
                        LOG.info(this + ". Torrent with hash " + torrent.getHash() + " will be deleted for client "
                                + client);
                        client.delete(torrent.getHash());
                    }
                    if (checkPeriod > 0) {
                        LOG.debug(Thread.currentThread() + ":" + super.toString() + ". Sleep for " + checkPeriod
                                + " ms");
                        Thread.sleep(checkPeriod);
                    } else {
                        LOG.debug(Thread.currentThread() + ":" + super.toString() + ". Finish");
                        break;
                    }
                } catch (FailingHttpStatusCodeException | IOException e) {
                    LOG.error(Thread.currentThread() + ":" + super.toString() + ". " + e);
                    if (errorPeriod > 0) {
                        LOG.debug(Thread.currentThread() + ":" + super.toString() + ". Sleep because of error for "
                                + errorPeriod + " ms");
                        Thread.sleep(errorPeriod);
                    } else {
                        LOG.debug(Thread.currentThread() + ":" + super.toString() + ". Finish because of error");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    protected abstract List<TorrentData> perform(final List<TorrentData> torrents);
}
