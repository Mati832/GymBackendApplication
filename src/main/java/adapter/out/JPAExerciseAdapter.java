package adapter.out;

import adapter.mapper.JPAExerciseMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import adapter.out.Entities.UserEntity;
import adapter.out.Entities.WorkoutEntity;
import application.port.out.UserPorts.*;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.exceptions.ExerciseSetNotFoundException;
import domain.model.Exercise;
import domain.model.User;
import domain.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import static adapter.mapper.JPAExerciseMapper.copyFromDB;
import static adapter.mapper.JPAWorkoutMapper.toDomain;
import static adapter.mapper.JPAUserMapper.toDomain;
import static adapter.mapper.JPAExerciseMapper.toDomain;

@ApplicationScoped
public class JPAExerciseAdapter implements AddExerciseToWorkoutPort, AddExerciseToUserPort, DeleteExerciseInUserPort,
        DeleteExerciseInWorkoutPort, EditExercisePort {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Workout> addExerciseToWorkout(Long workoutId, Exercise exercise) {
        if(workoutId == null || exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        WorkoutEntity workoutEntity = em.find(WorkoutEntity.class, workoutId);
        if (workoutEntity == null) return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);

        ExerciseEntity exerciseEntity = resolveExerciseEntityForAdd(exercise);
        if(exerciseEntity == null) return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        exerciseEntity.setOwner(workoutEntity.getOwner());
        exerciseEntity.setWorkout(workoutEntity);
        em.persist(exerciseEntity);
        workoutEntity.getExercises().add(exerciseEntity);
        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity));
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<User> addExerciseToUser(Long userId, Exercise exercise) {
        if(userId == null || exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        UserEntity userEntity = em.find(UserEntity.class, userId);
        if(userEntity == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        ExerciseEntity exerciseEntity =  resolveExerciseEntityForAdd(exercise);
        if(exerciseEntity == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        exerciseEntity.setOwner(userEntity);
        exerciseEntity.setWorkout(null);
        em.persist(exerciseEntity);
        userEntity.getExercises().add(exerciseEntity);

        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(userEntity));
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<User> deleteExerciseInUser(Long userId, Long exerciseId) {
        if(exerciseId == null || userId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        UserEntity userEntity = em.find(UserEntity.class, userId);
        if(userEntity == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        ExerciseEntity exerciseEntityInUser = userEntity.getExercises().stream()
                .filter(eEntity -> eEntity.getId().equals(exerciseId)).findFirst().orElse(null);
        if(exerciseEntityInUser == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_IN_USER_NOT_FOUND);

        //remove the exercise from the User
        if(!userEntity.getExercises().remove(exerciseEntityInUser))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_DELETED);

        em.remove(exerciseEntityInUser);
        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(userEntity));
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Workout> deleteExerciseInWorkout(Long workoutId, Long exerciseId) {
        if(exerciseId == null || workoutId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        WorkoutEntity workoutEntity = em.find(WorkoutEntity.class, workoutId);
        if(workoutEntity == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);

        ExerciseEntity exerciseEntityInWorkout = workoutEntity.getExercises().stream()
                .filter(eEntity -> eEntity.getId().equals(exerciseId)).findFirst().orElse(null);
        if(exerciseEntityInWorkout == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_IN_WORKOUT_NOT_FOUND);

        //remove the exercise from the Workout
        if(!workoutEntity.getExercises().remove(exerciseEntityInWorkout))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_DELETED);

        em.remove(exerciseEntityInWorkout);
        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity));
    }

    @Override
    @Transactional
    //If you want to edit the sets of an exercise, then use the JPAExerciseSetAdapter
    //it doesn't matter where the exercise is. It works for exercises both in user and workouts
    public JPAWorkoutExerciseAdapterResult<Exercise> editExercise(Long exerciseId, Exercise exercise) {
        if(exerciseId == null || exercise == null || exercise.getId() == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        if(!exercise.getId().equals(exerciseId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        ExerciseEntity eEntityToBeEdited  = em.find(ExerciseEntity.class, exerciseId);
        if(eEntityToBeEdited == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        if(!eEntityToBeEdited.getOwner().getId().equals(exercise.getCreatedByUserId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(exercise.getName() != null) eEntityToBeEdited.setName(exercise.getName());
        if(exercise.getType() != null) eEntityToBeEdited.setType(exercise.getType());
        if(exercise.getDurationInSec() != null) eEntityToBeEdited.setDurationInSec(exercise.getDurationInSec());
        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(eEntityToBeEdited));
    }

    private ExerciseEntity toEntity(Exercise exercise) {
        ExerciseEntity exerciseEntity = JPAExerciseMapper.toEntity(exercise);
        exerciseEntity.setExerciseSets(exercise.getExerciseSets().stream().map(eSetId -> {
            ExerciseSetEntity eSetEntity = em.find(ExerciseSetEntity.class, eSetId);
            if (eSetEntity == null) throw new ExerciseSetNotFoundException("exerciseSet not found: " + eSetId);
            return  eSetEntity;
        }).toList());
        if(exercise.getCreatedByUserId() != null) exerciseEntity.setOwner(em.find(UserEntity.class, exercise.getCreatedByUserId()));
        if(exercise.getWorkoutId() != null) exerciseEntity.setWorkout(em.find(WorkoutEntity.class, exercise.getWorkoutId()));
        return  exerciseEntity;
    }

    //Returns a copy of the Exercise in the DB OR if it is not in the DB, then it will create a new ExerciseEntity
    //The Result is always a completely new ExerciseEntity with no linking to the DB
    private ExerciseEntity resolveExerciseEntityForAdd(Exercise exercise){
        ExerciseEntity exerciseEntity;
        if(exercise.getId() == null){
            try {
                if(!exercise.getExerciseSets().isEmpty()) return null;
                exerciseEntity = toEntity(exercise);
            }catch (ExerciseSetNotFoundException e){
                return null;
            }
        }else {
            exerciseEntity = em.find(ExerciseEntity.class, exercise.getId());
            exerciseEntity = exerciseEntity == null ? null : copyFromDB(exerciseEntity);
        }
        return exerciseEntity;
    }
}