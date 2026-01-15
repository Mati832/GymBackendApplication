package adapter.out;

import adapter.out.Entities.AssignedWorkoutEntity;
import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.MemberEntity;
import adapter.out.Entities.WorkoutEntity;
import application.port.out.AssignedWorkoutPorts.CreateAssignedWorkoutPort;
import application.port.out.AssignedWorkoutPorts.GetAssignedWorkoutsPort;
import domain.dbResults.PagedResult;
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
    public PagedResult<AssignedWorkout> getAssignedWorkouts(Long memberId, Long coachId, String search, int offset, int size) {
        String dataSql = "SELECT a FROM AssignedWorkoutEntity a WHERE a.member.id=:memberId";
        String countSql = "SELECT COUNT(a) FROM AssignedWorkoutEntity a WHERE a.member.id = :memberId";
        String searchSql = " AND ( LOWER( a.workout.description ) LIKE LOWER(CONCAT('%', :search, '%'))" +
                " OR LOWER (a.workout.name) LIKE LOWER(CONCAT('%', :search, '%'))" +
                " OR LOWER (a.coach.lastName) LIKE LOWER(CONCAT('%', :search, '%')))";

        if (coachId != null) {
            dataSql += " AND a.coach.id=:coachId";
            countSql += " AND a.coach.id = :coachId";
        }
        if (search != null && !search.isEmpty()) {
            dataSql += searchSql;
            countSql += searchSql;
        }
        TypedQuery<AssignedWorkoutEntity> query = em.createQuery(dataSql, AssignedWorkoutEntity.class);
        query.setParameter("memberId", memberId);
        if (coachId != null) {
            query.setParameter("coachId", coachId);
        }
        if (search != null && !search.isEmpty()) {
            query.setParameter("search", search);
        }

        query.setFirstResult(offset);
        query.setMaxResults(size);

        List<AssignedWorkout> workouts = query.getResultList().stream().map(entity -> new AssignedWorkout(entity.getId(),
                entity.getWorkout().getId(),
                entity.getMember().getId(),
                entity.getCoach().getId(),
                entity.getAssignedAt(),
                entity.getEtag())).collect(Collectors.toList());

        TypedQuery<Long> countQuery = em.createQuery(countSql, Long.class);
        countQuery.setParameter("memberId", memberId);
        if (coachId != null) countQuery.setParameter("coachId", coachId);
        if (search != null && !search.isEmpty()) countQuery.setParameter("search", search);

        long totalCount = countQuery.getSingleResult();

        return new PagedResult<AssignedWorkout>(workouts, totalCount, offset, size);
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
        return new AssignedWorkout(toPersist.getId(), toPersist.getWorkout().getId(), toPersist.getMember().getId(), toPersist.getCoach().getId(), toPersist.getAssignedAt(), toPersist.getEtag());
    }
}
