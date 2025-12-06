package adapter.out.Entities;

import adapter.out.Entities.MemberEntity;
import adapter.out.Entities.UserEntity;
import domain.model.Coach;
import domain.valueobject.Gender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
public class CoachEntity extends UserEntity {
    @OneToMany(mappedBy = "coach", cascade=CascadeType.ALL, orphanRemoval = true)
    List<CoachMemberEntity> assignments=new ArrayList<>();

    //Constructors

    public void assignMember(MemberEntity member){
        member.assignCoach(this);
    }

    public CoachEntity(Long id, String firstName, String lastName, String email,
                       String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
    }

    public CoachEntity(Long id, String firstName, String lastName, String email,
                       String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt, List<CoachMemberEntity> assignments) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
        this.assignments = assignments;
    }

    public CoachEntity() {}

    //getter and setter
    public void setAssignments(List<CoachMemberEntity> assignments) {
        this.assignments = assignments;
    }

    public List<CoachMemberEntity> getAssignments() {
        return this.assignments;
    }
}

