package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.ExerciseSet;

@FunctionalInterface
public interface EditExerciseSetPort {
    public JPAWorkoutExerciseAdapterResult<ExerciseSet>  editExerciseSet(Long exerciseSetId, ExerciseSet exerciseSet);
}
