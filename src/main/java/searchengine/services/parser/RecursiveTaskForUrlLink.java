package searchengine.services.parser;

import org.springframework.beans.factory.annotation.Autowired;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class RecursiveTaskForUrlLink extends RecursiveTask<Set<String>> {
    private static final Set<String> resultList = Collections.synchronizedSet(new TreeSet<>());
    private final List<RecursiveTaskForUrlLink> taskList = new ArrayList<>();
    private final String url;
    private final UrlLinkParser urlLinkParser;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final searchengine.config.Site siteInConfig;
    private final Site siteModel;

    @Autowired
    public RecursiveTaskForUrlLink(String url, UrlLinkParser urlLinkParser,
                                   SiteRepository siteRepository, PageRepository pageRepository,
                                   searchengine.config.Site siteInConfig, Site siteModel) {
        this.url = url;
        this.urlLinkParser = urlLinkParser;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.siteInConfig = siteInConfig;
        this.siteModel = siteModel;
    }

    @Override
    protected Set<String> compute() {
        urlLinkParser.getPathListForFork(url, resultList, siteModel).forEach(url -> {
            RecursiveTaskForUrlLink task = new RecursiveTaskForUrlLink(url, urlLinkParser, siteRepository,
                    pageRepository, siteInConfig, siteModel);
            taskList.add(task);
            task.fork();
        });
        taskList.forEach(ForkJoinTask::quietlyJoin);
        siteModel.setStatusTime(LocalDateTime.now());
        taskList.clear();
        return resultList;
    }
}
