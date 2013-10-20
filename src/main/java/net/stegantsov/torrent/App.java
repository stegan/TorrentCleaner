package net.stegantsov.torrent;

import net.stegantsov.torrent.config.ConfigLoader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

/**
 * User: stegan
 * Date: 20.10.13
 * Time: 0:42
 */
public class App {
    private static final Logger LOG = Logger.getLogger(App.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        LOG.info("TorrentCleaner version: " + App.class.getPackage().getImplementationVersion());
        LOG.debug("Input args array: " + Arrays.toString(args));
        try {
            String configPath = null;
            for (int i = 0; i < args.length; i++) {
                if (args[i].toLowerCase().equals("--config") && args.length >= i) {
                    configPath = args[++i];
                } else if (args[i].toLowerCase().equals("--help")) {
                    LOG.info("Info:");
                    LOG.info("--config - path to configuration file");
                    System.exit(0);
                }
            }
            if (configPath == null) {
                LOG.error("Input error: config path should be set");
                System.exit(1);
            }
            LOG.info("Loading configuration");
            new ConfigLoader().load(configPath);
        } catch (Exception e) {
            LOG.error("Exception message: " + e.getMessage() + "\n" +
                    "Exception stack trace:\n" + StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}
