package ru.practicum.ewmapp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

class PaginationInfoTest {
    @Test
    void asPageRequest() {
        PaginationInfo info1 = new PaginationInfo(0, 10);
        Assertions.assertEquals(PageRequest.of(0, 10), info1.asPageRequest());

        PaginationInfo info2 = new PaginationInfo(3, 2);
        Assertions.assertEquals(PageRequest.of(1, 2), info2.asPageRequest());
    }
}