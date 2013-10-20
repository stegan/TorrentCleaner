package net.stegantsov.torrent.client;

/**
 * data bean.
 * <p/>
 * User: stegan
 * Date: 20.10.13
 * Time: 0:48
 */
public class TorrentData {
    public static final int STARTED = 1;
    public static final int CHECKING = 2;
    public static final int START_AFTER_CHECK = 4;
    public static final int CHECKED = 8;
    public static final int ERROR = 16;
    public static final int PAUSED = 32;
    public static final int QUEUED = 64;
    public static final int LOADED = 128;

    public static final int MAX_PERCENT = 1000;

    private String hash;
    private int status;
    private double ratio;
    private int uploadSpeed;
    private int percentProgress;

    public TorrentData() {
    }

    /**
     * Get hash.
     *
     * @return hash.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Set hash.
     *
     * @param hash - hash.
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Set status.
     * <p/>
     * Based on uTorrent web api.
     * The STATUS is a bitfield represented as integers, which is obtained by adding up the different values for
     * corresponding statuses:
     * 1 = Started
     * 2 = Checking
     * 4 = Start after check
     * 8 = Checked
     * 16 = Error
     * 32 = Paused
     * 64 = Queued
     * 128 = Loaded
     * For example, if a torrent job has a status of 201 = 128 + 64 + 8 + 1, then it is loaded, queued, checked, and
     * started. A bitwise AND operator should be used to determine whether the given STATUS contains a particular
     * status.
     *
     * @param status - status.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Get ratio.
     *
     * @return ratio.
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Set ratio.
     *
     * @param ratio - ratio.
     */
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    /**
     * Get upload speed.
     * Bytes per second.
     *
     * @return upload speed.
     */
    public int getUploadSpeed() {
        return uploadSpeed;
    }

    /**
     * Set upload speed.
     *
     * @param uploadSpeed - upload speed.
     */
    public void setUploadSpeed(int uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    /**
     * Set percent progress.
     * Integer in per mils.
     *
     * @param percentProgress - percent progress.
     */
    public void setPercentProgress(int percentProgress) {
        this.percentProgress = percentProgress;
    }

    /**
     * Helper to check if torrent is downloaded & seeding.
     *
     * @return true if downloaded & seeding, otherwise false.
     */
    public boolean isDownloaded() {
        return (status & LOADED) == LOADED &&
                (status & QUEUED) == QUEUED &&
                (status & CHECKED) == CHECKED &&
                (status & STARTED) == STARTED &&
                percentProgress == MAX_PERCENT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TorrentData torrentData = (TorrentData) o;

        if (hash != null ? !hash.equals(torrentData.hash) : torrentData.hash != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hash != null ? hash.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TorrentData{" +
                "hash='" + hash + '\'' +
                ", status=" + status +
                ", ratio=" + ratio +
                ", uploadSpeed=" + uploadSpeed +
                '}';
    }
}
