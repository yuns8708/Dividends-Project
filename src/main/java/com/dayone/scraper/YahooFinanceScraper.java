package com.dayone.scraper;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    private static final long START_TIME = 86400; // 60 * 60 * 24

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);
        try {
            long now = System.currentTimeMillis() / 1000;

            String resultUrl = String.format(URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(resultUrl);
            Document doc = connection.get();

            Elements parsingTable = doc.getElementsByAttributeValue("class", "table yf-j5d1ld noDl");

            Element tbody = parsingTable.get(0).children().get(1);
            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String text = e.text();
                if (!text.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = text.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexcepted Month enum value -> " + splits[0]);
                }

                dividends.add(
                        new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend)
                );
            }
            scrapResult.setDividendEntities(dividends);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleElement = document.getElementsByAttributeValue("class", "yf-xxbei9").get(0);

            return new Company(ticker, titleElement.text());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
