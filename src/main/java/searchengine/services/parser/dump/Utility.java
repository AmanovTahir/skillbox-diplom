package searchengine.services.parser.dump;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class Utility {
    private Utility() {
    }

    public static void createSiteMapFile(String webSite, Set<String> siteMap, String path) {
        siteMap.add(webSite);
        StringBuilder builder = new StringBuilder();
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileWriter fileWriter = new FileWriter(path)) {
            siteMap.stream().sorted().forEach(s -> {
                try {
                    fileWriter.write(getStringWithTabSpace(s, builder) + "\n");
                    builder.setLength(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getStringWithTabSpace(String s, StringBuilder builder) {
        long count = s.chars().filter(c -> c == '/').count();
        for (int i = 3; i < count; i++) {
            builder.append("\t");
        }
        return builder.append(s).toString();
    }
}
