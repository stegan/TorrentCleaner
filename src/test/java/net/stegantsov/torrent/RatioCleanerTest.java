package net.stegantsov.torrent;

import net.stegantsov.torrent.cleaner.RatioCleaner;
import net.stegantsov.torrent.client.Client;
import net.stegantsov.torrent.client.TorrentData;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: stegan
 * Date: 21.10.13
 * Time: 2:00
 */
public class RatioCleanerTest {
    @Test
    public void testSimpleFunctionality() {
        final List<TorrentData> allTorrents = new ArrayList<>();
        final List<String> expectedDeleted = new ArrayList<>();

        TorrentData torrent = new TorrentData();
        torrent.setPercentProgress(TorrentData.MAX_PERCENT);
        torrent.setStatus(TorrentData.CHECKED + TorrentData.LOADED + TorrentData.QUEUED + TorrentData.STARTED);
        torrent.setRatio(1);
        torrent.setHash("1");
        allTorrents.add(torrent);

        torrent = new TorrentData();
        torrent.setPercentProgress(TorrentData.MAX_PERCENT);
        torrent.setStatus(TorrentData.CHECKED + TorrentData.LOADED + TorrentData.QUEUED + TorrentData.STARTED);
        torrent.setRatio(5);
        torrent.setHash("2");
        allTorrents.add(torrent);
        expectedDeleted.add("2");

        torrent = new TorrentData();
        torrent.setPercentProgress(TorrentData.MAX_PERCENT);
        torrent.setStatus(TorrentData.CHECKED + TorrentData.LOADED + TorrentData.QUEUED + TorrentData.STARTED);
        torrent.setRatio(6);
        torrent.setHash("3");
        allTorrents.add(torrent);
        expectedDeleted.add("3");

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

        new RatioCleaner(stubClient, 0, 5).run();
        assertTrue(actualDeleted.containsAll(expectedDeleted)
                && expectedDeleted.containsAll(actualDeleted));
    }
}
