package adapter.out;

import adapter.mapper.JPAUserMapper;
import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.MemberEntity;
import adapter.out.Entities.UserEntity;
import application.port.out.UserPorts.*;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import static adapter.mapper.JPAUserMapper.toDomain;

@ApplicationScoped
//UserAdapter kann über @Inject injiziert werden und durch applicationscoped wird sichergestellt, dass die klasse nur einmal erstellt wird und überall benutzt wird.
public class JPAUserAdapter implements SaveUserPort, FindUserByEmailPort, FindUserByIdPort, UpdateUserPort {
    @PersistenceContext //entitymanager wird automatisch gefüllt
    EntityManager em;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = toEntity(user);
        em.persist(entity);
        return toDomain(entity);
    }

    @Override
    public User findByEmail(String email) {
        UserEntity entity = em.createQuery("select u from UserEntity u where u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getSingleResult();
        return toDomain(entity);
    }

    @Override
    public User findUserById(Long id) {
        return toDomain(em.find(UserEntity.class, id));
    }

    @Transactional
    @Override
    public void update(User user) {
        if(user.getId() == null) {
            throw new IllegalArgumentException("id of updated user is null");
        }
        em.merge(toEntity(user));
    }

    private UserEntity toEntity(User user) {
        if(user instanceof Member member){
            MemberEntity memberEntity = JPAUserMapper.toEntity(member);
            MemberEntity inDB = user.getId() == null ? null : em.find(MemberEntity.class, user.getId());
            memberEntity.setExercises(inDB == null ? new  ArrayList<>() : inDB.getExercises());
            memberEntity.setWorkouts(inDB == null ? new  ArrayList<>() : inDB.getWorkouts());
            memberEntity.setAssignments(inDB == null ? new  ArrayList<>() : inDB.getAssignments());
            return memberEntity;
        }
        CoachEntity coachEntity = JPAUserMapper.toEntity((Coach) user);
        CoachEntity inDB = user.getId() == null ? null : em.find(CoachEntity.class, user.getId());
        coachEntity.setExercises(inDB == null ? new  ArrayList<>() : inDB.getExercises());
        coachEntity.setWorkouts(inDB == null ? new  ArrayList<>() : inDB.getWorkouts());
        coachEntity.setAssignments(inDB == null ? new  ArrayList<>() : inDB.getAssignments());
        return coachEntity;
    }
}