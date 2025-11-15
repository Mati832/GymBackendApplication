package adapter.out;

import adapter.mapper.UserMapper;
import adapter.out.Entities.UserEntity;
import application.port.out.FindUserByEmailPort;
import application.port.out.SaveUserPort;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
//UserAdapter kann über @Inject injiziert werden und durch applicationscoped wird sichergestellt, dass die klasse nur einmal erstellt wird und überall benutzt wird.
public class UserAdapter implements SaveUserPort, FindUserByEmailPort {

    @PersistenceContext //entitymanager wird automatisch gefüllt
    EntityManager em;


    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        em.persist(entity);
        return UserMapper.toDomain(entity);
    }

    @Override
    public User findByEmail(String email) {
        UserEntity entity = em.createQuery("select u from UserEntity u where u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getSingleResult();
        return UserMapper.toDomain(entity);
    }
}
