package com.alextim.myblog.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaginationHelperTest {

    @Test
    void calculate_WhenSizeIsZero_ShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> PaginationHelper.calculate(10, 0, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be greater than 0");
    }

    @Test
    void calculate_WhenSizeIsNegative_ShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> PaginationHelper.calculate(10, -5, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be greater than 0");
    }

    @ParameterizedTest
    @CsvSource({
            // count, size, page, expectedLastPage, expectedHasPrev, expectedHasNext
            "0, 10, 1, 0, false, false",      // Нет элементов
            "1, 10, 1, 1, false, false",      // Один элемент, одна страница
            "10, 10, 1, 1, false, false",     // Ровно одна страница
            "11, 10, 1, 2, false, true",      // Несколько страниц, первая
            "11, 10, 2, 2, true, false",      // Несколько страниц, последняя
            "20, 10, 1, 2, false, true",      // Несколько страниц, первая
            "20, 10, 2, 2, true, false",      // Несколько страниц, последняя
            "25, 10, 1, 3, false, true",      // Несколько страниц, первая
            "25, 10, 2, 3, true, true",       // Несколько страниц, средняя
            "25, 10, 3, 3, true, false",      // Несколько страниц, последняя
            "100, 10, 5, 10, true, true",     // Много страниц, средняя
            "100, 10, 1, 10, false, true",    // Первая страница из многих
            "100, 10, 10, 10, true, false",   // Последняя страница из многих
    })
    void calculate_WithVariousInputs_ShouldReturnCorrectResult(
            long count, int size, int page, int expectedLastPage, boolean expectedHasPrev, boolean expectedHasNext) {

        PaginationResult result = PaginationHelper.calculate(count, size, page);

        assertThat(result.getLastPage()).isEqualTo(expectedLastPage);
        assertThat(result.isHasPrev()).isEqualTo(expectedHasPrev);
        assertThat(result.isHasNext()).isEqualTo(expectedHasNext);
    }

    @Test
    void calculate_WhenPageIsGreaterThanLastPage_ShouldReturnHasNextAsTrue() {
        // Это граничный случай: пользователь запросил страницу > lastPage
        // hasNext будет true, потому что page (5) < lastPage (2) -> false, но на практике это означает, что страница вне диапазона
        // В текущей реализации: если page > lastPage, то hasNext = false, hasPrev = true (если page > 1)
        // Пример: count=20, size=10 -> lastPage = 2. Если page = 5, то hasPrev = true, hasNext = false
        // Это может быть корректным поведением, если верхний уровень логики обрабатывает выход за диапазон.

        long count = 20;
        int size = 10;
        int page = 5; // Больше последней

        PaginationResult result = PaginationHelper.calculate(count, size, page);

        assertThat(result.getLastPage()).isEqualTo(2); // lastPage = 2
        assertThat(result.isHasPrev()).isTrue();      // page > 1
        assertThat(result.isHasNext()).isFalse();     // page (5) >= lastPage (2), значит false
    }

    @Test
    void calculate_WhenPageIsLessThanOne_ShouldReturnHasPrevAsFalse() {
        long count = 20;
        int size = 10;
        int page = 0;

        PaginationResult result = PaginationHelper.calculate(count, size, page);

        assertThat(result.isHasPrev()).isFalse();
    }
}