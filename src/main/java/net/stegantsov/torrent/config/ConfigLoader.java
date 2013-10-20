package net.stegantsov.torrent.config;

import net.stegantsov.torrent.cleaner.Cleaner;
import net.stegantsov.torrent.cleaner.CleanerFactory;
import net.stegantsov.torrent.client.Client;
import net.stegantsov.torrent.client.ClientFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Configuration loader. Check sample config.xml.
 * <p/>
 * User: stegan
 * Date: 20.10.13
 * Time: 22:31
 */
public final class ConfigLoader {
    private static final Logger LOG = Logger.getLogger(ConfigLoader.class);

    private static final String TORRENT_CLIENT = "torrent-clients.torrent-client";
    private static final String TORRENT_CLEANER = "cleaners.cleaner";

    /**
     * Loading xml config.
     *
     * @param path - path to xml.
     * @throws ConfigurationException if structure is corrupted.
     */
    public void load(String path) throws ConfigurationException {
        XMLConfiguration configuration = new XMLConfiguration(path);

        Object obj = configuration.getProperty(TORRENT_CLIENT + ".type");
        if (obj instanceof Collection) {
            int size = ((Collection) obj).size();
            for (int i = 0; i < size; i++) {
                loadClient(configuration,
                        TORRENT_CLIENT + "(" + i + ")");
            }
        } else if (obj instanceof String) {
            loadClient(configuration,
                    TORRENT_CLIENT);
        } else {
            throw new ConfigurationException("no clients defined");
        }
    }

    private void loadClient(final XMLConfiguration configuration,
                            final String prefix) throws ConfigurationException {
        Object clientType = configuration.getProperty(prefix + ".type");
        if (clientType == null || clientType.toString().isEmpty()) {
            throw new ConfigurationException("client's type should be set");
        }

        Object clientUrl = configuration.getProperty(prefix + ".url");
        if (clientUrl == null || clientUrl.toString().isEmpty()) {
            throw new ConfigurationException("client's url should be set");
        }

        Object clientLogin = configuration.getProperty(prefix + ".login");
        if (clientLogin == null || clientLogin.toString().isEmpty()) {
            throw new ConfigurationException("client's login should be set");
        }

        Object clientPassword = configuration.getProperty(prefix + ".password");
        if (clientPassword == null || clientPassword.toString().isEmpty()) {
            throw new ConfigurationException("client's password should be set");
        }

        if ("UTorrentClient".equalsIgnoreCase(clientType.toString())) {
            Client client = ClientFactory.createUTorrentClient(clientLogin.toString(),
                    clientPassword.toString(),
                    clientUrl.toString());
            loadCleaners(configuration,
                    client,
                    prefix);
        } else {
            throw new ConfigurationException("unknown client");
        }
    }

    private void loadCleaners(final XMLConfiguration configuration,
                              final Client client,
                              final String prefix) throws ConfigurationException {
        Object obj = configuration.getProperty(prefix + "." + TORRENT_CLEANER + ".type");
        if (obj instanceof Collection) {
            int size = ((Collection) obj).size();
            for (int i = 0; i < size; i++) {
                loadCleaner(configuration,
                        client,
                        prefix + "." + TORRENT_CLEANER + "(" + i + ")");
            }
        } else if (obj instanceof String) {
            loadCleaner(configuration,
                    client,
                    prefix + "." + TORRENT_CLEANER);
        } else {
            throw new ConfigurationException("no cleaner defined for client: " + client);
        }
    }

    private void loadCleaner(final XMLConfiguration configuration,
                             final Client client,
                             final String prefix) throws ConfigurationException {
        Object cleanerType = configuration.getProperty(prefix + ".type");
        if (cleanerType == null || cleanerType.toString().isEmpty()) {
            throw new ConfigurationException("cleaner's type should be set for client: " + client);
        }
        Object checkPeriod = configuration.getProperty(prefix + ".checkPeriod");
        if (checkPeriod == null || checkPeriod.toString().isEmpty()) {
            throw new ConfigurationException("cleaner's checkPeriod should be set for client: " + client);
        }
        Cleaner cleaner;
        if ("SpeedCleaner".equalsIgnoreCase(cleanerType.toString())) {
            Object minUploadSpeed = configuration.getProperty(prefix + ".minUploadSpeed");
            if (minUploadSpeed == null || minUploadSpeed.toString().isEmpty()) {
                throw new ConfigurationException("SpeedCleaner's minUploadSpeed should be set for client: " + client);
            }

            Object monitoringTime = configuration.getProperty(prefix + ".monitoringTime");
            if (monitoringTime == null || monitoringTime.toString().isEmpty()) {
                throw new ConfigurationException("SpeedCleaner's monitoringTime should be set for client: " + client);
            }

            cleaner = CleanerFactory.createSpeedCleaner(client,
                    Long.parseLong(checkPeriod.toString()),
                    Integer.parseInt(minUploadSpeed.toString()),
                    Long.parseLong(monitoringTime.toString()));

        } else if ("RatioCleaner".equalsIgnoreCase(cleanerType.toString())) {
            Object minRating = configuration.getProperty(prefix + ".minRating");
            if (minRating == null || minRating.toString().isEmpty()) {
                throw new ConfigurationException("RatioCleaner's minRating should be set for client: " + client);
            }

            cleaner = CleanerFactory.createRatioCleaner(client,
                    Long.parseLong(checkPeriod.toString()),
                    Double.parseDouble(minRating.toString()));
        } else {
            throw new ConfigurationException("unknown cleaner for client: " + client);
        }
        LOG.info("Starting " + cleaner + " for client " + client);
        new Thread(cleaner).start();
    }
}
