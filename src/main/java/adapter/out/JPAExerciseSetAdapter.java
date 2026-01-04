package adapter.out;

import adapter.mapper.JPAExerciseSetMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import application.port.out.ExerciseSetPorts.DeleteExerciseSetPort;
import application.port.out.ExerciseSetPorts.FindExerciseSetByIdPort;
import application.port.out.ExerciseSetPorts.SaveExerciseSetPort;
import application.port.out.ExerciseSetPorts.UpdateExerciseSetPort;
import domain.model.ExerciseSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import static adapter.mapper.JPAExerciseSetMapper.toDomain;

@ApplicationScoped
public class JPAExerciseSetAdapter implements FindExerciseSetByIdPort, SaveExerciseSetPort, UpdateExerciseSetPort, DeleteExerciseSetPort {
    @Inject
    EntityManager em;

    @Override
    public ExerciseSet findExerciseSetById(Long exerciseSetId) {
        ExerciseSetEntity exerciseSetEntity = em.find(ExerciseSetEntity.class, exerciseSetId);
        return exerciseSetEntity == null ? null : toDomain(exerciseSetEntity);
    }

    @Override
    @Transactional
    public ExerciseSet saveExerciseSet(ExerciseSet exerciseSet) {
        ExerciseSetEntity exerciseSetEntity = toEntity(exerciseSet);
        em.persist(exerciseSetEntity);
        return toDomain(exerciseSetEntity);
    }

    @Override
    @Transactional
    public ExerciseSet updateExerciseSet(ExerciseSet exerciseSet) {
        ExerciseSetEntity exerciseSetEntity = em.find(ExerciseSetEntity.class, exerciseSet.getId());
        if(exerciseSetEntity == null) return null;
        exerciseSetEntity.setReps(exerciseSet.getReps());
        exerciseSetEntity.setWeightInKg(exerciseSet.getWeightInKg());
        exerciseSetEntity.setNotes(exerciseSet.getNotes());
        exerciseSetEntity.setDurationInSec(exerciseSet.getDurationInSec());
        exerciseSetEntity.setCreatedAt(exerciseSet.getCreatedAt());
        exerciseSetEntity.setExercise(em.find(ExerciseEntity.class, exerciseSet.getBelongsToExercise()));
        return toDomain(exerciseSetEntity);
    }

    @Override
    @Transactional
    public void deleteExerciseSet(Long exerciseSetId) {
        ExerciseSetEntity exerciseSetEntity = em.find(ExerciseSetEntity.class, exerciseSetId);
        if(exerciseSetEntity == null) return;
        exerciseSetEntity.getExercise().getExerciseSets().remove(exerciseSetEntity);
        em.remove(exerciseSetEntity);
    }

    private ExerciseSetEntity toEntity(ExerciseSet exerciseSet) {
        ExerciseSetEntity exerciseSetEntity = JPAExerciseSetMapper.toEntity(exerciseSet);
        ExerciseEntity belongsToESet =
                exerciseSet.getBelongsToExercise() == null ? null : em.find(ExerciseEntity.class, exerciseSet.getBelongsToExercise());
        if(belongsToESet == null) return exerciseSetEntity;
        exerciseSetEntity.setExercise(belongsToESet);
        return exerciseSetEntity;
    }
}
