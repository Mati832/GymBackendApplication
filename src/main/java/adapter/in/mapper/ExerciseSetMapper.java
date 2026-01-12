package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.exerciseSet.ExerciseSetRequest;
import domain.model.ExerciseSet;

public class ExerciseSetMapper {
    public static ExerciseSet toDomain(ExerciseSetRequest request) {
        return new ExerciseSet(request.id(), request.reps(), request.weightInKg(), request.notes(),
                request.durationInSec(), request.createdAt(), null);
    }
}
