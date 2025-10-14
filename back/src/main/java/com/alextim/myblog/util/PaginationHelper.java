package com.alextim.myblog.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class PaginationHelper {


    public static PaginationResult calculate(long count, int size, int page) {
        log.debug("Calculating pagination for count={}, size={}, page={}", count, size, page);

        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        int lastPage = (int) Math.ceil((double) count / size);
        boolean hasPrev = page > 1;
        boolean hasNext = page < lastPage;

        log.debug("Calculated: lastPage={}, hasPrev={}, hasNext={}", lastPage, hasPrev, hasNext);
        return new PaginationResult(lastPage, hasPrev, hasNext);
    }
}