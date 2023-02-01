package searchengine.services.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

@Service
public class ParserStarter {
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final UrlLinkParser urlLinkParser;
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    @Autowired
    public ParserStarter(SitesList sitesList,
                         SiteRepository siteRepository,
                         PageRepository pageRepository,
                         UrlLinkParser urlLinkParser) {
        this.sitesList = sitesList;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.urlLinkParser = urlLinkParser;
    }


    public void run() {
        sitesList.getSites().forEach(siteConfig -> {
            RecursiveTaskForUrlLink getSite = new RecursiveTaskForUrlLink(
                    siteConfig.getUrl(), urlLinkParser,
                    siteRepository, pageRepository, siteConfig, initSiteModel(siteConfig));
            forkJoinPool.invoke(getSite);
        });
        System.out.println("Complete");
    }

    public void stop() {
        forkJoinPool.shutdown();
    }

    private Site initSiteModel(searchengine.config.Site site) {
        return Site.builder()
                .url(site.getUrl())
                .statusTime(LocalDateTime.now())
                .name(site.getName())
                .status(Status.INDEXING)
                .build();
    }
}