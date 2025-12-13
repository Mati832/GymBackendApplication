package domain.model;
import domain.valueobject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Coach extends User {
    List<Long> clients=new ArrayList<>();

    public Coach(Long id, String firstName, String lastName, String email,
                 String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt,
                 List<Long>  exercises, List<Long> workouts, List<Long> clients) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt,  exercises, workouts);
        this.clients = clients;
    }

    public Coach(String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn) {
        super(firstName, lastName, email, password, gender, bornOn);
    }

    public Coach(Long id, String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
    }

    public Coach(String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn, List<Long> clients) {
        super(firstName, lastName, email, password, gender, bornOn);
        this.clients = clients;
    }

    public List<Long> getClients() {
        return clients;
    }

    public void setClients(List<Long> clients) {
        this.clients = clients;
    }
}