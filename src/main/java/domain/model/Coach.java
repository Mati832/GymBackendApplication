package domain.model;
import domain.valueobject.Gender;
import java.time.LocalDateTime;
import java.util.List;

public class Coach extends User {
    List<Long> clients;

    public Coach(Long id, String firstName, String lastName, String email,
                 String password, Gender gender, LocalDateTime bornOn, LocalDateTime createdAt, List<Long> clients) {
        super(id, firstName, lastName, email, password, gender, bornOn, createdAt);
        this.clients = clients;
    }

    public List<Long> getClients() {
        return clients;
    }

    public void setClients(List<Long> clients) {
        this.clients = clients;
    }
}