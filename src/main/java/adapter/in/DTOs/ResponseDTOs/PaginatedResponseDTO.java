package adapter.in.DTOs.ResponseDTOs;

import jakarta.ws.rs.core.Link;

import java.net.URI;
import java.util.List;

public record PaginatedResponseDTO<T>(
        List<?> data,
        int currentPage,
        int pageSize,
        int totalPages
) {}