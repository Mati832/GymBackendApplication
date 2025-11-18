package adapter.out;

import adapter.mapper.UserMapper;

import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.MemberEntity;

import static adapter.mapper.UserMapper.toDomain;
import adapter.out.Entities.UserEntity;
import application.port.out.FindUserByEmailPort;
import application.port.out.FindUserByIdPort;
import application.port.out.SaveUserPort;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@ApplicationScoped
//UserAdapter kann über @Inject injiziert werden und durch applicationscoped wird sichergestellt, dass die klasse nur einmal erstellt wird und überall benutzt wird.
public class JPAUserAdapter implements SaveUserPort, FindUserByEmailPort, FindUserByIdPort{
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
                .getSingleResult();
        return toDomain(entity);
    }

    @Override
    public User findUserById(Long id) {
        return toDomain(em.find(UserEntity.class, id));
    }


    //For Mapping from Domain <-> Persistence

    //Every Adapter looks up and finds the Entity by id. This is done by the adapter and not the Mapper
    private UserEntity toEntity(User user) {
        if(user instanceof Member member){
           MemberEntity memberEntity = UserMapper.toEntity(member);
           memberEntity.setCoaches(member.getCoaches().stream().map(id -> em.find(CoachEntity.class, id)).toList());
           return memberEntity;
        }
        Coach coach = (Coach)user;
        CoachEntity coachEntity = UserMapper.toEntity(coach);
        coachEntity.setClients(coach.getClients().stream().map(id -> em.find(MemberEntity.class, id)).toList());
        return coachEntity;
    }

    private User toDomain(UserEntity entity) {
        if(entity instanceof MemberEntity memberEntity){
            return UserMapper.toDomain(memberEntity);
        }
        return UserMapper.toDomain((CoachEntity)entity);
    }
}
