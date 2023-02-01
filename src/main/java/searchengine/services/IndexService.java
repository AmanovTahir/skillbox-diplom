package searchengine.services;

import searchengine.dto.IndexResponse;

public interface IndexService {
    IndexResponse startIndexing();
    IndexResponse stopIndexing();
}
