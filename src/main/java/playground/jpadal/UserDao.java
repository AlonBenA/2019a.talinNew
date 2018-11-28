package playground.jpadal;

import org.springframework.data.repository.CrudRepository;

import playground.logic.Entities.UserEntity;

public interface UserDao extends CrudRepository<UserEntity, String>{

}
