package adapter.out;

import adapter.out.Entities.AssignedWorkoutEntity;
import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.MemberEntity;
import adapter.out.Entities.WorkoutEntity;
import application.port.out.AssignedWorkoutPorts.CreateAssignedWorkoutPort;
import application.port.out.AssignedWorkoutPorts.GetAssignedWorkoutsPort;
import domain.model.AssignedWorkout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class JPAAssignedWorkoutAdapter implements GetAssignedWorkoutsPort, CreateAssignedWorkoutPort {

    @Inject
    EntityManager em;

    @Override
    public List<AssignedWorkout> getAssignedWorkouts(Long memberId, Long coachId) {
        String sql = "SELECT a FROM AssignedWorkoutEntity a WHERE a.member.id=:memberId";
        if (coachId != null) {
            sql += " AND a.coach.id=:coachId";
        }
        TypedQuery<AssignedWorkoutEntity> query = em.createQuery(sql, AssignedWorkoutEntity.class);
        query.setParameter("memberId", memberId);
        if (coachId != null) {
            query.setParameter("coachId", coachId);
        }
        return query.getResultList().stream().map(entity -> new AssignedWorkout(entity.getId(), entity.getWorkout().getId(), entity.getMember().getId(), entity.getCoach().getId(), entity.getAssignedAt())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssignedWorkout createAssignedWorkout(AssignedWorkout assignedWorkout) {
        AssignedWorkoutEntity toPersist = new AssignedWorkoutEntity();
        var c = em.find(CoachEntity.class, assignedWorkout.getCoachId());
        var m = em.find(MemberEntity.class, assignedWorkout.getMemberId());
        var wo = em.find(WorkoutEntity.class, assignedWorkout.getWorkoutId());
        toPersist.setCoach(c);
        toPersist.setMember(m);
        toPersist.setWorkout(wo);
        em.persist(toPersist);
        return new AssignedWorkout(toPersist.getId(), toPersist.getWorkout().getId(), toPersist.getMember().getId(), toPersist.getCoach().getId(), toPersist.getAssignedAt());
    }
}
