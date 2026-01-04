package application.service.exerciseSet;

import application.port.in.exerciseSet.AddExerciseSetToExerciseUseCase;
import application.port.in.exerciseSet.DeleteExerciseSetInExerciseUseCase;
import application.port.in.exerciseSet.EditExerciseSetUseCase;
import application.port.out.ExercisePorts.FindExerciseByIdPort;
import application.port.out.ExercisePorts.UpdateExercisePort;
import application.port.out.ExerciseSetPorts.DeleteExerciseSetPort;
import application.port.out.ExerciseSetPorts.FindExerciseSetByIdPort;
import application.port.out.ExerciseSetPorts.SaveExerciseSetPort;
import application.port.out.ExerciseSetPorts.UpdateExerciseSetPort;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.exceptions.ExerciseSetNotFoundException;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ExerciseSetService implements AddExerciseSetToExerciseUseCase, DeleteExerciseSetInExerciseUseCase, EditExerciseSetUseCase {

    @Inject
    FindExerciseSetByIdPort findExerciseSetByIdPort;
    @Inject
    SaveExerciseSetPort saveExerciseSetPort;
    @Inject
    UpdateExerciseSetPort updateExerciseSetPort;
    @Inject
    DeleteExerciseSetPort deleteExerciseSetPort;
    @Inject
    FindExerciseByIdPort findExerciseByIdPort;
    @Inject
    UpdateExercisePort updateExercisePort;

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Exercise> addExerciseSetToExercise(Long exerciseId, ExerciseSet exerciseSet) {
        if(exerciseId == null || exerciseSet == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(!exerciseId.equals(exerciseSet.getBelongsToExercise()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        Exercise exercise = findExerciseByIdPort.findExerciseById(exerciseId);
        if(exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        exerciseSet.setBelongsToExercise(exerciseId);
        ExerciseSet persisted;
        if(exerciseSet.getId() == null) persisted = saveExerciseSetPort.saveExerciseSet(exerciseSet);
        else {
            try{
                persisted = copyExerciseSet(exerciseId, exerciseSet.getId());
            }catch (ExerciseSetNotFoundException e){
                return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);
            }
        }

        exercise.getExerciseSets().add(persisted.getId());
        Exercise updated = updateExercisePort.updateExercise(exercise);
        if(updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        return new JPAWorkoutExerciseAdapterResult.Success<>(exercise);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Exercise> deleteExerciseSetInExercise(Long exerciseId, Long exerciseSetId) {
        if(exerciseId == null || exerciseSetId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        Exercise exercise = findExerciseByIdPort.findExerciseById(exerciseId);
        if(exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        if(!exercise.getExerciseSets().contains(exerciseSetId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_IN_EXERCISE_NOT_FOUND);

        if(findExerciseByIdPort.findExerciseById(exerciseId) == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);

        try{
            deleteExerciseSetPort.deleteExerciseSet(exerciseSetId);
        } catch (Exception e) {
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_DELETED);
        }

        exercise.getExerciseSets().remove(exerciseSetId);
        Exercise updated =  updateExercisePort.updateExercise(exercise);
        if (updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        return new JPAWorkoutExerciseAdapterResult.Success<>(updated);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> editExerciseSet(Long exerciseSetId, ExerciseSet exerciseSet) {
        if(exerciseSetId == null || exerciseSet == null ||  exerciseSet.getId() == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(!exerciseSetId.equals(exerciseSet.getId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        ExerciseSet toBeEdited = findExerciseSetByIdPort.findExerciseSetById(exerciseSetId);
        if(toBeEdited == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);

        if(exerciseSet.getReps() != null) toBeEdited.setReps(exerciseSet.getReps());
        if(exerciseSet.getWeightInKg() != null) toBeEdited.setWeightInKg(exerciseSet.getWeightInKg());
        if(exerciseSet.getNotes() != null) toBeEdited.setNotes(exerciseSet.getNotes());
        if(exerciseSet.getDurationInSec() != null) toBeEdited.setDurationInSec(exerciseSet.getDurationInSec());
        if(exerciseSet.getCreatedAt() != null) toBeEdited.setCreatedAt(exerciseSet.getCreatedAt());

        ExerciseSet updated = updateExerciseSetPort.updateExerciseSet(toBeEdited);
        if (updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);

        return new  JPAWorkoutExerciseAdapterResult.Success<>(updated);
    }

    @Transactional
    public ExerciseSet copyExerciseSet(Long exerciseId, Long exerciseSetId) {
        ExerciseSet exerciseSetToCopyFrom = findExerciseSetByIdPort.findExerciseSetById(exerciseSetId);
        if(exerciseSetToCopyFrom == null) throw new ExerciseSetNotFoundException("exerciseSet: " + exerciseSetId + " not found");

        ExerciseSet copyExerciseSet = new ExerciseSet();
        copyExerciseSet.setReps(exerciseSetToCopyFrom.getReps());
        copyExerciseSet.setWeightInKg(exerciseSetToCopyFrom.getWeightInKg());
        copyExerciseSet.setNotes(exerciseSetToCopyFrom.getNotes());
        copyExerciseSet.setDurationInSec(exerciseSetToCopyFrom.getDurationInSec());
        copyExerciseSet.setCreatedAt(exerciseSetToCopyFrom.getCreatedAt());
        copyExerciseSet.setBelongsToExercise(exerciseId);

        return saveExerciseSetPort.saveExerciseSet(copyExerciseSet);
    }
}
