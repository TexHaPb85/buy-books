package com.ua.buybooks.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class FailureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private FailureType type;

    private Long itemId;

    private String itemName;

    private String failureReason;

    public static enum FailureType {
        IMPORT, UPLOAD
    }
}