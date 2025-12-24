package adapter.out;

import adapter.mapper.JPAWorkoutMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.UserEntity;
import adapter.out.Entities.WorkoutEntity;
import application.port.out.UserPorts.AddWorkoutToUserPort;
import application.port.out.UserPorts.DeleteWorkoutInUserPort;
import application.port.out.UserPorts.EditWorkoutInUserPort;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.exceptions.ExerciseNotFoundException;
import domain.exceptions.UserNotFoundException;
import domain.model.User;
import domain.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import static adapter.mapper.JPAUserMapper.toDomain;
import static adapter.mapper.JPAWorkoutMapper.toDomain;
import static adapter.mapper.JPAWorkoutMapper.copyFromDB;

@ApplicationScoped
public class JPAWorkoutAdapter implements AddWorkoutToUserPort, DeleteWorkoutInUserPort, EditWorkoutInUserPort {
    @Inject
    EntityManager em;

    @Override
    //Can Add workouts in the DB as a copy to the user OR create new ones
    /*
    IMPORTANT: the user can only create a new empty workout first and THEN add exercises to it
    if the workout is already somewhere in the DB, it is added as a copy to the user
     */
    @Transactional
    public JPAWorkoutExerciseAdapterResult<User> addWorkoutToUser(Long userId, Workout workout) {
        UserEntity userEntity = em.find(UserEntity.class, userId);
        if (userEntity == null) return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        WorkoutEntity workoutEntity;

        if(workout.getId() == null){
            try{
                if(!workout.getExercises().isEmpty())
                    return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
                workoutEntity = toEntity(workout);
            }catch (ExerciseNotFoundException | UserNotFoundException e){
                return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
            }
        }else {
            workoutEntity = em.find(WorkoutEntity.class, workout.getId());
            workoutEntity = workoutEntity == null ? null : copyFromDB(workoutEntity);
        }

        if(workoutEntity == null) return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        workoutEntity.setOwner(userEntity);
        em.persist(workoutEntity);
        userEntity.getWorkouts().add(workoutEntity);
        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(userEntity));
    }

    @Override
    public JPAWorkoutExerciseAdapterResult<User> deleteWorkoutInUser(Long userId, Long workoutId) {
        if(userId == null ||  workoutId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        UserEntity userEntity = em.find(UserEntity.class, userId);
        if(userEntity == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);

        WorkoutEntity wEntityToBeDeleted = userEntity.getWorkouts().stream()
                .filter(wEntity -> wEntity.getId().equals(workoutId)).findFirst().orElse(null);
        if(wEntityToBeDeleted == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_IN_USER_NOT_FOUND);

        if(!userEntity.getWorkouts().remove(wEntityToBeDeleted))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_DELETED);

        em.remove(wEntityToBeDeleted);
        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(userEntity));
    }

    @Override
    @Transactional
    //if you want to edit the exercises in the workout, then use the JPAExerciseAdapter
    public JPAWorkoutExerciseAdapterResult<Workout> editWorkoutInUser(Long workoutId, Workout workout) {
        if(workoutId == null || workout == null || workout.getId() == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        if(!workoutId.equals(workout.getId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        WorkoutEntity workoutEntity = em.find(WorkoutEntity.class, workoutId);
        if(workoutEntity == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        if(!workoutEntity.getOwner().getId().equals(workout.getCreatedByUserId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(workout.getName() != null) workoutEntity.setName(workout.getName());
        if(workout.getDescription() != null) workoutEntity.setDescription(workout.getDescription());
        if(workout.getCreatedAt() != null) workoutEntity.setCreatedAt(workout.getCreatedAt());

        return new  JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity));
    }

    private WorkoutEntity toEntity(Workout workout){
        WorkoutEntity workoutEntity = JPAWorkoutMapper.toEntity(workout);
        workoutEntity.setExercises(workout.getExercises().stream().map(eId -> {
            ExerciseEntity eEntity = em.find(ExerciseEntity.class, eId);
            if(eEntity == null) throw new ExerciseNotFoundException("exercise not found: " + eId);
            return  eEntity;
        }).toList());
        if(workout.getCreatedByUserId() == null) return  workoutEntity;
        UserEntity userEntity = em.find(UserEntity.class, workout.getCreatedByUserId());
        if(userEntity == null) throw new UserNotFoundException("user not found: " + workout.getCreatedByUserId());
        workoutEntity.setOwner(userEntity);
        return  workoutEntity;
    }
}