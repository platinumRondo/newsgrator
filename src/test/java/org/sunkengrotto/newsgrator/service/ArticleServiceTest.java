package org.sunkengrotto.newsgrator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.sunkengrotto.newsgrator.dto.PagedArticlesDTO;
import org.sunkengrotto.newsgrator.dto.SiteDTO;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ArticleServiceTest {

    @TestHTTPResource("hn.rss")
    URL hnRss;

    @Inject
    SiteService siteService;

    @Inject
    ArticleService service;

    @Test
    public void updateWithoutDuplicatesTest() throws Exception {
        SiteDTO site = siteService.add(hnRss.toString());

        PagedArticlesDTO articles = service.getArticlesBySiteId(0, Integer.MAX_VALUE, site.id);

        assertNotNull(articles);
        assertTrue(articles.total > 0);
        List<Long> firstIds = articles.items.stream().map(item -> item.id).sorted().collect(Collectors.toList());

        siteService.updateArticlesForSiteId(site.id);

        PagedArticlesDTO secondArticles = service.getArticlesBySiteId(0, Integer.MAX_VALUE, site.id);

        assertNotNull(secondArticles);
        assertTrue(secondArticles.total > 0);
        List<Long> secondIds = articles.items.stream().map(item -> item.id).sorted().collect(Collectors.toList());

        assertEquals(articles.total, secondArticles.total);
        assertEquals(firstIds.size(), secondIds.size());

        for (int i = 0; i < firstIds.size(); i++) {
            assertEquals(firstIds.get(i), secondIds.get(i));
        }
    }

}
