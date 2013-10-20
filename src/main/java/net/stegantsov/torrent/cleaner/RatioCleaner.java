package net.stegantsov.torrent.cleaner;

import net.stegantsov.torrent.client.Client;
import net.stegantsov.torrent.client.TorrentData;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Cleaner base on torrent's ratio.
 * <p/>
 * User: stegan
 * Date: 20.10.13
 * Time: 21:55
 */
public final class RatioCleaner extends AbstractCleaner {
    private static final Logger LOG = Logger.getLogger(RatioCleaner.class);

    private final double minRating;

    /**
     * Base constructor.
     *
     * @param client      - {@link net.stegantsov.torrent.client.Client}.
     * @param checkPeriod - period of sleep in ms between checks.
     * @param minRating   - minimum rating for saving torrent.
     */
    public RatioCleaner(final Client client,
                        final long checkPeriod,
                        final double minRating) {
        super(client,
                checkPeriod);
        this.minRating = minRating;
    }

    @Override
    protected List<TorrentData> perform(final List<TorrentData> torrents) {
        List<TorrentData> toDelete = new ArrayList<>();
        for (TorrentData torrent : torrents) {
            if (!torrent.isDownloaded()) {
                continue;
            }
            if (torrent.getRatio() >= minRating) {
                LOG.debug(Thread.currentThread() + ":" + super.toString() + " Torrent should be deleted by ratio. " +
                        "Torrent: " + torrent);
                toDelete.add(torrent);
            }
        }
        return toDelete;
    }

    @Override
    public String toString() {
        return "RatioCleaner{" +
                "minRating=" + minRating +
                '}';
    }
}
