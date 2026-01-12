package adapter.in.DTOs.ResponseDTOs;

import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> data,
        int currentPage,
        int pageSize,
        int totalPages
) {}