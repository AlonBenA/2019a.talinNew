package playground.jpadal;

import org.springframework.data.repository.CrudRepository;
import playground.logic.Entities.GeneratedNumber;


public interface NumbersDao extends CrudRepository<GeneratedNumber, Long>{

}

