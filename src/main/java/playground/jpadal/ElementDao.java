package playground.jpadal;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import playground.logic.Entities.ElementEntity;

//@RepositoryRestResource
public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String> {

	
	public Page<ElementEntity> findAllByXLessThanAndXGreaterThanAndYLessThanAndYGreaterThan(
			@Param("upperX") Double upperX,
			@Param("lowerX") Double lowerX, 
			@Param("upperY") Double upperY,
			@Param("lowerY") Double lowerY, 
			Pageable pageable);

	public Page<ElementEntity> findAllByNameLike(
			@Param("value") String value, 
			Pageable pageable);
	
	public Page<ElementEntity> findAllByTypeLike(
			@Param("value") String value, 
			Pageable pageable);
}


