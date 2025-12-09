package domain.model;

import java.util.List;

public class Exercise {
    long id;
    String name;
    //maybe enum later
    String type;
    long durationInSec;
    Long createdByUserId;
    List<Long> exerciseSets;

    public Exercise(long id, String name, String type, long durationInSec, Long createdByUserId, List<Long> exerciseSets) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.durationInSec = durationInSec;
        this.createdByUserId = createdByUserId;
        this.exerciseSets = exerciseSets;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(long durationInSec) {
        this.durationInSec = durationInSec;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public List<Long> getExerciseSets() {
        return exerciseSets;
    }

    public void setExerciseSets(List<Long> exerciseSets) {
        this.exerciseSets = exerciseSets;
    }
}
