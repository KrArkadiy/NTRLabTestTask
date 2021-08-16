import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;

public class JsoupSolve {
    private static final String PATH_TO_FILE = "\\src\\main\\resources\\";

    public List<String> websiteParse() {
        List<String> contentList = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect("https://yandex.ru/news/rubric/computers")
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .get();

            Elements titles = doc.select("h2.mg-card__title");
            Elements annotations = doc.select("div.mg-card__annotation");
            titles.forEach(x -> contentList.add(x.text()));
            annotations.forEach(x -> contentList.add(x.text()));
        } catch (IOException exception) {
            System.out.println("Error occurred " + exception.getMessage());
        }
        if(contentList.size() == 0) {
            throw new RuntimeException("No suitable elements found");
        } else {
            return contentList;
        }
    }

    public void requiredWordsFrequency(String fileName){
        List<String> contentList = websiteParse();
        Map<String, Integer> resultMap = new HashMap<>();
        Map<String, Integer> finalMap = new HashMap<>();
        String[] requiredWordsList = requiredWordsList(fileName);
        contentList.stream().map(e -> e.replaceAll("[,.]", "")
                .toLowerCase().split(" ")).forEach(x -> Arrays.stream(x)
                .forEach(e -> resultMap.merge(e, 1, (oldValue, newValue) -> oldValue + 1)));
        for (String s : requiredWordsList) {
            if (resultMap.containsKey(s.toLowerCase())) {
                finalMap.put(s, resultMap.get(s));
            }
        }
        finalMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(System.out::println);
    }

    public String[] requiredWordsList(String fileName) {
        List<String> requiredWords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getPath(fileName))))) {
            String line;
            while ((line = br.readLine()) != null) {
                Arrays.stream(line.split(",")).forEach(x -> requiredWords.add(x.trim()));
            }
        } catch (IOException exception) {
            System.out.println("Error occurred " + exception.getMessage());
        }
        return String.join(" ", requiredWords).split(" ");
    }

    private String getPath(String fileName){
        return Paths.get("").toAbsolutePath() + PATH_TO_FILE + fileName;
    }
}

