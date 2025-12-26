package adapter.out;

import adapter.mapper.JPAExerciseSetMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import application.port.out.UserPorts.AddExerciseSetToExercisePort;
import application.port.out.UserPorts.DeleteExerciseSetInExercisePort;
import application.port.out.UserPorts.EditExerciseSetPort;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import static adapter.mapper.JPAExerciseSetMapper.toDomain;
import static adapter.mapper.JPAExerciseMapper.toDomain;
import static adapter.mapper.JPAExerciseSetMapper.copyFromDB;

@ApplicationScoped
public class JPAExerciseSetAdapter implements AddExerciseSetToExercisePort, DeleteExerciseSetInExercisePort, EditExerciseSetPort {
    @Inject
    EntityManager em;

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Exercise> addExerciseSetToExercise(Long exerciseId, ExerciseSet exerciseSet) {
        if(exerciseId == null || exerciseSet == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(!exerciseId.equals(exerciseSet.getBelongsToExercise()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, exerciseId);
        if(exerciseEntity == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        ExerciseSetEntity exerciseSetEntity;
        if(exerciseSet.getId() != null) exerciseSetEntity = copyFromDB(em.find(ExerciseSetEntity.class, exerciseSet.getId()));
        else exerciseSetEntity = toEntity(exerciseSet);

        exerciseSetEntity.setExercise(exerciseEntity);
        em.persist(exerciseSetEntity);
        exerciseEntity.getExerciseSets().add(exerciseSetEntity);
        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity));
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Exercise> deleteExerciseSetInExercise(Long exerciseId, Long exerciseSetId) {
        if(exerciseId == null || exerciseSetId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, exerciseId);
        if(exerciseEntity == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        ExerciseSetEntity exerciseSetToBeDeleted = exerciseEntity.getExerciseSets().stream().
                filter(eSet -> eSet.getId().equals(exerciseSetId)).findFirst().orElse(null);
        if(exerciseSetToBeDeleted == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_IN_EXERCISE_NOT_FOUND);

        if(!exerciseEntity.getExerciseSets().remove(exerciseSetToBeDeleted))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_DELETED);

        em.remove(exerciseSetToBeDeleted);
        return  new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity));
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> editExerciseSet(Long exerciseSetId, ExerciseSet exerciseSet) {
        if(exerciseSetId == null || exerciseSet == null ||  exerciseSet.getId() == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(!exerciseSetId.equals(exerciseSet.getId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        ExerciseSetEntity eSetToBeEdited = em.find(ExerciseSetEntity.class, exerciseSetId);
        if(eSetToBeEdited == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);


        if(exerciseSet.getReps() != null) eSetToBeEdited.setReps(exerciseSet.getReps());
        if(exerciseSet.getWeightInKg() != null) eSetToBeEdited.setWeightInKg(exerciseSet.getWeightInKg());
        if(exerciseSet.getNotes() != null) eSetToBeEdited.setNotes(exerciseSet.getNotes());
        if(exerciseSet.getDurationInSec() != null) eSetToBeEdited.setDurationInSec(exerciseSet.getDurationInSec());
        if(exerciseSet.getCreatedAt() != null) eSetToBeEdited.setCreatedAt(exerciseSet.getCreatedAt());

        return new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(eSetToBeEdited));
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
