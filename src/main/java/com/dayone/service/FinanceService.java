package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company");
        CompanyEntity companyEntity = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(companyEntity.getId());

        List<Dividend> dividends = new ArrayList<>();
        for (var entity : dividendEntities) {
            dividends.add(new Dividend(entity.getDate(), entity.getDividend()));
        }

        return new ScrapedResult(new Company(companyEntity.getTicker(), companyEntity.getName()),
                dividends);
    }
}
