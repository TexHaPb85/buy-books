package com.ua.buybooks.service;

import org.springframework.stereotype.Service;

import com.ua.buybooks.repo.wp.CategoryWPRepository;
import com.ua.buybooks.repo.wp.ImageWPRepository;
import com.ua.buybooks.repo.wp.ItemWPRepository;
import com.ua.buybooks.repo.wp.TagWPRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromCsvItemsImportService {
    private final ItemWPRepository itemWPRepository;
    private final CategoryWPRepository categoryWPRepository;
    private final ImageWPRepository imageWPRepository;
    private final TagWPRepository tagWPRepository;
}
