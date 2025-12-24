package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import domain.model.User;

@FunctionalInterface
public interface AddExerciseSetToExercisePort {
    public JPAWorkoutExerciseAdapterResult<Exercise> addExerciseSetToExercise(Long exerciseId, ExerciseSet exerciseSet);

    @FunctionalInterface
    interface AddExerciseToUserPort {
        public JPAWorkoutExerciseAdapterResult<User> addExerciseToUser(Long userId, Exercise exercise);
    }
}
