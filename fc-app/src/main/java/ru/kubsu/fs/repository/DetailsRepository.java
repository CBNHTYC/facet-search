package ru.kubsu.fs.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.fs.entity.Detail;

@Repository
public interface DetailsRepository extends CrudRepository<Detail, Long> {
}
