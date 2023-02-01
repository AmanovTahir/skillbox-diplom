package searchengine.services.parser.dump;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;


@Log4j2
public class ParseService extends RecursiveTask<Set<String>> {
    private static final Set<String> pathList = Collections.synchronizedSet(new TreeSet<>());
    private static final Set<String> dumpPathList = Collections.synchronizedSet(new TreeSet<>());
    private static final Set<Page> pageModelLists = Collections.synchronizedSet(new TreeSet<>());
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final searchengine.config.Site siteInConfig;
    private Site site;
    private final String url;

    public ParseService(searchengine.config.Site siteInConfig,
                        Site site,
                        String url,
                        SiteRepository siteRepository,
                        PageRepository pageRepository) {
        this.siteInConfig = siteInConfig;
        this.site = site;
        this.url = url;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
    }

    public ParseService(searchengine.config.Site siteInConfig, SiteRepository siteRepository, PageRepository pageRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.siteInConfig = siteInConfig;
        this.url = siteInConfig.getUrl();
//        extractSiteToBD();
    }


    @Override
    protected Set<String> compute() {
        List<String> urlList = new ArrayList<>(getUrlList(siteInConfig.getUrl()));
        List<ParseService> taskList = new ArrayList<>();
        urlList.forEach(url -> {
            ParseService task = new ParseService(siteInConfig, site, url, siteRepository, pageRepository);
            taskList.add(task);
            task.fork();
        });
        taskList.forEach(ForkJoinTask::quietlyJoin);
        return pathList;
    }

    public Set<String> getUrlList(String url) {
        dumpPathList.clear();
        try {
            Thread.sleep(1500);
            Document doc = getJsoupDocument(url);
            doc.select("a")
                    .stream()
                    .map(element -> element.attr("abs:href"))
                    .filter(s -> s.matches(getUrlRegex()))
                    .filter(pathList::add)
                    .peek(log::info)
                    .forEach(dumpPathList::add);
//                        pageModelLists.add(setSitePages(doc, s));
//            pageRepository.saveAll(pageModelLists);
//            pageModelLists.clear();
        } catch (IOException e) {
            log.info(url + " - " + e.getMessage());
        } catch (InterruptedException e) {
            log.info("InterruptedException: " + e);
            Thread.currentThread().interrupt();
        }
        return dumpPathList;
    }

    private void extractSiteToBD() {
//        siteRepository.save(
//        siteModel = searchengine.model.Site.builder()
//                .url(siteInConfig.getUrl())
//                .statusTime(LocalDateTime.now())
//                .name(siteInConfig.getName())
//                .status(Status.INDEXING)
//                .build();
//    );
    }

    private static Document getJsoupDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .get();
    }

    private Page setSitePages(Document doc, String url) {
        if (!pathList.contains(url)) {
            String html = doc.html();
            return Page.builder()
                    .path(URI.create(url).getPath())
                    .site(site)
                    .code(doc.connection().response().statusCode())
                    .content(html)
                    .build();
        }
        throw new RuntimeException("найден дубликат!");
    }

    private String getUrlRegex() {
        return "^(https://)(www.)?+("
                + siteInConfig.getUrl().replaceAll("http(s)?://|www\\.|/.*", "")
                + ").*[html/\\d]";
    }
}

