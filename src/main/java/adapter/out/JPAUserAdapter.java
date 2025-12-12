package adapter.out;

import adapter.out.Entities.UserEntity;
import application.port.out.FindUserByEmailPort;
import application.port.out.FindUserByIdPort;
import application.port.out.SaveUserPort;
import application.port.out.UpdateUserPort;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import static adapter.mapper.JPAUserMapper.*;

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

}
