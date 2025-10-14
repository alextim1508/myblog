package com.alextim.myblog.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResult {
    private int lastPage;
    private boolean hasPrev;
    private boolean hasNext;
}