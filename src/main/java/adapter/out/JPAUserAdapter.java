package adapter.out;

import adapter.mapper.UserMapper;
import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.CoachMemberEntity;
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

@ApplicationScoped
//UserAdapter kann über @Inject injiziert werden und durch applicationscoped wird sichergestellt, dass die klasse nur einmal erstellt wird und überall benutzt wird.
public class JPAUserAdapter implements SaveUserPort, FindUserByEmailPort, FindUserByIdPort, UpdateUserPort {
    @PersistenceContext //entitymanager wird automatisch gefüllt
    EntityManager em;

    //Used a static import on UserMapper.toEntity() -> toEntity()
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
                .getSingleResultOrNull();
        if(entity == null){
            return null;
        }
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


    //For Mapping from Domain <-> Persistence

    //Every Adapter looks up and finds the Entity by id. This is done by the adapter and not the Mapper
    private UserEntity toEntity(User user) {
        if (user instanceof Member m) {
            return toEntityMember(m);
        }

        if (user instanceof Coach c) {
            return toEntityCoach(c);
        }

        throw new IllegalArgumentException("Unknown user type");
    }

    private MemberEntity toEntityMember(Member member) {
        MemberEntity memberEntity = UserMapper.toEntity(member);
        for (Long coachIds : member.getCoaches()) {
            CoachEntity coachEntity = em.find(CoachEntity.class, coachIds);
            CoachMemberEntity coachMemberEntity = new CoachMemberEntity();
            coachMemberEntity.setMember(memberEntity);
            coachMemberEntity.setCoach(coachEntity);
            coachEntity.getAssignments().add(coachMemberEntity);
            memberEntity.getAssignments().add(coachMemberEntity);
        }
        return memberEntity;
    }

    private CoachEntity toEntityCoach(Coach coach) {
        CoachEntity coachEntity = UserMapper.toEntity(coach);
        for (Long client : coach.getClients()) {
            MemberEntity memberEntity = em.find(MemberEntity.class, client);
            CoachMemberEntity coachMemberEntity = new CoachMemberEntity();
            coachMemberEntity.setMember(memberEntity);
            coachMemberEntity.setCoach(coachEntity);
            coachEntity.getAssignments().add(coachMemberEntity);
            memberEntity.getAssignments().add(coachMemberEntity);
        }
        return coachEntity;
    }

    private User toDomain(UserEntity entity) {
        if (entity instanceof MemberEntity memberEntity) {
            return UserMapper.toDomain(memberEntity);
        }
        return UserMapper.toDomain((CoachEntity) entity);
    }
}
