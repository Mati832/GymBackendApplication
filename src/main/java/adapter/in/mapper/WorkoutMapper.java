package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.workout.WorkoutRequest;
import domain.model.Workout;

import java.util.ArrayList;

public class WorkoutMapper {
    public static Workout toDomain(WorkoutRequest request){
        return new Workout(request.id(), request.name(), request.description(), request.createdAt(), new ArrayList<>(), null);
    }
}
