package adapter.mapper;

import adapter.out.Entities.UserEntity;
import domain.model.User;

public class UserMapper {
    public static User toDomain(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getFirstName(), userEntity.getLastName(),userEntity.getEmail(), userEntity.getPassword(),  userEntity.getGender(), userEntity.getBornOn(), userEntity.getCreatedAt());
    }

public static UserEntity toEntity(User user) {
        return new UserEntity(user.getFirstName(), user.getLastName(),  user.getEmail(), user.getPassword(), user.getGender(), user.getBornOn(), user.getCreatedAt());
    }
}
