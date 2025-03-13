package com.ua.buybooks.repo.wp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ua.buybooks.entity.wp.ItemWP;

@Repository
public interface ItemWPRepository extends JpaRepository<ItemWP, Long> {
    Optional<ItemWP> findByIdOrNameUaOrNameRu(Long aLong, String nameUa, String nameRu);
}
