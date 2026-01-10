package adapter.in.DTOs.ResponseDTOs.member;

import java.util.List;

public record PagedAssignedWorkoutResponse(List<AssignedWorkoutResponse> data,
                                           long totalCount,
                                           int offset,
                                           int size) {
}
