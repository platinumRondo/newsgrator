package org.sunkengrotto.newsgrator.resource;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.sunkengrotto.newsgrator.dto.PagedArticlesDTO;
import org.sunkengrotto.newsgrator.service.ArticleService;

@Path("/articles")
@Tag(name = "Articles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @Inject
    ArticleService service;

    @GET
    @Operation(summary = "Fetch the articles", description = "Fetch the retrived articles")
    @APIResponse(responseCode = "200", description = "Articles found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PagedArticlesDTO.class)))
    public Response getArticles(@QueryParam("index") @DefaultValue("0") int index,
            @QueryParam("size") @DefaultValue("20") int size, @QueryParam("site") Optional<Long> siteId) {
        PagedArticlesDTO response;
        if (siteId.isPresent()) {
            response = service.getArticlesBySiteId(index, size, siteId.get());
        } else {
            response = service.getArticles(index, size);
        }
        return Response.ok(response).build();
    }

}
