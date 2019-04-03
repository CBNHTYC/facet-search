package ru.kubsu.fs.model;

import lombok.Getter;

public enum ReplicationStatus {

    CREATED("Создана новая учетная запись - %s%s"),
    DUPLICATED_WHILE_CREATING("Найдено совпадение. Учетная запись не создана - %s%s"),
    BLOCKED("Заблокирована учетная запись - %s%s"),
    DUPLICATED_WHILE_BLOCKING("Найдено совпадение. Учетная запись не заблокирована - %s%s"),
    NOT_FOUND_WHILE_BLOCKING("Совпадений не найдено. Учетная запись не заблокирована - %s%s"),
    ERROR("Исключительная ситуация c - %s, исключение %s"),
    INVALID_DATE_WHILE_BLOCKING("Недействительная дата при блокировке - %s%s");

    @Getter
    private final String template;

    ReplicationStatus(String template) {
        this.template = template;
    }

}
