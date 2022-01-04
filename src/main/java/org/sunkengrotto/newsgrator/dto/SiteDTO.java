package org.sunkengrotto.newsgrator.dto;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.sunkengrotto.newsgrator.entity.Site;

public class SiteDTO {
    public Long id;
    @NotNull
    public String feedUrl;
    public String title;
    public String link;
    public ZonedDateTime lastUpdated;
    public boolean enabled;

    public static SiteDTO from(Site site) {
        SiteDTO dto = new SiteDTO();
        dto.id = site.id;
        dto.feedUrl = site.feedUrl;
        dto.title = site.title;
        dto.link = site.link;
        dto.lastUpdated = site.lastUpdated;
        dto.enabled = site.enabled;

        return dto;
    }

    public Site to() {
        Site entity = new Site();
        entity.id = this.id;
        entity.feedUrl = this.feedUrl;
        entity.title = this.title;
        entity.link = this.link;
        entity.lastUpdated = this.lastUpdated;
        entity.enabled = this.enabled;

        return entity;
    }

}
