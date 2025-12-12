package domain.model;

import java.time.LocalDate;

public class CoachMember {
    Long id;
    Long coachId;
    Long memberId;
    LocalDate assignedAt;


    public CoachMember(){}
    public CoachMember(Long coachId, Long memberId) {
        this.coachId = coachId;
        this.memberId = memberId;
    }

    public CoachMember(Long id, Long coachId, Long memberId, LocalDate assignedAt) {
        this.id = id;
        this.coachId = coachId;
        this.memberId = memberId;
        this.assignedAt = assignedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDate getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDate assignedAt) {
        this.assignedAt = assignedAt;
    }
}
