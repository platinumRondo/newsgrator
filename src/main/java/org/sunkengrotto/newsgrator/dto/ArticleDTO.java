package org.sunkengrotto.newsgrator.dto;

import java.time.ZonedDateTime;

import org.sunkengrotto.newsgrator.entity.Article;

public class ArticleDTO {
    public Long id;
    public String title;
    public String description;
    public String link;
    public String author;
    public ZonedDateTime pubDate;

    public static ArticleDTO from(Article article) {
        ArticleDTO dto = new ArticleDTO();

        dto.id = article.id;
        dto.title = article.title;
        dto.description = article.description;
        dto.link = article.link;
        dto.author = article.author;
        dto.pubDate = article.pubDate;

        return dto;
    }
}
