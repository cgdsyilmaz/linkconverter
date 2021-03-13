package com.trendyol.applicanttest.repository;

import com.trendyol.applicanttest.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkConverterRepository extends JpaRepository<Link, Long> {

}
