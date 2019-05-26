package ru.kubsu.fs.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kubsu.fs.entity.Model;

import java.util.List;

public interface ModelRepository extends CrudRepository<Model, Long> {
    List<Model> findAll();
    Model findByModelId(Long id);
}
