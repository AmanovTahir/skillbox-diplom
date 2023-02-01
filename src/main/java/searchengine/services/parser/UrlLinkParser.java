package searchengine.services.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Log4j2
@Service
@RequiredArgsConstructor
public class UrlLinkParser {
    private final SiteRepository siteRepository;
    private static final Set<String> pathListForFork = Collections.synchronizedSet(new TreeSet<>());
    public Set<String> getPathListForFork(String url, Set<String> resultPathList, Site site) {
        pathListForFork.clear();
        try {
            Thread.sleep(1500);
            Document doc = getHTMLDocument(url);
            doc.select("a")
                    .stream()
                    .map(element -> element.attr("abs:href"))
                    .filter(s -> s.matches(getUrlRegex(url)))
                    .filter(resultPathList::add)
                    .peek(log::info)
                    .forEach(link -> {
                        pathListForFork.add(link);
                        initPageModel(doc, url, site);
                    });
        } catch (IOException e) {
            log.error(url + " - " + e.getMessage());
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
            Thread.currentThread().interrupt();
        }
        transaction(site);
        return pathListForFork;
    }

    private static Document getHTMLDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .referrer("https://www.google.com/")
                .ignoreContentType(true)
                .get();
    }

    private void initPageModel(Document doc, String url, Site site) {
        String html = doc.html();
        Page.builder()
                .path(URI.create(url).getPath())
                .site(site)
                .code(doc.connection().response().statusCode())
                .content(html)
                .build();
    }

    private String getUrlRegex(String url) {
        return "^(https://)(www.)?+("
                + url.replaceAll("http(s)?://|www\\.|/.*", "")
                + ").*[html/\\d]";
    }

    private void transaction(Site site) {
        siteRepository.save(site);
    }
}
