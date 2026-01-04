package adapter.out;

import adapter.mapper.JPAExerciseMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import adapter.out.Entities.UserEntity;
import adapter.out.Entities.WorkoutEntity;
import application.port.out.ExercisePorts.DeleteExercisePort;
import application.port.out.ExercisePorts.FindExerciseByIdPort;
import application.port.out.ExercisePorts.SaveExercisePort;
import application.port.out.ExercisePorts.UpdateExercisePort;
import application.port.out.UserPorts.*;
import domain.exceptions.ExerciseNotFoundException;
import domain.exceptions.ExerciseSetNotFoundException;
import domain.model.Exercise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;

import static adapter.mapper.JPAExerciseMapper.copyFromDB;
import static adapter.mapper.JPAExerciseMapper.toDomain;

@ApplicationScoped
public class JPAExerciseAdapter implements FindExerciseByIdPort, SaveExercisePort, UpdateExercisePort, DeleteExercisePort {
    @Inject
    EntityManager em;

    @Override
    public Exercise findExerciseById(Long exerciseId) {
        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, exerciseId);
        return exerciseEntity == null ? null : toDomain(exerciseEntity);
    }

    @Override
    @Transactional
    public Exercise saveExercise(Exercise exercise) {
        ExerciseEntity exerciseEntity = toEntity(exercise);
        em.persist(exerciseEntity);
        return toDomain(exerciseEntity);
    }

    @Override
    @Transactional
    public Exercise updateExercise(Exercise exercise) {
        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, exercise.getId());
        if(exerciseEntity == null) return null;
        exerciseEntity.setId(exercise.getId());
        exerciseEntity.setName(exercise.getName());
        exerciseEntity.setType(exercise.getType());
        exerciseEntity.setDurationInSec(exercise.getDurationInSec());
        exerciseEntity.setOwner(em.find(UserEntity.class, exercise.getCreatedByUserId()));
        exerciseEntity.getExerciseSets().clear();
        exerciseEntity.getExerciseSets().addAll(exercise.getExerciseSets().stream()
                .map(eSetId -> em.find(ExerciseSetEntity.class, eSetId)).toList());
        if(exercise.getWorkoutId() != null)
            exerciseEntity.setWorkout(em.find(WorkoutEntity.class, exercise.getWorkoutId()));
        return toDomain(exerciseEntity);
    }

    @Override
    @Transactional
    public void deleteExercise(Long exerciseId) {
        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, exerciseId);
        if(exerciseEntity == null) return;

        exerciseEntity.getOwner().getExercises().remove(exerciseEntity);
        if(exerciseEntity.getWorkout() != null)
         exerciseEntity.getWorkout().getExercises().remove(exerciseEntity);
        em.remove(exerciseEntity);
    }

    private ExerciseEntity toEntity(Exercise exercise) {
        ExerciseEntity exerciseEntity = JPAExerciseMapper.toEntity(exercise);
        exerciseEntity.setExerciseSets(new ArrayList<>(exercise.getExerciseSets().stream().map(eSetId -> {
            ExerciseSetEntity eSetEntity = em.find(ExerciseSetEntity.class, eSetId);
            if (eSetEntity == null) throw new ExerciseSetNotFoundException("exerciseSet not found: " + eSetId);
            return  eSetEntity;
        }).toList()));
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