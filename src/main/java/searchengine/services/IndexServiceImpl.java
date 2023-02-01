package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.IndexResponse;
import searchengine.services.parser.ParserStarter;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {
    private final IndexResponse indexResponse;
    private final ParserStarter parserStarter;

    @Override
    public IndexResponse startIndexing() {
        parserStarter.run();
        indexResponse.setResult(true);
        indexResponse.setError("Индексация уже запущена");
        return indexResponse;
    }

    @Override
    public IndexResponse stopIndexing() {
        parserStarter.stop();
        indexResponse.setResult(true);
        indexResponse.setError("Индексация не запущена");
        return indexResponse;
    }
}
