package org.sunkengrotto.newsgrator.entity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Article extends PanacheEntityBase {
    @Id
    @SequenceGenerator(name = "articleSeq", sequenceName = "ARTICLE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articleSeq")
    public Long id;
    @Version
    public Long version;
    @Column(name = "title", nullable = false, length = 2000)
    public String title;
    @Lob
    @Column(name = "description")
    public String description;
    @Column(name = "link", nullable = false, length = 2000)
    public String link;
    @Column(name = "author", length = 255)
    public String author;
    @Column(name = "pub_date")
    public ZonedDateTime pubDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    public Site site;

    public Article updateFrom(SyndEntry entry) {
        this.title = entry.getTitle();
        this.description = Optional.ofNullable(entry.getDescription()).map(SyndContent::getValue).orElse("");
        this.link = entry.getLink();
        this.author = entry.getAuthor();
        this.pubDate = Optional.ofNullable(entry.getPublishedDate())
                .map(date -> ZonedDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault()))
                .orElse(null);

        return this;
    }

    public static Optional<Article> findByLink(String link) {
        return find("link", link).firstResultOptional();
    }

    public static Article from(SyndEntry entry) {
        return new Article().updateFrom(entry);
    }
}
