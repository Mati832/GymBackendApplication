package domain.model;

import adapter.out.Entities.CoachMemberEntity;
import domain.valueobject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Member extends User {
    List<Long> coaches=new ArrayList<>();

    //Constructors
    public Member(Long id, String firstName, String lastName, String email,
                  String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt, int etag,
                  List<Long>  exercises, List<Long> workouts, List<Long> coaches) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt, etag, exercises, workouts);
        this.coaches = coaches;
    }

    public Member(String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn){
        super(firstName, lastName, email, password, gender, bornOn);
    }
    public Member(Long id, String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt, int etag) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt, etag);
    }

    public List<Long> getCoaches() {
        return this.coaches;
    }

    public void setCoaches(List<Long> coaches) {
        this.coaches = coaches;
    }
}
