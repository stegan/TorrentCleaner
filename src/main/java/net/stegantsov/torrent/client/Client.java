package net.stegantsov.torrent.client;

import java.io.Closeable;
import java.io.IOException;

import java.util.List;

/**
 * Torrent's client interface.
 *
 * User: stegan
 * Date: 20.10.13
 * Time: 0:48
 */
public interface Client extends Closeable {
    /**
     * Getting all torrents.
     *
     * @return {@link List} of {@link TorrentData} objects.
     */
    List<TorrentData> getTorrents() throws IOException;

    /**
     * Deleting torrent by hash.
     *
     * @param hash - torrent's hash.
     */
    void delete(String hash) throws IOException;
}
