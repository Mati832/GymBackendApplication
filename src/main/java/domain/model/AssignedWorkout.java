package domain.model;

import java.time.LocalDateTime;

public class AssignedWorkout {

    private Long id;
    private Long workoutId;
    private Long memberId;
    private Long coachId;
    private LocalDateTime assignedAt;

    public AssignedWorkout(Long id, Long workoutId, Long memberId, Long coachId, LocalDateTime assignedAt) {
        this.id = id;
        this.workoutId = workoutId;
        this.memberId = memberId;
        this.coachId = coachId;
        this.assignedAt = assignedAt;
    }

    public AssignedWorkout(Long workoutId, Long memberId, Long coachId) {
        this.workoutId = workoutId;
        this.memberId = memberId;
        this.coachId = coachId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
