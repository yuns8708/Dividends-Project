package com.dayone;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);


        try {
            Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history/?frequency=1mo&period1=99153000&period2=1735568506");
            Document doc = connection.get();

            Elements elements = doc.getElementsByAttributeValue("class", "table yf-j5d1ld noDl");
//            Element element = elements.get(0);

            Element tbody = elements.get(0).children().get(1);
            for (Element e : tbody.children()) {
                String text = e.text();
                if (!text.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = text.split(" ");
                String month = splits[0];
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];
                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }



//            System.out.println(tbody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
