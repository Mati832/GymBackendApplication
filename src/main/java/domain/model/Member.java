package domain.model;

import domain.valueobject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Member extends User {
    List<Long> coaches;


    //Constructors
    public Member(Long id, String firstName, String lastName, String email, String password,
                  Gender gender, LocalDate bornOn, LocalDateTime createdAt, List<Long> coaches) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
        this.coaches = coaches;
    }

    public Member(String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn){
        super(firstName, lastName, email, password, gender, bornOn);
    }

    public List<Long> getCoaches() {
        return coaches;
    }

    public void setCoaches(List<Long> coaches) {
        this.coaches = coaches;
    }
}
