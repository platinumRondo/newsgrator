package org.sunkengrotto.newsgrator.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.sunkengrotto.newsgrator.exception.FetchFeedException;

public final class FeedUtils {

    private static final HttpClient client;

    static {
        client = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    private FeedUtils() {
    }

    public static SyndFeed fetchFeed(String feedUrl) throws FetchFeedException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(feedUrl))
                    .timeout(Duration.ofMinutes(2))
                    .header("User-Agent", "newsgrator/1.0")
                    .GET()
                    .build();
            HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
            if (response.statusCode() > 299 || response.statusCode() < 200) {
                throw new FetchFeedException(feedUrl, "Returned code is not 200, aborting...");
            }
            return new SyndFeedInput().build(new XmlReader(response.body()));
        } catch (IllegalStateException | IOException | FeedException e) {
            throw new FetchFeedException(feedUrl, e);
        }
    }

}
