package net.stegantsov.torrent.client;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: stegan
 * Date: 20.10.13
 * Time: 0:42
 */
public final class UTorrentClient implements Client {
    private static final Logger LOG = Logger.getLogger(UTorrentClient.class);

    private static final int TIMEOUT = 30000;
    private static final boolean USE_INSECURE_SSL = true;
    private static final BrowserVersion BROWSER_VERSION = BrowserVersion.CHROME;

    private WebClient webClient;
    private String token;

    private final String login;
    private final String password;
    private final String url;

    public UTorrentClient(final String login,
                          final String password,
                          final String url) {
        this.login = login;
        this.password = password;
        this.url = url;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized List<TorrentData> getTorrents() throws IOException {
        //todo check alive status
        if (webClient == null) {
            connect();
        }

        TextPage tp = webClient.getPage(url + "/?list=1&token=" + token);
        List<List> tc = JSONArray.toList(JSONObject.fromObject(tp.getContent()).getJSONArray("torrents"));
        List<TorrentData> result = new ArrayList<>();
        for (List ti : tc) {
            if (ti.size() >= 8) {
                TorrentData torrentData = new TorrentData();
                torrentData.setHash(String.valueOf(ti.get(0)));
                try {
                    torrentData.setRatio(Double.parseDouble(String.valueOf(ti.get(7))) / 1000);
                    torrentData.setStatus(Integer.parseInt(String.valueOf(ti.get(1))));
                    torrentData.setUploadSpeed(Integer.parseInt(String.valueOf(ti.get(8))));
                    torrentData.setPercentProgress(Integer.parseInt(String.valueOf(ti.get(4))));
                } catch (NumberFormatException nfe) {
                    throw new IOException("Unable to parse data");
                }
                result.add(torrentData);
            } else {
                throw new IOException("Received unknown structure of torrent's list");
            }
        }
        LOG.debug(Thread.currentThread() + ":" + super.toString() + " Torrents retrieved: " + result);
        return result;
    }

    @Override
    public synchronized void delete(String hash) throws IOException {
        if (webClient == null) {
            connect();
        }
        LOG.debug(Thread.currentThread() + ":" + super.toString() + " Delete torrent by hash: " + hash);
        //todo possible to check deletion status!?
        TextPage tp = webClient.getPage(url + "/?action=remove&hash=" + hash + "&token=" + token);
        LOG.debug("Content: " + tp.getContent());
    }

    @Override
    public void close() throws IOException {
        if (webClient != null) {
            webClient.closeAllWindows();
        }
    }

    private void connect() throws IOException {
        if (login == null || password == null || url == null) {
            throw new IllegalStateException("login password url are missed");
        }
        LOG.debug(Thread.currentThread() + ":" + super.toString() + " Connecting to: " + url);

        webClient = new WebClient(BROWSER_VERSION);
        webClient.getOptions().setUseInsecureSSL(USE_INSECURE_SSL);
        webClient.getOptions().setTimeout(TIMEOUT);
        webClient.getOptions().setCssEnabled(false);
        webClient.setHTMLParserListener(HTMLParserListener.LOG_REPORTER);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(login, password));

        HtmlPage p = webClient.getPage(url + "/token.html");
        token = p.getElementById("token").asText();
        if (token == null || token.isEmpty()) {
            throw new IOException("Unable to retrieve token");
        }
        LOG.debug(Thread.currentThread() + ":" + super.toString() + " Connection success. Token: " + token);
    }

    @Override
    public String toString() {
        return "UTorrentClient{" +
                "login='" + login + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
