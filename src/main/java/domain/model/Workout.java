package domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Workout {
    long id;
    String name;
    String description;
    User createdBy;
    LocalDateTime createdAt;
    List<Long> exercises;
}
