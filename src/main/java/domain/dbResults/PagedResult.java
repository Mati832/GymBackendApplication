package domain.dbResults;

import java.util.List;

public record PagedResult<T>(
        List<T> data,
        long totalCount,
        int offset,
        int size
) {}