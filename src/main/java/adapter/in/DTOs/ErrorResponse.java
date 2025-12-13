package adapter.in.DTOs;

import jakarta.ws.rs.core.Response;

public record ErrorResponse(Response.Status status, String error, String message) {
}
