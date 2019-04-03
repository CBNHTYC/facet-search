package fs.repository;

import fs.entity.Phone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepository extends CrudRepository<Phone, Long> {

//    @Query(value = "select * from phones where ?", facets, nativeQuery = true)
//    List<Phone> findAllByAny(@Param("facets") String facets);

}
