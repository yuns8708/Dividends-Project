package com.dayone.persist;

import com.dayone.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);
    Optional<CompanyEntity> findByName(String name);
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String str, Pageable pageable);
    Optional<CompanyEntity> findByTicker(String ticker);
}
