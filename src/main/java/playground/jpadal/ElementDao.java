package playground.jpadal;

import org.springframework.data.repository.CrudRepository;

import playground.logic.Entities.ElementEntity;

public interface ElementDao extends CrudRepository<ElementEntity, String> {

}


