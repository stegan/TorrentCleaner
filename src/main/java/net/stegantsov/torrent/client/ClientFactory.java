package net.stegantsov.torrent.client;

/**
 * Torrent's clients factory.
 * <p/>
 * User: stegan
 * Date: 20.10.13
 * Time: 22:18
 */
public abstract class ClientFactory {
    private ClientFactory() {
    }

    /**
     * Create {@link UTorrentClient}
     *
     * @param login    - login.
     * @param password - password.
     * @param url      - url.
     * @return new object of {@link UTorrentClient} type.
     */
    public static UTorrentClient createUTorrentClient(final String login,
                                                      final String password,
                                                      final String url) {
        return new UTorrentClient(login,
                password,
                url);
    }
}
