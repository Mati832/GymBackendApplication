package application.service.exercise;

import application.commands.exercise.ExerciseFilter;
import application.port.in.exercise.*;
import application.port.out.ExercisePorts.*;
import application.port.out.UserPorts.FindUserByIdPort;
import application.port.out.UserPorts.LoadUserByIdPort;
import application.port.out.UserPorts.UpdateUserPort;
import application.port.out.WorkoutPorts.FindWorkoutByIdPort;
import application.port.out.WorkoutPorts.LoadWorkoutByIdPort;
import application.port.out.WorkoutPorts.UpdateWorkoutPort;
import application.service.exerciseSet.ExerciseSetService;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.exceptions.ExerciseNotFoundException;
import domain.exceptions.ExerciseSetNotFoundException;
import domain.exceptions.UserNotFoundException;
import domain.exceptions.WorkoutNotFoundException;
import domain.model.Exercise;
import domain.model.User;
import domain.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ExerciseService implements LoadExerciseByIdUseCase, LoadExercisesUseCase, AddExerciseToUserUseCase, AddExerciseToWorkoutUseCase,
        DeleteExerciseInUserUseCase, DeleteExerciseInWorkoutUseCase, EditExerciseUseCase {

    @Inject
    FindExerciseByIdPort findExerciseByIdPort;
    @Inject
    SaveExercisePort saveExercisePort;
    @Inject
    UpdateExercisePort updateExercisePort;
    @Inject
    DeleteExercisePort deleteExercisePort;
    @Inject
    ExerciseSetService exerciseSetService;
    @Inject
    FindUserByIdPort findUserByIdPort;
    @Inject
    UpdateUserPort updateUserPort;
    @Inject
    FindWorkoutByIdPort findWorkoutByIdPort;
    @Inject
    UpdateWorkoutPort updateWorkoutPort;
    @Inject
    LoadExerciseByIdPort loadExerciseByIdPort;
    @Inject
    LoadExercisesPort loadExercisesPort;
    @Inject
    CountExercisesPort countExercisesPort;
    @Inject
    LoadUserByIdPort loadUserByIdPort;
    @Inject
    LoadWorkoutByIdPort loadWorkoutByIdPort;

    @Override
    public JPAWorkoutExerciseAdapterResult<Exercise> loadExerciseById(Long exerciseId) {
        if(exerciseId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        Exercise loaded = loadExerciseByIdPort.loadExerciseById(exerciseId);
        if(loaded == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        return new JPAWorkoutExerciseAdapterResult.Success<>(loaded);
    }

    @Override
    public JPAWorkoutExerciseAdapterResult<Exercise> loadExercises(ExerciseFilter filter, int page, int size){
        if(filter == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        if(filter.userId() != null && filter.workoutId() != null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(filter.userId() != null && loadUserByIdPort.loadUser(filter.userId())  == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        if(filter.workoutId() != null && loadWorkoutByIdPort.laodWorkout(filter.workoutId())  == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);

        List<Exercise> loaded = loadExercisesPort.loadExercises(filter, page, size);
        int totalPageCount =(int) Math.ceil((double) countExercisesPort.countExercises(filter)/size);
        return new JPAWorkoutExerciseAdapterResult.Paginated<>(loaded, page, size, totalPageCount);
    }


    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Exercise> addExerciseToUser(Long userId, Exercise exercise) {
        if(userId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);
        if(exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        User user = findUserByIdPort.findUserById(userId);
        if(user == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        if(!user.getId().equals(userId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);

        Exercise persistedExercise;
        try{
            if(exercise.getId() == null) persistedExercise = saveExercisePort.saveExercise(exercise);
            else persistedExercise = copyExercise(userId, null, exercise.getId());
        }catch (ExerciseNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        }catch (WorkoutNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        }catch (ExerciseSetNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);
        }catch (UserNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        }

        user.getExercises().add(persistedExercise.getId());
        User updated = updateUserPort.update(user);
        if(updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        return new JPAWorkoutExerciseAdapterResult.Created<>(persistedExercise);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Exercise> addExerciseToWorkout(Long workoutId, Exercise exercise) {
        if(workoutId == null || exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        if(exercise.getCreatedByUserId() == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);

        Workout workout = findWorkoutByIdPort.findWorkoutById(workoutId);
        if(workout == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        if(!workout.getCreatedByUserId().equals(exercise.getCreatedByUserId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);

        exercise.setWorkoutId(workoutId);
        Exercise persistedExercise;
        try{
            if(exercise.getId() == null) persistedExercise = saveExercisePort.saveExercise(exercise);
            else persistedExercise = copyExercise(workout.getCreatedByUserId(), workoutId, exercise.getId());
        }catch (ExerciseNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        }catch (WorkoutNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        }catch (ExerciseSetNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);
        }catch (UserNotFoundException e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        }

        workout.getExercises().add(persistedExercise.getId());
        Workout updatedWorkout = updateWorkoutPort.update(workout);

        if(updatedWorkout == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);

        return new  JPAWorkoutExerciseAdapterResult.Created<>(persistedExercise);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Void> deleteExerciseInUser(Long userId, Long exerciseId) {
        if(userId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);
        if(exerciseId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        User user = findUserByIdPort.findUserById(userId);
        if(user == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        if(!user.getExercises().contains(exerciseId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_IN_USER_NOT_FOUND);

        Exercise exercise = findExerciseByIdPort.findExerciseById(exerciseId);
        if(exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        if(!exercise.getCreatedByUserId().equals(user.getId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);

        try{
            deleteExercisePort.deleteExercise(exerciseId);
        }catch (Exception e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_DELETED);
        }

        user.getExercises().remove(exerciseId);
        User updated = updateUserPort.update(user);
        if(updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        return new JPAWorkoutExerciseAdapterResult.Deleted<>(true);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Void> deleteExerciseInWorkout(Long userId, Long workoutId, Long exerciseId) {
        if(userId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);
        if(exerciseId == null || workoutId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        Workout workout = findWorkoutByIdPort.findWorkoutById(workoutId);
        if(workout == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        if(!workout.getCreatedByUserId().equals(userId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);

        if(!workout.getExercises().contains(exerciseId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_IN_WORKOUT_NOT_FOUND);

        if(findExerciseByIdPort.findExerciseById(exerciseId) == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        try{
            deleteExercisePort.deleteExercise(exerciseId);
        }catch (Exception e){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_DELETED);
        }

        workout.getExercises().remove(exerciseId);
        Workout updated = updateWorkoutPort.update(workout);
        if(updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);

        return new JPAWorkoutExerciseAdapterResult.Deleted<>(true);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Exercise> editExercise(Long exerciseId, Exercise exercise) {
        if(exercise == null || exercise.getId() == null || !exercise.getId().equals(exerciseId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        if(exercise.getCreatedByUserId() == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);

        Exercise toBeEdited = findExerciseByIdPort.findExerciseById(exerciseId);
        if(toBeEdited == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        if(!toBeEdited.getCreatedByUserId().equals(exercise.getCreatedByUserId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);

        if(exercise.getName() != null) toBeEdited.setName(exercise.getName());
        if(exercise.getType() != null) toBeEdited.setType(exercise.getType());
        if(exercise.getDurationInSec() != null) toBeEdited.setDurationInSec(exercise.getDurationInSec());
        if(exercise.getCreatedAt() != null) toBeEdited.setCreatedAt(exercise.getCreatedAt());

        Exercise updated = updateExercisePort.updateExercise(toBeEdited);
        if(updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        return new JPAWorkoutExerciseAdapterResult.Updated<>(updated);
    }

    @Transactional
    public Exercise copyExercise(Long userId, Long workoutId, Long exerciseId){
        Exercise exerciseToCopyFrom = findExerciseByIdPort.findExerciseById(exerciseId);
        if(exerciseToCopyFrom == null) throw new ExerciseNotFoundException("Exercise: " + exerciseId + " not found");
        Exercise copyExercise = new Exercise();
        copyExercise.setName(exerciseToCopyFrom.getName());
        copyExercise.setType(exerciseToCopyFrom.getType());
        copyExercise.setDurationInSec(exerciseToCopyFrom.getDurationInSec());
        copyExercise.setCreatedByUserId(userId);
        copyExercise.setWorkoutId(workoutId);

        Exercise persisted = saveExercisePort.saveExercise(copyExercise);

       List<Long> newSetIds =  exerciseToCopyFrom.getExerciseSets().stream()
                .map(eSet -> exerciseSetService.copyExerciseSet(persisted.getId(), eSet).getId()).toList();

       persisted.setExerciseSets(new ArrayList<>(newSetIds));
       updateExercisePort.updateExercise(persisted);

        return persisted;
    }
}
