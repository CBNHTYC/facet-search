package ru.kubsu.fs.repository;

import ru.kubsu.fs.entity.Model;

import java.util.List;

public interface JdbcModelRepository {
    List<Model> getModels(String query);
}
