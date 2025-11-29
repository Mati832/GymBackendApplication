package adapter.out.Entities;

import domain.valueobject.Gender;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class MemberEntity extends UserEntity {
    @ManyToMany
    @JoinTable(
            name = "member_coach",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "coach_id")
    )
    List<CoachEntity> coaches;


    //Constructors
    public MemberEntity() {}

    public MemberEntity(Long id, String firstName, String lastName, String email, String password,
                        Gender gender, LocalDate bornOn, LocalDateTime createdAt) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
    }

    public MemberEntity(Long id, String firstName, String lastName, String email,
                        String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt, List<CoachEntity> coaches) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
        this.coaches = coaches;
    }

    //getter and setter

    public void setCoaches(List<CoachEntity> coaches) {
        this.coaches = coaches;
    }

    public List<CoachEntity> getCoaches() {
        return coaches;
    }
}
