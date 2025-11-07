package adapter.out;

import adapter.mapper.UserMapper;
import adapter.out.Entities.UserEntity;
import application.port.out.UserPort;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped //UserAdapter kann über @Inject injiziert werden und durch applicationscoped wird sichergestellt, dass die klasse nur einmal erstellt wird und überall benutzt wird.
public class UserAdapter implements UserPort {

    @PersistenceContext //entitymanager wird automatisch gefüllt
    EntityManager em;


    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        em.persist(entity);
        return UserMapper.toDomain(entity);
    }

    @Override
    public User findByEmail(String email) {
        UserEntity entity = em.find(UserEntity.class, email);
        return UserMapper.toDomain(entity);
    }
}
