package application.service.exerciseSet;

import application.commands.exerciseSet.ExerciseSetFilter;
import application.port.in.exerciseSet.*;
import application.port.out.ExercisePorts.FindExerciseByIdPort;
import application.port.out.ExercisePorts.LoadExerciseByIdPort;
import application.port.out.ExercisePorts.UpdateExercisePort;
import application.port.out.ExerciseSetPorts.*;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.exceptions.ExerciseSetNotFoundException;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ExerciseSetService implements LoadExerciseSetByIdUseCase, LoadExerciseSetsUseCase, AddExerciseSetToExerciseUseCase, DeleteExerciseSetInExerciseUseCase,
        EditExerciseSetUseCase {

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
    @Inject
    LoadExerciseSetsPort  loadExerciseSetsPort;
    @Inject
    CountExerciseSetsPort countExerciseSetsPort;
    @Inject
    LoadExerciseByIdPort  loadExerciseByIdPort;

    @Override
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> loadExerciseSetById(Long exerciseSetId){
        if(exerciseSetId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        ExerciseSet exerciseSet = findExerciseSetByIdPort.findExerciseSetById(exerciseSetId);
        if(exerciseSet == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);

        return new JPAWorkoutExerciseAdapterResult.Success<>(exerciseSet);
    }

    @Override
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> loadExerciseSets(ExerciseSetFilter filter, int page, int size){
        if(filter == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        if(filter.exerciseId() != null && findExerciseByIdPort.findExerciseById(filter.exerciseId()) == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        List<ExerciseSet> loaded = loadExerciseSetsPort.loadExerciseSets(filter, page, size);
        int totalPageCount =(int) Math.ceil((double) countExerciseSetsPort.countExerciseSets(filter)/size);

        return new JPAWorkoutExerciseAdapterResult.Paginated<>(loaded, page, size, totalPageCount);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> addExerciseSetToExercise(Long userId, Long exerciseId, ExerciseSet exerciseSet) {
        if(userId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);
        if(exerciseId == null || exerciseSet == null){
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        }

        if(!exerciseId.equals(exerciseSet.getBelongsToExercise()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        Exercise exercise = findExerciseByIdPort.findExerciseById(exerciseId);
        if(exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        if(!exercise.getCreatedByUserId().equals(userId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);

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
        Exercise updatedExercise = updateExercisePort.updateExercise(exercise);

        if(updatedExercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        return new JPAWorkoutExerciseAdapterResult.Created<>(persisted);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<Void> deleteExerciseSetInExercise(Long userId, Long exerciseId, Long exerciseSetId) {
        if(userId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);
        if(exerciseId == null || exerciseSetId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        Exercise exercise = findExerciseByIdPort.findExerciseById(exerciseId);
        if(exercise == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        if(!exercise.getExerciseSets().contains(exerciseSetId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_IN_EXERCISE_NOT_FOUND);

        if(!exercise.getCreatedByUserId().equals(userId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);

        if(findExerciseSetByIdPort.findExerciseSetById(exerciseSetId) == null)
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

        return new JPAWorkoutExerciseAdapterResult.Deleted<>(true);
    }

    @Override
    @Transactional
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> editExerciseSet(Long userId, Long exerciseSetId, ExerciseSet exerciseSet) {
        if(userId == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);

        if(exerciseSetId == null || exerciseSet == null ||  exerciseSet.getId() == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        if(!exerciseSetId.equals(exerciseSet.getId()))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        ExerciseSet toBeEdited = findExerciseSetByIdPort.findExerciseSetById(exerciseSetId);
        if(toBeEdited == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);
        if(!loadExerciseByIdPort.loadExerciseById(toBeEdited.getBelongsToExercise()).getCreatedByUserId().equals(userId))
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);

        if(exerciseSet.getReps() != null) toBeEdited.setReps(exerciseSet.getReps());
        if(exerciseSet.getWeightInKg() != null) toBeEdited.setWeightInKg(exerciseSet.getWeightInKg());
        if(exerciseSet.getNotes() != null) toBeEdited.setNotes(exerciseSet.getNotes());
        if(exerciseSet.getDurationInSec() != null) toBeEdited.setDurationInSec(exerciseSet.getDurationInSec());
        if(exerciseSet.getCreatedAt() != null) toBeEdited.setCreatedAt(exerciseSet.getCreatedAt());

        ExerciseSet updated = updateExerciseSetPort.updateExerciseSet(toBeEdited);
        if (updated == null)
            return new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);

        return new  JPAWorkoutExerciseAdapterResult.Updated<>(updated);
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
