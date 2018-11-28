package playground.jpadal;

import org.springframework.data.repository.CrudRepository;

import playground.logic.Entities.ActivityEntity;

public interface ActivityDao extends CrudRepository<ActivityEntity, String>{

}
