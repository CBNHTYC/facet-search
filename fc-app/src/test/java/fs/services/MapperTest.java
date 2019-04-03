package fs.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTest {

    @Test
    public void map() {
        List<String> a = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            a.add("a");
        }

        a.forEach(s -> s = "b");

        System.out.println(a);
    }
}