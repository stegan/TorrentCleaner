package net.stegantsov.torrent.cleaner;

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

    protected AbstractCleaner(Client client,
                              long checkPeriod) {
        this.client = client;
        this.checkPeriod = checkPeriod;
    }

    @Override
    public void run() {
        LOG.debug(Thread.currentThread() + ":" + super.toString() + " Started new cleaner. Client: " + client
                + ". Check period: " + checkPeriod);
        while (true) {
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
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + ". Sleep");
                    Thread.sleep(checkPeriod);
                } else {
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + ". Finish");
                    break;
                }
            } catch (IOException e) {
                LOG.error(Thread.currentThread() + ":" + super.toString() + ". IOException: " + e);
                break;
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    protected abstract List<TorrentData> perform(final List<TorrentData> torrents);
}
