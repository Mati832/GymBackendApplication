package adapter.out.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class CoachMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    CoachEntity coach;
    @ManyToOne(fetch = FetchType.LAZY)
    MemberEntity member;

    LocalDate assignedAt;

    public CoachMemberEntity() {
    }

    public CoachMemberEntity(CoachEntity coach, MemberEntity member) {
        this.coach = coach;
        this.member = member;
    }

    public CoachEntity getCoach() {
        return coach;
    }

    public void setCoach(CoachEntity coach) {
        this.coach = coach;
    }

    public MemberEntity getMember() {
        return member;
    }

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    public LocalDate getAssignedAt() {
        return assignedAt;
    }

    public void setAssigendAt(LocalDate assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Long getId() {
        return id;
    }
}
