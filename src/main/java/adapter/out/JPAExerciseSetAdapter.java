package adapter.out;

import adapter.mapper.JPAExerciseSetMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import application.commands.exerciseSet.ExerciseSetFilter;
import application.port.out.ExerciseSetPorts.*;
import domain.model.ExerciseSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

import static adapter.mapper.JPAExerciseSetMapper.toDomain;

@ApplicationScoped
public class JPAExerciseSetAdapter implements FindExerciseSetByIdPort, LoadExerciseSetsPort, CountExerciseSetsPort, SaveExerciseSetPort,
        UpdateExerciseSetPort, DeleteExerciseSetPort {
    @Inject
    EntityManager em;

    @Override
    public ExerciseSet findExerciseSetById(Long exerciseSetId) {
        ExerciseSetEntity exerciseSetEntity = em.find(ExerciseSetEntity.class, exerciseSetId);
        return exerciseSetEntity == null ? null : toDomain(exerciseSetEntity);
    }

    @Override
    public List<ExerciseSet> loadExerciseSets(ExerciseSetFilter filter, int page, int size) {
        return buildQuery(filter, ExerciseSetEntity.class, false)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(JPAExerciseSetMapper::toDomain)
                .toList();
    }

    @Override
    public Long countExerciseSets(ExerciseSetFilter filter){
        return buildQuery(filter, Long.class, true).getSingleResult();
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

    private <T> TypedQuery<T> buildQuery(ExerciseSetFilter filter, Class<T> resultClass, boolean isCount) {
        String selectPart = isCount ? "SELECT COUNT(e) " : "SELECT e ";
        StringBuilder queryString = new StringBuilder(selectPart);
        queryString.append("FROM ExerciseSetEntity e WHERE 1=1 ");

        if (filter.exerciseId() != null) queryString.append("AND e.exercise.id = :exerciseId ");
        if(filter.repsGreaterThan() != null) queryString.append("AND e.reps > :repsGreaterThan ");
        if(filter.repsLessThan() != null) queryString.append("AND e.reps < :repsLessThan ");
        if(filter.weightGreaterThan() != null) queryString.append("AND e.weightInKg > :weightGreaterThan ");
        if(filter.weightLessThan() != null) queryString.append("AND e.weightInKg < :weightLessThan ");
        if(filter.durationGreaterThan() != null) queryString.append("AND e.durationInSec > :durationGreaterThan ");
        if(filter.durationLessThan() != null) queryString.append("AND e.durationInSec < :durationLessThan ");
        if (filter.createdAfter() != null) queryString.append("AND e.createdAt >= :createdAfter ");
        if (filter.createdBefore() != null) queryString.append("AND e.createdAt <= :createdBefore ");

        TypedQuery<T> query = em.createQuery(queryString.toString(), resultClass);

        if (filter.exerciseId() != null) query.setParameter("exerciseId", filter.exerciseId());
        if(filter.repsGreaterThan() != null) query.setParameter("repsGreaterThan", filter.repsGreaterThan());
        if(filter.repsLessThan() != null) query.setParameter("repsLessThan", filter.repsLessThan());
        if(filter.weightGreaterThan() != null) query.setParameter("weightGreaterThan", filter.weightGreaterThan());
        if(filter.weightLessThan() != null) query.setParameter("weightLessThan", filter.weightLessThan());
        if(filter.durationGreaterThan() != null) query.setParameter("durationGreaterThan", filter.durationGreaterThan());
        if(filter.durationLessThan() != null) query.setParameter("durationLessThan", filter.durationLessThan());
        if (filter.createdAfter() != null) query.setParameter("createdAfter", filter.createdAfter());
        if (filter.createdBefore() != null) query.setParameter("createdBefore", filter.createdBefore());

        return query;
    }
}
