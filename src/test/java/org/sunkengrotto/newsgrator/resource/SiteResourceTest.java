package org.sunkengrotto.newsgrator.resource;

import org.junit.jupiter.api.Test;
import org.sunkengrotto.newsgrator.dto.PagedArticlesDTO;
import org.sunkengrotto.newsgrator.dto.PostSiteDTO;
import org.sunkengrotto.newsgrator.dto.SiteDTO;
import org.sunkengrotto.newsgrator.dto.StatusDTO;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;

@QuarkusTest
public class SiteResourceTest {

    @TestHTTPResource("hn.rss")
    URL hnRss;

    @TestHTTPResource("nyaa.rss")
    URL nyaaRss;

    @Test
    public void insertTest() {
        PostSiteDTO request = new PostSiteDTO();
        request.feedUrl = hnRss.toString();
        given().when().accept(ContentType.JSON).contentType(ContentType.JSON).body(request).post("/sites")
                .then()
                .statusCode(200)
                .body("feedUrl", equalTo(hnRss.toString()))
                .body("enabled", equalTo(true));
    }

    @Test
    public void getTest() {
        PostSiteDTO request = new PostSiteDTO();
        request.feedUrl = hnRss.toString();
        Integer id = given().when().accept(ContentType.JSON).contentType(ContentType.JSON).body(request)
                .post("/sites")
                .thenReturn().getBody().jsonPath().getInt("id");

        assertNotNull(id);

        given().when().accept(ContentType.JSON).get("/sites/{id}", id).then().assertThat()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("feedUrl", equalTo(hnRss.toString()));
    }

    @Test
    public void disableTest() {
        PostSiteDTO postRequest = new PostSiteDTO();
        postRequest.feedUrl = hnRss.toString();
        SiteDTO site = given().when().accept(ContentType.JSON).contentType(ContentType.JSON).body(postRequest)
                .post("/sites").thenReturn().as(SiteDTO.class, ObjectMapperType.JSONB);

        assertNotNull(site);
        assertNotNull(site.id);
        assertTrue(site.enabled);
        assertNotNull(site.feedUrl);
        assertEquals(site.feedUrl, hnRss.toString());

        StatusDTO statusRequest = new StatusDTO();
        statusRequest.enabled = false;

        given().when().accept(ContentType.JSON).contentType(ContentType.JSON).body(statusRequest)
                .put("/sites/{id}/status", site.id).then()
                .statusCode(202);

        given().when().accept(ContentType.JSON).get("/sites/{id}", site.id).then().assertThat()
                .statusCode(200)
                .body("id", equalTo(site.id.intValue()))
                .body("feedUrl", equalTo(hnRss.toString()))
                .body("enabled", equalTo(false));
    }

    @Test
    public void noDuplicateSitesTest() {
        PostSiteDTO postRequest = new PostSiteDTO();
        postRequest.feedUrl = hnRss.toString();

        SiteDTO site1 = given().when().accept(ContentType.JSON).contentType(ContentType.JSON).body(postRequest)
                .post("/sites").thenReturn().as(SiteDTO.class, ObjectMapperType.JSONB);
        assertNotNull(site1);
        assertNotNull(site1.id);

        SiteDTO site2 = given().when().accept(ContentType.JSON).contentType(ContentType.JSON).body(postRequest)
                .post("/sites").thenReturn().as(SiteDTO.class, ObjectMapperType.JSONB);
        assertNotNull(site2);
        assertNotNull(site2.id);

        assertEquals(site1.id, site2.id);
    }

    @Test
    public void deleteTest() {
        PostSiteDTO postRequest = new PostSiteDTO();
        postRequest.feedUrl = hnRss.toString();

        SiteDTO site = given().when().accept(ContentType.JSON).contentType(ContentType.JSON).body(postRequest)
                .post("/sites").thenReturn().as(SiteDTO.class, ObjectMapperType.JSONB);

        assertNotNull(site);
        assertNotNull(site.id);

        given().when().delete("/sites/{id}", site.id).then().statusCode(204);
        given().when().get("/sites/{id}", site.id).then().statusCode(404);

        PagedArticlesDTO articles = given().when().queryParam("site", site.id).get("/articles")
                .thenReturn().as(PagedArticlesDTO.class, ObjectMapperType.JSONB);

        assertNotNull(articles);
        assertEquals(0, articles.total);
        assertNotNull(articles.items);
        assertEquals(0, articles.items.size());

    }
}
