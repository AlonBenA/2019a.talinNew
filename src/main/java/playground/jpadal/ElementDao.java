package playground.jpadal;

import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Date;
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
	
	
	public Page<ElementEntity> findAllByExirationDateIsNullOrExirationDateAfter(
			@Param("today") Date today,
			Pageable pageable);
	
	
	public Page<ElementEntity> findAllByExirationDateIsNullAndXLessThanAndXGreaterThanAndYLessThanAndYGreaterThanOrExirationDateAfterAndXLessThanAndXGreaterThanAndYLessThanAndYGreaterThan(
			@Param("upperX") Double upperX,
			@Param("lowerX") Double lowerX, 
			@Param("upperY") Double upperY,
			@Param("lowerY") Double lowerY,
			@Param("today") Date today,
			@Param("upperX2") Double upperX2,
			@Param("lowerX2") Double lowerX2, 
			@Param("upperY2") Double upperY2,
			@Param("lowerY2") Double lowerY2, 
			Pageable pageable);

	public Page<ElementEntity> findAllByNameLike(
			@Param("value") String value, 
			Pageable pageable);
	
	public Page<ElementEntity> findAllByTypeLike(
			@Param("value") String value, 
			Pageable pageable);


	public Page<ElementEntity> findAllByExirationDateIsNullAndNameLike(
			@Param("value") String value,
			Pageable pageable);
	
	public Page<ElementEntity> findAllByExirationDateAfterAndNameLike(
			@Param("today") Date today,
			@Param("value") String value,
			Pageable pageable);
	
	public Page<ElementEntity> findAllByExirationDateIsNullAndTypeLike(
			@Param("value") String value,
			Pageable pageable);
	
	public Page<ElementEntity> findAllByExirationDateAfterAndTypeLike(
			@Param("today") Date today,
			@Param("value") String value,
			Pageable pageable);
}


