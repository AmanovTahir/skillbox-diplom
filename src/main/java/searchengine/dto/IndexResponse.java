package searchengine.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class IndexResponse {
    private boolean result;
    private String error;
}
