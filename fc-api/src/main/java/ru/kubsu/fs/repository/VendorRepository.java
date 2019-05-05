package ru.kubsu.fs.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kubsu.fs.entity.Vendor;

import java.util.List;

public interface VendorRepository extends CrudRepository<Vendor, Long> {
    public List<Vendor> findAll();
}
