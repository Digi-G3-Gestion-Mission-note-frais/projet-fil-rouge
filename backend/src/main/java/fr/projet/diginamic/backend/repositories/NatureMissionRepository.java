package fr.projet.diginamic.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.projet.diginamic.backend.entities.NatureMission;
import jakarta.transaction.Transactional;

/**
 * Repository interface for NatureMission entities.
 */
public interface NatureMissionRepository extends JpaRepository<NatureMission, Integer> {

    /**
     * Find NatureMissions by their label.
     *
     * @param label the label of the NatureMission.
     * @return a list of NatureMissions with the given label.
     */
    //List<NatureMission> findByLabel(String label);

    /**
     * Find a NatureMission by its ID.
     *
     * @param id the ID of the NatureMission.
     * @return an Optional containing the NatureMission if found, or empty if not found.
     */
    Optional<NatureMission> findById(Long id);

    /**
     * Delete a NatureMission by its ID.
     *
     * @param id the ID of the NatureMission to delete.
     */
    @Transactional
    void deleteById(Long id);

    /**
     * Check if a NatureMission exists by its ID.
     *
     * @param id the ID of the NatureMission.
     * @return true if a NatureMission with the given ID exists, false otherwise.
     */
    boolean existsById(Long id);

    /**
     * Check if an active NatureMission exists by its label.
     * An active NatureMission is one with a null end date.
     *
     * @param label the label of the NatureMission.
     * @return true if an active NatureMission with the given label exists, false otherwise.
     */
    boolean existsByLabelAndEndDateIsNull(String label);
    Optional<NatureMission> findByLabel(String label);

}

