package net.stegantsov.torrent.cleaner;

import net.stegantsov.torrent.client.Client;
import net.stegantsov.torrent.client.TorrentData;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Cleaner base on torrent's upload speed.
 * <p/>
 * User: stegan
 * Date: 20.10.13
 * Time: 21:21
 */
public final class SpeedCleaner extends AbstractCleaner {
    private static final Logger LOG = Logger.getLogger(SpeedCleaner.class);

    private final int minUploadSpeed;
    private final long monitoringTime;
    private final Map<TorrentData, SpeedData> candidates;

    private class SpeedData {
        private long startTime;
        private List<Integer> speeds;

        private SpeedData(long startTime) {
            this.startTime = startTime;
            speeds = new ArrayList<>();
        }

        private long getStartTime() {
            return startTime;
        }

        private List<Integer> getSpeeds() {
            return Collections.unmodifiableList(speeds);
        }

        private void addSpeed(int speed) {
            speeds.add(speed);
        }

        @Override
        public String toString() {
            return "SpeedData{" +
                    "startTime=" + startTime +
                    ", speeds=" + speeds +
                    '}';
        }
    }

    /**
     * Base constructor.
     *
     * @param client         - {@link net.stegantsov.torrent.client.Client}.
     * @param checkPeriod    - period of sleep in ms between checks.
     * @param errorPeriod    - period of sleep in ms for next check if error occupied.
     * @param minUploadSpeed - minimum upload speed for saving torrent.
     * @param monitoringTime - monitoring time for average speed.
     */
    public SpeedCleaner(final Client client,
                        final long checkPeriod,
                        final long errorPeriod,
                        final int minUploadSpeed,
                        final long monitoringTime) {
        super(client,
                checkPeriod,
                errorPeriod);
        this.minUploadSpeed = minUploadSpeed;
        this.monitoringTime = monitoringTime;

        candidates = new HashMap<>();
    }

    @Override
    protected List<TorrentData> perform(final List<TorrentData> torrents) {
        List<TorrentData> loadedTorrents = new ArrayList<>();
        for (TorrentData torrent : torrents) {
            if (torrent.isDownloaded()) {
                loadedTorrents.add(torrent);
            }
        }

        //remove zombies
        for (Iterator<Map.Entry<TorrentData, SpeedData>> iterator = candidates.entrySet().iterator();
             iterator.hasNext();
                ) {
            Map.Entry<TorrentData, SpeedData> entry = iterator.next();
            if (!loadedTorrents.contains(entry.getKey())) {
                iterator.remove();
            }
        }


        for (TorrentData torrent : loadedTorrents) {
            if (torrent.getUploadSpeed() < minUploadSpeed) {
                if (!candidates.containsKey(torrent)) {
                    SpeedData speedData = new SpeedData(System.currentTimeMillis());
                    speedData.addSpeed(torrent.getUploadSpeed());
                    candidates.put(torrent, speedData);
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + " Adding new candidate: " + torrent);
                } else {
                    candidates.get(torrent).addSpeed(torrent.getUploadSpeed());
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + " Updating candidate: " + torrent);
                }
            } else {
                if (candidates.containsKey(torrent)) {
                    candidates.get(torrent).addSpeed(torrent.getUploadSpeed());
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + " Updating candidate: " + torrent);
                }
            }
        }
        LOG.debug(Thread.currentThread() + ":" + super.toString() + " Candidates map: " + candidates);

        List<TorrentData> toDelete = new ArrayList<>();
        for (Iterator<Map.Entry<TorrentData, SpeedData>> iterator = candidates.entrySet().iterator();
             iterator.hasNext();
                ) {
            Map.Entry<TorrentData, SpeedData> entry = iterator.next();
            if ((System.currentTimeMillis() - entry.getValue().getStartTime()) >= monitoringTime) {
                //calculating average speed
                int total = 0;
                List<Integer> speeds = entry.getValue().getSpeeds();
                for (Integer speed : speeds) {
                    total += speed;
                }
                int average = total / speeds.size();
                if (average < minUploadSpeed) {
                    LOG.debug(Thread.currentThread() + ":" + super.toString() + " Torrent should be deleted by upload "
                            + "speed. Torrent: " + entry.getKey() + ". Average speed: " + average);
                    toDelete.add(entry.getKey());
                    iterator.remove();
                }
            }
        }

        return toDelete;
    }

    @Override
    public String toString() {
        return "SpeedCleaner{" +
                "minUploadSpeed=" + minUploadSpeed +
                ", monitoringTime=" + monitoringTime +
                ", candidates=" + candidates +
                '}';
    }
}
