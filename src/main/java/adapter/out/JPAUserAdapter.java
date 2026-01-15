package adapter.out;

import adapter.mapper.JPAUserMapper;
import adapter.out.Entities.*;
import application.commands.UserFilter;
import application.port.out.UserPorts.*;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static adapter.mapper.JPAUserMapper.toDomain;

@ApplicationScoped
//UserAdapter kann über @Inject injiziert werden und durch applicationscoped wird sichergestellt, dass die klasse nur einmal erstellt wird und überall benutzt wird.
public class JPAUserAdapter implements SaveUserPort, FindUserByEmailPort, LoadUserByIdPort, LoadUsersPort, CountUsersPort, FindUserByIdPort, UpdateUserPort {
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
        try {
            UserEntity entity = em.createQuery("select u from UserEntity u where u.email = :email", UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return toDomain(entity);
        }
        catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public User loadUser(Long id) {
        UserEntity entity = em.find(UserEntity.class, id);
        if (entity == null) return null;
        return slimMapper(entity);
    }

    @Override
    public List<User> loadUsers(UserFilter filter, int page, int size) {
        return buildQuery(filter, UserEntity.class, false)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::slimMapper)
                .toList();
    }

    @Override
    public int countUsers(UserFilter filter, int page, int size){
        return buildQuery(filter, int.class, true).getSingleResult();
    }

    @Override
    public User findUserById(Long id) {
        UserEntity userEntity = em.find(UserEntity.class, id);
        return userEntity == null ? null : toDomain(em.find(UserEntity.class, id));
    }

    @Transactional
    @Override
    public User update(User user) {
        if(user.getId() == null) {
            throw new IllegalArgumentException("id of updated user is null");
        }
        UserEntity userEntity = em.find(UserEntity.class, user.getId());
        userEntity.setId(user.getId());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setGender(user.getGender());
        userEntity.setBornOn(user.getBornOn());
        userEntity.setCreatedAt(user.getCreatedAt());
        userEntity.getExercises().clear();
        userEntity.getExercises().addAll(user.getExercises().stream().map(eId -> em.find(ExerciseEntity.class, eId)).toList());
        userEntity.getWorkouts().clear();
        userEntity.getWorkouts().addAll(user.getWorkouts().stream().map(wId -> em.find(WorkoutEntity.class, wId)).toList());

        return toDomain(userEntity);
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

//A mapper only used for loading user entities without any relation. For performance and pagination
    private User slimMapper(UserEntity entity) {
        if(entity instanceof MemberEntity)
            return new Member(
                    entity.getId(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getEmail(),
                    entity.getPassword(),
                    entity.getGender(),
                    entity.getBornOn(),
                    entity.getCreatedAt(),
                    entity.getEtag()
            );
        else
            return new Coach(
                    entity.getId(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getEmail(),
                    entity.getPassword(),
                    entity.getGender(),
                    entity.getBornOn(),
                    entity.getCreatedAt(),
                    entity.getEtag()
            );
    }

    private <T> TypedQuery<T> buildQuery(UserFilter filter, Class<T> resultClass, boolean isCount) {
        String selectPart = isCount ? "SELECT COUNT(u) " : "SELECT u ";
        StringBuilder queryString = new StringBuilder(selectPart);
        queryString.append("FROM UserEntity u WHERE 1=1 ");

        if(filter.firstName() != null) queryString.append("AND lower(u.firstName) LIKE lower(:firstName) ");
        if(filter.lastName() != null) queryString.append("AND lower(u.lastName) LIKE lower(:lastName) ");
        if(filter.email() != null) queryString.append("AND lower(u.email) LIKE lower(:email) ");
        if(filter.gender() != null) queryString.append("AND u.gender = :gender ");
        if(filter.minAge() != null) queryString.append("AND u.bornOn <= :minAgeDate ");
        if(filter.maxAge() != null) queryString.append("AND u.bornOn >= :maxAgeDate ");


        TypedQuery<T> query = em.createQuery(queryString.toString(), resultClass);

        if(filter.firstName() != null) query.setParameter("firstName", "%" + filter.firstName() + "%");
        if(filter.lastName() != null) query.setParameter("lastName", "%" + filter.lastName() + "%");
        if(filter.email() != null) query.setParameter("email", "%" +  filter.email() + "%");
        if(filter.gender() != null) query.setParameter("gender", filter.gender());
        if(filter.minAge() != null) query.setParameter("minAge", LocalDate.now().minusYears(filter.minAge()));
        if(filter.maxAge() != null) query.setParameter("maxAge", LocalDate.now().minusYears(filter.maxAge()));

        return query;
    }
}