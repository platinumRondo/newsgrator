package org.sunkengrotto.newsgrator.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.sunkengrotto.newsgrator.dto.ArticleDTO;
import org.sunkengrotto.newsgrator.dto.PagedArticlesDTO;
import org.sunkengrotto.newsgrator.entity.Article;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class ArticleService {

    public PagedArticlesDTO getArticles(int index, int size) {
        List<ArticleDTO> items = Article.<Article>findAll(Sort.descending("pubDate"))
                .page(Page.of(index, size)).stream().map(ArticleDTO::from).collect(Collectors.toList());

        long total = Article.count();

        PagedArticlesDTO response = new PagedArticlesDTO();
        response.index = index;
        response.size = size;
        response.total = total;
        response.items = items;

        return response;
    }

    public PagedArticlesDTO getArticlesBySiteId(int index, int size, long siteId) {
        List<ArticleDTO> items = Article.<Article>find("site.id", Sort.descending("pubDate"), siteId)
                .page(Page.of(index, size)).stream().map(ArticleDTO::from).collect(Collectors.toList());

        long total = Article.count("site.id", siteId);

        PagedArticlesDTO response = new PagedArticlesDTO();
        response.index = index;
        response.size = size;
        response.total = total;
        response.items = items;

        return response;
    }

}
