package org.sunkengrotto.newsgrator.scheduler;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.sunkengrotto.newsgrator.dto.SiteDTO;
import org.sunkengrotto.newsgrator.service.SiteService;

import io.quarkus.scheduler.Scheduled;
import io.vertx.core.eventbus.EventBus;

@ApplicationScoped
public class SiteScheduler {

    private static final Logger LOGGER = Logger.getLogger(SiteScheduler.class);

    @Inject
    SiteService service;

    @Inject
    EventBus bus;

    @Scheduled(every = "{org.sunkengrotto.newsgrator.scheduler.site}")
    public void updateSites() {
        List<SiteDTO> sites = service.getAllByStatus(true);
        for (SiteDTO site : sites) {
            LOGGER.infof("Requesting update for site %d: %s", site.id, site.title);
            bus.send("feed-update", site.id);
        }
    }

}
