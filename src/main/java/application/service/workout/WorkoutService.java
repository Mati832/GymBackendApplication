package application.service.workout;

import application.commands.workout.WorkoutFilter;
import application.port.in.workout.*;
import application.port.out.UserPorts.FindUserByIdPort;
import application.port.out.UserPorts.LoadUserByIdPort;
import application.port.out.UserPorts.UpdateUserPort;
import application.port.out.WorkoutPorts.*;
import application.service.exercise.ExerciseService;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.exceptions.ExerciseNotFoundException;
import domain.exceptions.ExerciseSetNotFoundException;
import domain.exceptions.WorkoutNotFoundException;
import domain.model.User;
import domain.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class WorkoutService implements LoadWorkoutByIdUseCase, LoadWorkoutsUseCase, AddWorkoutToUserUseCase,
        DeleteWorkoutInUserUseCase, EditWorkoutInUserUseCase {

    @Inject
    FindWorkoutByIdPort findWorkoutByIdPort;
    @Inject
    SaveWorkoutPort saveWorkoutPort;
    @Inject
    UpdateWorkoutPort updateWorkoutPort;
    @Inject
    DeleteWorkoutPort deleteWorkoutPort;
    @Inject
    ExerciseService exerciseService;
    @Inject
    FindUserByIdPort findUserByIdPort;
    @Inject
    UpdateUserPort updateUserPort;
    @Inject
    LoadWorkoutByIdPort loadWorkoutByIdPort;
    @Inject
    LoadUserByIdPort loadUserByIdPort;
    @Inject
    LoadWorkouts loadWorkouts;
    @Inject
    CountWorkouts countWorkoutsByUserIdPort;

    @Override
    public JPAWorkoutExerciseAdapterResult<Workout> loadWorkoutById(Long workoutId){
        if(workoutId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        Workout inDB = loadWorkoutByIdPort.laodWorkout(workoutId);

        if(inDB == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        return new JPAWorkoutExerciseAdapterResult.Success<>(inDB);
    }

    @Override
    public JPAWorkoutExerciseAdapterResult<Workout> loadWorkouts(WorkoutFilter filter, int page, int size){
        if(filter == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        if(filter.userId() != null && loadUserByIdPort.loadUser(filter.userId())  == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        List<Workout> toBeLoaded =  loadWorkouts.loadWorkouts(filter, page, size);
        int totalPageCount =(int) Math.ceil((double) countWorkoutsByUserIdPort.countWorkouts(filter)/size);

        return new JPAWorkoutExerciseAdapterResult.Paginated<>(toBeLoaded, page, size, totalPageCount);
    }

    @Override
    //Can Add workouts in the DB as a copy to the user OR create new ones
    /*
    IMPORTANT: the user can only create a new empty workout first and THEN add exercises to it
    if the workout is already somewhere in the DB, it is added as a copy to the user
     */
    @Transactional
    public JPAWorkoutExerciseAdapterResult<User> addWorkoutToUser(Long userId, Workout workout) {
        if(userId == null || workout == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        User user = findUserByIdPort.findUserById(userId);
        if(user == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        if(workout.getId() == null && !workout.getExercises().isEmpty())
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        workout.setCreatedByUserId(userId);
        Workout persistedWorkout;

        try{
            if(workout.getId() == null) persistedWorkout = saveWorkoutPort.saveWorkout(workout);
            else persistedWorkout = copyWorkout(userId, workout.getId());
        }catch (ExerciseNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        }catch (WorkoutNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        }catch (ExerciseSetNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);
        }

            if(persistedWorkout == null)
                return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        user.getWorkouts().add(persistedWorkout.getId());
        User updated = updateUserPort.update(user);
        if (updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        return new JPAWorkoutExerciseAdapterResult.Success<>(updated);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<User> deleteWorkoutInUser(Long userId, Long workoutId) {
        if(userId == null || workoutId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        User user = findUserByIdPort.findUserById(userId);
        if(user == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        if(!user.getWorkouts().contains(workoutId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_IN_USER_NOT_FOUND);

        if(findWorkoutByIdPort.findWorkoutById(workoutId) == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);

        try{
            deleteWorkoutPort.deleteWorkout(workoutId);
        }catch (Exception e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_DELETED);
        }

        user.getWorkouts().remove(workoutId);
        User updated = updateUserPort.update(user);
        if (updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        return new  JPAWorkoutExerciseAdapterResult.Success<>(updated);
    }

    @Override
    @Transactional
    //if you want to edit the exercises in the workout, then use the JPAExerciseAdapter
    public JPAWorkoutExerciseAdapterResult<Workout> editWorkoutInUser(Long workoutId, Workout workout) {
        if(workoutId == null || workout == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        if(!workoutId.equals(workout.getId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        Workout toBeEditedWorkout = findWorkoutByIdPort.findWorkoutById(workoutId);
        if(toBeEditedWorkout == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        if(!toBeEditedWorkout.getCreatedByUserId().equals(workout.getCreatedByUserId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(workout.getName() != null) toBeEditedWorkout.setName(workout.getName());
        if(workout.getDescription() != null) toBeEditedWorkout.setDescription(workout.getDescription());
        if(workout.getCreatedAt() != null) toBeEditedWorkout.setCreatedAt(workout.getCreatedAt());

        Workout updated = updateWorkoutPort.update(toBeEditedWorkout);
        if(updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);

        return new JPAWorkoutExerciseAdapterResult.Success<>(updated);
    }

    @Transactional
    public Workout copyWorkout(Long userId, Long workoutId) {
        Workout workoutToCopyFrom = findWorkoutByIdPort.findWorkoutById(workoutId);
        if(workoutToCopyFrom == null) throw new WorkoutNotFoundException("Workout: " + workoutId+ " not found");
        Workout copyWorkout = new Workout();
        copyWorkout.setName(workoutToCopyFrom.getName());
        copyWorkout.setDescription(workoutToCopyFrom.getDescription());
        copyWorkout.setCreatedAt(workoutToCopyFrom.getCreatedAt());
        copyWorkout.setCreatedByUserId(userId);

        Workout persisted = saveWorkoutPort.saveWorkout(copyWorkout);

        List<Long> newExerciseIds = workoutToCopyFrom.getExercises().stream()
                .map(exercise -> exerciseService.copyExercise(userId, persisted.getId(), exercise).getId()).toList();

        persisted.setExercises(new ArrayList<>(newExerciseIds));
        updateWorkoutPort.update(persisted);
        return persisted;
    }
}