package playground.jpadal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import playground.logic.Entities.ActivityEntity;

public interface ActivityDao extends CrudRepository<ActivityEntity, String> {
 
	public Page<ActivityEntity> findAllByTypeLikeAndElementIdLikeAndElementPlaygroundLike(
			@Param("type") String type,
			@Param("elementId") String elementId,
			@Param("elementPlayground") String elementPlayground,
			Pageable pageable);
}


