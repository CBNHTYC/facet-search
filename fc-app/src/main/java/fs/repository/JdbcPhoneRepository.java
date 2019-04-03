package fs.repository;

import fs.entity.Phone;

import java.util.List;

public interface JdbcPhoneRepository {
    List<Phone> getPhones(String query);
}
