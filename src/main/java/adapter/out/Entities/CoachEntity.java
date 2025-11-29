package adapter.out.Entities;

import adapter.out.Entities.MemberEntity;
import adapter.out.Entities.UserEntity;
import domain.model.Coach;
import domain.valueobject.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
public class CoachEntity extends UserEntity {
    @ManyToMany(mappedBy = "coaches")
    List<MemberEntity> clients;

    //Constructors


    public CoachEntity(Long id, String firstName, String lastName, String email,
                       String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
    }

    public CoachEntity(Long id, String firstName, String lastName, String email,
                       String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt, List<MemberEntity> clients) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
        this.clients = clients;
    }

    public CoachEntity() {}

    //getter and setter
    public void setClients(List<MemberEntity> clients) {
        this.clients = clients;
    }

    public List<MemberEntity> getClients() {
        return clients;
    }
}

