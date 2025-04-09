package com.ua.buybooks.repo.wp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ua.buybooks.entity.wp.CategoryWP;

@Repository
public interface CategoryWPRepository extends JpaRepository<CategoryWP, Long> {
    Optional<CategoryWP> findByCategoryName(String categoryName);

    Optional<CategoryWP> findByCategoryIdAndCategoryName(Long categoryId, String categoryName);
}
