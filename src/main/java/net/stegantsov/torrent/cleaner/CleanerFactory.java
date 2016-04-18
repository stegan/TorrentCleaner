package net.stegantsov.torrent.cleaner;

import net.stegantsov.torrent.client.Client;

/**
 * Cleaner's factory.
 * <p/>
 * User: stegan
 * Date: 20.10.13
 * Time: 22:18
 */
public abstract class CleanerFactory {
    private CleanerFactory() {
    }

    /**
     * Create {@link RatioCleaner}.
     *
     * @param client      - {@link net.stegantsov.torrent.client.Client}.
     * @param checkPeriod - period of sleep in ms between checks.
     * @param errorPeriod - period of sleep in ms for next check if error occupied.
     * @param minRating   - minimum rating for saving torrent.
     * @return new object of {@link RatioCleaner} type.
     */
    public static RatioCleaner createRatioCleaner(final Client client,
                                                  final long checkPeriod,
                                                  final long errorPeriod,
                                                  final double minRating) {
        return new RatioCleaner(client,
                checkPeriod,
                errorPeriod,
                minRating);
    }

    /**
     * Create {@link SpeedCleaner}.
     *
     * @param client         - {@link net.stegantsov.torrent.client.Client}.
     * @param checkPeriod    - period of sleep in ms between checks.
     * @param errorPeriod    - period of sleep in ms for next check if error occupied.
     * @param minUploadSpeed - minimum upload speed for saving torrent.
     * @param monitoringTime - monitoring time for average speed.
     * @return new object of {@link SpeedCleaner} type.
     */
    public static SpeedCleaner createSpeedCleaner(final Client client,
                                                  final long checkPeriod,
                                                  final long errorPeriod,
                                                  final int minUploadSpeed,
                                                  final long monitoringTime) {
        return new SpeedCleaner(client,
                checkPeriod,
                errorPeriod,
                minUploadSpeed,
                monitoringTime);
    }
}
