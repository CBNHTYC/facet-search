package fs.repository;

import fs.entity.Phone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
@Transactional
public class JdbcPhoneRepositoryImpl implements JdbcPhoneRepository{
    private static final BeanPropertyRowMapper<Phone> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Phone.class);
    private final JdbcTemplate jdbcTemplate;

    private static final String BASIC_QUERY = "SELECT * FROM phones WHERE ";

    public JdbcPhoneRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Phone> getPhones(String query) {
        List<Phone> phoneList = Collections.emptyList();
        try {
            StringWriter sw = new StringWriter();
            sw.append(BASIC_QUERY).append(query);
            phoneList = jdbcTemplate.query(sw.toString(), ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            log.info("Empty result for query: {}", query);
        } catch (Exception e) {
            log.info("Error result for query: {}", query, e.getMessage());
        }
        return phoneList;
    }
}
