package org.sunkengrotto.newsgrator.resource;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.sunkengrotto.newsgrator.dto.PostSiteDTO;
import org.sunkengrotto.newsgrator.dto.SiteDTO;
import org.sunkengrotto.newsgrator.dto.StatusDTO;
import org.sunkengrotto.newsgrator.service.SiteService;

@Path("/sites")
@Tag(name = "Sites")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SiteResource {

    private static final Logger LOGGER = Logger.getLogger(SiteResource.class);

    @Inject
    SiteService service;

    @GET
    @Operation(summary = "Fetch all sites", description = "Fetch all the sites added")
    @APIResponse(responseCode = "200", description = "Sites found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SiteDTO.class, type = SchemaType.ARRAY)))
    public Response getSites(@QueryParam("enabled") Optional<Boolean> enabled) {
        try {
            List<SiteDTO> sites;
            if (enabled.isPresent()) {
                sites = service.getAllByStatus(enabled.get());
            } else {
                sites = service.getAll();
            }
            return Response.ok(sites).build();
        } catch (Exception e) {
            LOGGER.error("Cannot getAll", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Operation(summary = "Add site", description = "Add a new site")
    @APIResponse(responseCode = "200", description = "Site added", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SiteDTO.class)))
    public Response addSite(@Valid PostSiteDTO request) {
        try {
            SiteDTO site = service.add(request.feedUrl);
            return Response.ok(site).build();
        } catch (Exception e) {
            LOGGER.error("Cannot add site", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Operation(summary = "Fetch site", description = "Fetch the requested site")
    @APIResponse(responseCode = "404", description = "Site not found")
    @APIResponse(responseCode = "200", description = "Site found")
    @Path("/{siteId}")
    public Response getSite(@PathParam("siteId") Long siteId) {
        Optional<SiteDTO> site = service.getById(siteId);
        if (site.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.OK).entity(site.get()).build();
    }

    @DELETE
    @Operation(summary = "Remove site", description = "Remove the requested site")
    @APIResponse(responseCode = "404", description = "Site not found")
    @APIResponse(responseCode = "204", description = "Site removed")
    @Path("/{siteId}")
    public Response deleteSite(@PathParam("siteId") Long siteId) {
        if (!service.delete(siteId)) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.NO_CONTENT).build();
    }

    @PUT
    @Operation(summary = "Change site status", description = "Enable or disable fetching the site requested")
    @APIResponse(responseCode = "404", description = "Site not found")
    @APIResponse(responseCode = "202", description = "Site updated")
    @Path("/{siteId}/status")
    public Response changeStatus(@PathParam("siteId") Long siteId, @Valid @NotNull StatusDTO status) {
        boolean changed = service.changeStatus(siteId, status.enabled);
        Status responseStatus = changed ? Status.ACCEPTED : Status.NOT_FOUND;
        return Response.status(responseStatus).build();
    }

}
