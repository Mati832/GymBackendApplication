package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.exercise.ExerciseRequest;
import adapter.in.DTOs.ResponseDTOs.exercises.ExerciseResponse;
import domain.model.Exercise;

import java.util.ArrayList;

public class ExerciseMapper {
    public static Exercise toDomain(ExerciseRequest request) {
        return  new Exercise(request.id(), request.name(), request.type(), request.durationInSec(),
                null, new ArrayList<>(),request.createdAt(), null);
    }

    public static ExerciseResponse toResponse(Exercise exercise) {
        return new ExerciseResponse(exercise.getName(), exercise.getType(), exercise.getDurationInSec(), exercise.getCreatedAt());
    }
}
