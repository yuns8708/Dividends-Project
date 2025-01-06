package com.dayone.scheduler;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        List<CompanyEntity> companyEntities = this.companyRepository.findAll();

        for (var company : companyEntities) {
            log.info("scraping scheduler is started : " + company.getName());
            ScrapedResult result = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName())
            );
            result.getDividendEntities().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        // 배당금 정보가 db에 없으면 저장
                        if (!exists) {
                            this.dividendRepository.save(e);
                            log.info("insert new dividend : " + e.toString());
                        }
                    });

            // 연속 요청 방지
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
