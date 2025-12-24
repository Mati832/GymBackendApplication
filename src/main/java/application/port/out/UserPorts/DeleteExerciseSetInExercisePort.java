package application.port.out.UserPorts;


import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;

@FunctionalInterface
public interface DeleteExerciseSetInExercisePort {
    public JPAWorkoutExerciseAdapterResult<Exercise> deleteExerciseSetInExercise(Long exerciseId, Long exerciseSetId);
}
