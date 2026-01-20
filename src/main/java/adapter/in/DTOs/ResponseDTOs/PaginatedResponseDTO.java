package adapter.in.DTOs.ResponseDTOs;


import java.util.List;

public record PaginatedResponseDTO(
        List<?> data,
        int currentPage,
        int pageSize,
        int totalPages
) {}