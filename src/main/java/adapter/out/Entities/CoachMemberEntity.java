package adapter.out.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "coach_member",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"coach_id", "member_id"})
        }
)
public class CoachMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    CoachEntity coach;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    MemberEntity member;

    LocalDate assignedAt;

    @PrePersist
    protected void onCreate() {
        this.assignedAt = LocalDate.now();
    }

    public CoachMemberEntity() {
    }

    public CoachMemberEntity(CoachEntity coach, MemberEntity member) {
        this.coach = coach;
        this.member = member;
    }

    public CoachMemberEntity(Long id, CoachEntity coach, MemberEntity member, LocalDate assignedAt) {
        this.id = id;
        this.coach = coach;
        this.member = member;
        this.assignedAt = assignedAt;
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
