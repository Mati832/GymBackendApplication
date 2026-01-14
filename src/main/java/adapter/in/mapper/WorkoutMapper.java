package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.workout.WorkoutRequest;
import adapter.in.DTOs.ResponseDTOs.workout.WorkoutResponse;
import domain.model.Workout;

import java.util.ArrayList;

public class WorkoutMapper {
    public static Workout toDomain(WorkoutRequest request){
        return new Workout(request.id(), request.name(), request.description(), request.createdAt(), new ArrayList<>(), null);
    }

    public static WorkoutResponse toResponse(Workout workout){
        return new WorkoutResponse(workout.getName(), workout.getDescription(), workout.getCreatedAt());
    }
}
