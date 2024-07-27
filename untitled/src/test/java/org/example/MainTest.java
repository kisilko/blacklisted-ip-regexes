package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class MainTest {

    @Test
    void test1() {
        final List<String> ipRegexes = List.of("*111.*", "123.*", "34.*");
        final List<String> requests = List.of("123.1.23.34", "121.1.23.34", "121.1.23.34", "34.1.23.34", "121.1.23.34", "12.1.23.34", "121.1.23.34");

        assertThat(Main.processRequests(ipRegexes, requests)).isEqualTo(List.of(1, 0, 0, 1, 1, 0, 0));
    }
}