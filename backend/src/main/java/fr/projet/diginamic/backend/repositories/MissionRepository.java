package fr.projet.diginamic.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import fr.projet.diginamic.backend.entities.Mission;

/**
 * Repository interface for {@link Mission} entities, providing methods to
 * perform operations on the database. It extends JpaRepository for basic CRUD
 * operations and JpaSpecificationExecutor for dynamic query construction based
 * on specifications.
 */
@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, JpaSpecificationExecutor<Mission> {

	/**
	 * Find a mission by its ID.
	 * 
	 * @param id the ID of the mission.
	 * @return an Optional containing the mission if found, or an empty Optional if
	 *         not found.
	 */
	Optional<Mission> findById(Long id);

	/**
	 * Deletes a mission by its ID.
	 * 
	 * @param id the ID of the mission to delete.
	 */
	void deleteById(Long id);

	/**
	 * Checks if a mission exists by its ID.
	 * 
	 * @param id the ID of the mission.
	 * @return true if the mission exists, false otherwise.
	 */
	boolean existsById(Long id);

	/**
	 * Finds all missions that match a given specification with pagination.
	 * 
	 * @param spec     the specification for filtering missions.
	 * @param pageable the pagination information.
	 * @return a page of missions that match the specification and pagination
	 *         criteria.
	 */
	Page<Mission> findAll(Specification<Mission> spec, Pageable pageable);

	/**
	 * Retrieves all missions with pagination.
	 * 
	 * This method provides a way to retrieve all missions from the database while
	 * also supporting pagination. Pagination is crucial for efficiently handling
	 * large datasets by breaking the result set into manageable chunks or "pages".
	 *
	 * @param pageable the pagination and sorting information.
	 * @return a {@link Page} object containing a page of missions along with
	 *         pagination metadata, such as total number of pages, total number of
	 *         elements, and so on.
	 */
	Page<Mission> findAll(Pageable pageable);

}
