package org.sunkengrotto.newsgrator.entity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.rometools.rome.feed.synd.SyndFeed;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(indexes = { @Index(columnList = "feed_url", unique = true) })
public class Site extends PanacheEntityBase {
    @Id
    @SequenceGenerator(name = "siteSeq", sequenceName = "SITE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "siteSeq")
    public Long id;
    @Version
    public Long version;
    @Column(name = "feed_url", nullable = false, length = 2000, unique = true)
    public String feedUrl;
    @Column(name = "title", length = 2000)
    public String title;
    @Column(name = "link", length = 2000)
    public String link;
    @Column(name = "last_updated", nullable = false)
    public ZonedDateTime lastUpdated;
    @Column(name = "enabled", nullable = false)
    public boolean enabled = true;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "site")
    public List<Article> articles;

    public static Optional<Site> findByFeedUrl(String feedUrl) {
        return find("feedUrl", feedUrl).firstResultOptional();
    }

    public static List<Site> listAllEnabled() {
        return find("enabled", true).list();
    }

    public static Site from(SyndFeed feed) {
        Site site = new Site();
        site.feedUrl = feed.getUri();
        site.title = feed.getTitle();
        site.link = feed.getLink();
        site.lastUpdated = Optional.ofNullable(feed.getPublishedDate())
                .map(date -> ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()))
                .orElse(ZonedDateTime.now());

        return site;
    }
}
