package com.ua.buybooks.controller;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ua.buybooks.service.PromCsvImportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportPromCsvController {

    private final PromCsvImportService promCsvImportService;

    @PostMapping("/categories/prom")
    public ResponseEntity<String> importPromCategoriesCsv(@RequestParam("file") MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            List<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader("Номер_групи", "Назва_групи", "Назва_групи_укр", "Ідентифікатор_групи",
                    "Номер_батьківської_групи", "Ідентифікатор_батьківської_групи", "HTML_заголовок_групи",
                    "HTML_заголовок_групи_укр", "HTML_опис_групи", "HTML_опис_групи_укр", "Опис_групи_до_списку_товарних_позицій",
                    "Опис_групи_до_списку_товарних_позицій_укр", "Опис_групи_після_списку_товарних_позицій", "Опис_групи_після_списку_товарних_позицій_укр",
                    "Посилання_зображення_групи")
                .withSkipHeaderRecord()
                .parse(reader)
                .getRecords();

            promCsvImportService.processCategoriesProm(records);
            return ResponseEntity.ok("Import successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Import failed: " + e.getMessage());
        }
    }

    @PostMapping("/items/prom")
    public ResponseEntity<String> importPromCsv(@RequestParam("file") MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            List<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(
                    "Код_товару", "Назва_позиції", "Назва_позиції_укр", "Пошукові_запити", "Пошукові_запити_укр",
                    "Опис", "Опис_укр", "Тип_товару", "Ціна", "Валюта", "Одиниця_виміру",
                    "Мінімальний_обсяг_замовлення", "Оптова_ціна", "Мінімальне_замовлення_опт",
                    "Посилання_зображення", "Наявність", "Кількість", "Номер_групи", "Назва_групи",
                    "Посилання_підрозділу", "Можливість_поставки", "Термін_поставки", "Спосіб_пакування",
                    "Спосіб_пакування_укр", "Унікальний_ідентифікатор", "Ідентифікатор_товару",
                    "Ідентифікатор_підрозділу", "Ідентифікатор_групи", "Виробник", "Країна_виробник",
                    "Знижка", "ID_групи_різновидів", "Особисті_нотатки", "Продукт_на_сайті",
                    "HTML_заголовок", "HTML_заголовок_укр", "HTML_опис", "HTML_опис_укр"
                )
                .withSkipHeaderRecord()
                .parse(reader)
                .getRecords();

            promCsvImportService.processItemRecordsProm(records);
            return ResponseEntity.ok("Import successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Import failed: " + e.getMessage());
        }
    }

}
