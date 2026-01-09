package adapter.out.Entities;

import domain.valueobject.Gender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MemberEntity extends UserEntity {

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CoachMemberEntity> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignedWorkoutEntity> assignedWorkouts = new ArrayList<>();

    //Constructors
    public MemberEntity() {
    }

    public MemberEntity(Long id, String firstName, String lastName, String email, String password,
                        Gender gender, LocalDate bornOn, LocalDateTime createdAt) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
    }

    public MemberEntity(Long id, String firstName, String lastName, String email,
                        String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt, List<CoachMemberEntity> assignments) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
        this.assignments = assignments;
    }

    //getter and setter


    public void setAssignments(List<CoachMemberEntity> assignments) {
        this.assignments = assignments;
    }

    public List<CoachMemberEntity> getAssignments() {
        return this.assignments;
    }
}
