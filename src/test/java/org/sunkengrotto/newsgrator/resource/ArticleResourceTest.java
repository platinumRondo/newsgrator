package org.sunkengrotto.newsgrator.resource;

import java.net.URL;

import com.google.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sunkengrotto.newsgrator.dto.PagedArticlesDTO;
import org.sunkengrotto.newsgrator.dto.PostSiteDTO;
import org.sunkengrotto.newsgrator.scheduler.SiteScheduler;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ArticleResourceTest {

    @TestHTTPResource("hn.rss")
    URL hnRss;

    @Inject
    SiteScheduler scheduler;

    private long siteId;

    @BeforeEach
    public void setup() {
        PostSiteDTO request = new PostSiteDTO();
        request.feedUrl = hnRss.toString();
        this.siteId = given().when().accept(ContentType.JSON).contentType(ContentType.JSON).body(request).post("/sites")
                .thenReturn().jsonPath().getLong("id");
    }

    @Test
    public void getArticles() {
        PagedArticlesDTO articles = given().when().accept(ContentType.JSON).contentType(ContentType.JSON)
                .get("/articles").thenReturn().as(PagedArticlesDTO.class, ObjectMapperType.JSONB);

        assertNotNull(articles);
        assertEquals(0, articles.index);
        assertEquals(20, articles.size);
        assertTrue(articles.total > 0);
        assertNotNull(articles.items);
        assertEquals(articles.size, articles.items.size());
    }

    @Test
    public void getArticlesById() {
        PagedArticlesDTO articles = given().when().accept(ContentType.JSON).contentType(ContentType.JSON)
                .queryParam("site", siteId).get("/articles").thenReturn()
                .as(PagedArticlesDTO.class, ObjectMapperType.JSONB);

        assertNotNull(articles);
        assertEquals(0, articles.index);
        assertEquals(20, articles.size);
        assertTrue(articles.total > 0);
        assertNotNull(articles.items);
        assertEquals(articles.size, articles.items.size());
    }

}
