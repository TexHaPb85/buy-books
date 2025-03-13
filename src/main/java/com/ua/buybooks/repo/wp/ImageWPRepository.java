package com.ua.buybooks.repo.wp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ua.buybooks.entity.wp.ImageWP;

@Repository
public interface ImageWPRepository extends JpaRepository<ImageWP, Long> {
}
