package net.stegantsov.torrent;

import net.stegantsov.torrent.cleaner.SpeedCleaner;
import net.stegantsov.torrent.client.Client;
import net.stegantsov.torrent.client.TorrentData;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * User: stegan
 * Date: 21.10.13
 * Time: 2:25
 */
public class SpeedCleanerTest {
    @Test
    public void testSimpleFunctionality() {
        final List<TorrentData> allTorrents = new ArrayList<>();
        final List<String> expectedDeleted = new ArrayList<>();

        TorrentData torrent = new TorrentData();
        torrent.setPercentProgress(TorrentData.MAX_PERCENT);
        torrent.setStatus(TorrentData.CHECKED + TorrentData.LOADED + TorrentData.QUEUED + TorrentData.STARTED);
        torrent.setUploadSpeed(10000);
        torrent.setHash("1");
        allTorrents.add(torrent);

        torrent = new TorrentData();
        torrent.setPercentProgress(TorrentData.MAX_PERCENT);
        torrent.setStatus(TorrentData.CHECKED + TorrentData.LOADED + TorrentData.QUEUED + TorrentData.STARTED);
        torrent.setUploadSpeed(15000);
        torrent.setHash("2");
        allTorrents.add(torrent);

        torrent = new TorrentData();
        torrent.setPercentProgress(TorrentData.MAX_PERCENT);
        torrent.setStatus(TorrentData.CHECKED + TorrentData.LOADED + TorrentData.QUEUED + TorrentData.STARTED);
        torrent.setUploadSpeed(9000);
        torrent.setHash("3");
        allTorrents.add(torrent);
        expectedDeleted.add("3");

        torrent = new TorrentData();
        torrent.setPercentProgress(TorrentData.MAX_PERCENT);
        torrent.setStatus(TorrentData.CHECKED + TorrentData.LOADED + TorrentData.QUEUED + TorrentData.STARTED);
        torrent.setUploadSpeed(0);
        torrent.setHash("4");
        allTorrents.add(torrent);
        expectedDeleted.add("4");

        final List<String> actualDeleted = new ArrayList<>();
        Client stubClient = new Client() {
            @Override
            public List<TorrentData> getTorrents() throws IOException {
                return allTorrents;
            }

            @Override
            public void delete(String hash) throws IOException {
                actualDeleted.add(hash);
            }

            @Override
            public void close() throws IOException {
            }
        };

        new SpeedCleaner(stubClient, 0, 10000, 0).run();
        assertTrue(actualDeleted.containsAll(expectedDeleted)
                && expectedDeleted.containsAll(actualDeleted));
    }
}
