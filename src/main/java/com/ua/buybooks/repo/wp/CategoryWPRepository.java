package com.ua.buybooks.repo.wp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ua.buybooks.entity.wp.CategoryWP;

@Repository
public interface CategoryWPRepository extends JpaRepository<CategoryWP, Long> {
}
