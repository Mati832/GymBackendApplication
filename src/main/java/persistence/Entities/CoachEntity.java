package persistence.Entities;

import domain.valueobject.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.time.LocalDateTime;
import java.util.List;
@Entity
public class CoachEntity extends UserEntity {
    @ManyToMany(mappedBy = "coaches")
    List<MemberEntity> clients;

    //Constructors


    public CoachEntity(Long id, String firstName, String lastName, String email,
                       String password, Gender gender, LocalDateTime bornOn, LocalDateTime createdAt) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
    }

    public CoachEntity(Long id, String firstName, String lastName, String email,
                       String password, Gender gender, LocalDateTime bornOn, LocalDateTime createdAt, List<MemberEntity> clients) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
        this.clients = clients;
    }

    public CoachEntity() {};

    //getter and setter
    public void setClients(List<MemberEntity> clients) {
        this.clients = clients;
    }

    public List<MemberEntity> getClients() {
        return clients;
    }
}

