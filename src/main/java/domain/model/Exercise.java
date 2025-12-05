package domain.model;

import java.util.List;

public class Exercise {
    long id;
    String name;
    //maybe enum later
    String type;
    long durationInSec;
    Long createdBy;
    List<Long> exerciseSets;
}
