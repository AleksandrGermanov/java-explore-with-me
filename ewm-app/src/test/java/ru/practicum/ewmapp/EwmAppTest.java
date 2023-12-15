package ru.practicum.ewmapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EwmAppTest {

    @Test
    public void loadContext() {
        EwmApp.main(null);
    }
}
