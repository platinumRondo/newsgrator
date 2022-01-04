package org.sunkengrotto.newsgrator.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import org.jboss.logging.Logger;
import org.sunkengrotto.newsgrator.dto.SiteDTO;
import org.sunkengrotto.newsgrator.entity.Article;
import org.sunkengrotto.newsgrator.entity.Site;
import org.sunkengrotto.newsgrator.exception.UpdateFailedException;
import org.sunkengrotto.newsgrator.utils.FeedUtils;

import io.quarkus.vertx.ConsumeEvent;

@ApplicationScoped
@Transactional
public class SiteService {

    private static final Logger LOG = Logger.getLogger(SiteService.class);

    public List<SiteDTO> getAll() {
        return Site.<Site>findAll().stream().map(SiteDTO::from).collect(Collectors.toList());
    }

    public List<SiteDTO> getAllByStatus(boolean enabled) {
        return Site.<Site>find("enabled", enabled).stream().map(SiteDTO::from).collect(Collectors.toList());
    }

    public SiteDTO add(String feedUrl) throws MalformedURLException {
        URL url = new URL(feedUrl);
        Optional<Site> searchSite = Site.findByFeedUrl(url.toString());

        if (searchSite.isPresent()) {
            return SiteDTO.from(searchSite.get());
        }

        Site site = new Site();
        site.feedUrl = feedUrl;
        site.lastUpdated = ZonedDateTime.now();
        site.persist();

        updateArticlesForSiteId(site.id);

        site = Site.findById(site.id);

        return SiteDTO.from(site);
    }

    public boolean delete(Long siteId) {
        return Site.deleteById(siteId);
    }

    public boolean changeStatus(Long siteId, boolean enabled) {
        Site site = Site.findById(siteId);

        if (Objects.isNull(site)) {
            return false;
        }

        site.enabled = enabled;
        site.persist();
        return true;
    }

    public Optional<SiteDTO> getById(Long siteId) {
        return Site.<Site>findByIdOptional(siteId).map(SiteDTO::from);
    }

    @ConsumeEvent(value = "feed-update", blocking = true)
    public void updateArticlesForSiteId(Long siteId) throws UpdateFailedException {
        try {
            Site site = Site.findById(siteId);
            SyndFeed feed = FeedUtils.fetchFeed(site.feedUrl);
            site.title = feed.getTitle();
            site.lastUpdated = Optional.ofNullable(feed.getPublishedDate())
                    .map(date -> ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()))
                    .orElse(ZonedDateTime.now());
            site.persist();

            for (SyndEntry entry : feed.getEntries()) {
                Article article = Article.findByLink(entry.getLink()).map(a -> a.updateFrom(entry))
                        .orElse(Article.from(entry));
                article.site = site;
                article.persist();
            }
            LOG.infof("Update complete for site %d", siteId);
        } catch (Exception e) {
            LOG.warnf("Update failed for %d: %s", siteId, e.getMessage());
            throw new UpdateFailedException(e);
        }
    }

}
