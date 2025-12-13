package adapter.in.DTOs.ResponseDTOs;

import java.time.LocalDate;

//dto so no personal information like passwords are responded to client and links can be added
public class CoachMemberResponse {
    Long id;
    Long coachId;
    Long memberId;
    LocalDate assignedAt;


    public CoachMemberResponse(Long id, Long coachId, Long memberId, LocalDate assignedAt) {
        this.id = id;
        this.coachId = coachId;
        this.memberId = memberId;
        this.assignedAt = assignedAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
