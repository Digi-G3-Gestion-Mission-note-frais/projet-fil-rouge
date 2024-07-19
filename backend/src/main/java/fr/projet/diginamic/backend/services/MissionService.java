package fr.projet.diginamic.backend.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.entities.UserEntity;
import fr.projet.diginamic.backend.enums.StatusEnum;
import fr.projet.diginamic.backend.enums.TransportEnum;
import fr.projet.diginamic.backend.repositories.MissionRepository;
import fr.projet.diginamic.backend.specs.MissionSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;

/**
 * Service class for managing mission entities.
 * Provides methods to perform CRUD operations on mission entities.
 */
@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    /**
     * Save a mission entity.
     * 
     * @param mission the mission to save.
     * @return the saved mission entity.
     */
    public Mission createMission(Mission mission) {
        validateMission(mission, true);
        return missionRepository.save(mission);
    }

    /**
     * Validates mission data before saving.
     * 
     * @param mission the mission to validate
     * @throws IllegalArgumentException if validation fails
     * @return the mission if no exception were raised
     */
    private void validateMission(Mission mission, boolean isNew) {
        Date today = new Date();
        boolean isManager = true;
//        boolean isManager = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
//                .contains(new SimpleGrantedAuthority("ROLE_MANAGER"));

        // Check if the mission starts in the past or today
        if (!mission.getStartDate().after(today)) {
            if (isNew) {
                throw new IllegalArgumentException("Mission cannot start today or in the past.");
            } else {
                throw new IllegalArgumentException(
                        "Modifications to missions scheduled to start today are not allowed.");
            }
        }

        // Check for 7-day advance booking for flights
        if (mission.getTransport() == TransportEnum.AIRPLANE) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            if (mission.getStartDate().before(cal.getTime())) {
                throw new IllegalArgumentException("Flights must be booked at least 7 days in advance.");
            }
        }
        // Check valid status for managers and non-managers
        if (isManager) {
            // Check status is either INITIAL or REJECTED for new or modified missions :
            if (mission.getStatus() != StatusEnum.IN_PROGRESS) {
                throw new IllegalArgumentException("Invalid status for operation by manager.");
            }
        } else {
            if (!(mission.getStatus() == StatusEnum.INITIAL || mission.getStatus() == StatusEnum.REJECTED)) {
                throw new IllegalArgumentException("Invalid status for operation by employees.");
            }
        }

        // Check valid nature of mission
        if (mission.getNatureMission().getEndDate() != null && mission.getNatureMission().getEndDate().before(today)) {
            throw new IllegalArgumentException("The nature of the mission is no longer valid.");
        }

        // Ensure start date is before end date
        if (mission.getEndDate().before(mission.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
    }

    /**
     * Retrieve a single mission by its ID.
     * 
     * @param id the ID of the mission to retrieve.
     * @return the found mission entity.
     * @throws EntityNotFoundException if the mission is not found.
     */
    public Mission findOneMission(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found with ID: " + id));
    }

    /**
     * Retrieve all missions.
     * 
     * @return a list of missions.
     */
    public Page<Mission> findAllMissions(Pageable pageable) {
        return missionRepository.findAll(pageable);
    }

    /**
     * Retrieve all missions that match a given specification.
     * 
     * @param spec     the specification for filtering missions.
     * @param pageable the pagination information.
     * @return a page of missions that match the specification.
     */
    @Transactional(readOnly = true)
    public Page<Mission> findAllMissionsWithSpecs(String status, String nature, String labelOrUsername,
            Pageable pageable) {

        boolean isManager = true;
//        boolean isManager = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
//                .contains(new SimpleGrantedAuthority("ROLE_MANAGER"));

        Specification<Mission> spec = isManager ? createSpecificationForManager(status, nature, labelOrUsername)
                : createSpecificationForEmployee(status, nature, labelOrUsername);
        return missionRepository.findAll(spec, pageable);
    }

    /**
     * Creates a Specification object for filtering missions based on criteria
     * applicable to managers.
     * This method constructs a dynamic query specification that managers can use to
     * filter missions
     * based on the provided status, nature of the mission, and a label or username.
     * Managers have
     * the ability to search missions not only based on mission details but also
     * involving user-related
     * information, providing broader search capabilities.
     *
     * @param status          The status of the missions to be filtered. Can be null
     *                        if status filtering is not required.
     * @param nature          The nature of the missions to be filtered. Can be null
     *                        if nature filtering is not required.
     * @param labelOrUsername The label of the mission or the username of the
     *                        associated user to filter by.
     *                        This allows managers to perform searches across
     *                        mission labels or user names.
     *                        Can be null if this type of filtering is not required.
     * @return A Specification object that can be used to perform the query with the
     *         specified criteria.
     */
    private Specification<Mission> createSpecificationForManager(String status, String nature, String labelOrUsername) {
        return MissionSpecifications.filterMissionsByCriteriaForManager(status, nature, labelOrUsername);
    }

    /**
     * Creates a Specification object for filtering missions based on criteria
     * applicable to employees.
     * This method constructs a dynamic query specification that employees can use
     * to filter missions
     * based on the provided status, nature of the mission, and mission label.
     * Employees are limited
     * to filtering missions based on their own missions, and as such, their filter
     * criteria exclude user-related
     * information beyond their own scope.
     *
     * @param status The status of the missions to be filtered. Can be null if
     *               status filtering is not required.
     * @param nature The nature of the missions to be filtered. Can be null if
     *               nature filtering is not required.
     * @param label  The label of the mission to filter by. This allows employees to
     *               search for specific
     *               missions by label. Can be null if label filtering is not
     *               required.
     * @return A Specification object that can be used to perform the query with the
     *         specified criteria.
     */
    private Specification<Mission> createSpecificationForEmployee(String status, String nature, String label) {
        return MissionSpecifications.filterMissionsByCriteriaForEmployee(status, nature, label);
    }

    /**
     * Retrieve a single mission by its ID and update it.
     * 
     * @param id             the ID of the mission to retrieve and update.
     * @param updatedMission the updated mission data.
     * @return the updated mission entity.
     * @throws EntityNotFoundException if the mission is not found.
     */
    @Transactional
    public Mission updateMission(Long id, Mission updatedMission) {
        validateMission(updatedMission, false);
        return missionRepository.findById(id).map(mission -> {
            mission.setStatus(StatusEnum.INITIAL);
            mission.setLabel(updatedMission.getLabel());
            mission.setDailyRate(updatedMission.getDailyRate());
            mission.setStartDate(updatedMission.getStartDate());
            mission.setEndDate(updatedMission.getEndDate());
            mission.setTransport(updatedMission.getTransport());
            mission.setDepartureCity(updatedMission.getDepartureCity());
            mission.setArrivalCity(updatedMission.getArrivalCity());
            mission.setBonusDate(updatedMission.getBonusDate());
            mission.setBonusAmount(updatedMission.getBonusAmount());
            mission.setUser(updatedMission.getUser());
            mission.setNatureMission(updatedMission.getNatureMission());
            mission.setExpense(updatedMission.getExpense());
            return missionRepository.save(mission);
        }).orElseThrow(() -> new EntityNotFoundException("Mission not found with ID: " + id));
    }

    /**
     * Delete a mission by its ID.
     * 
     * @param id the ID of the mission to delete.
     * @throws EntityNotFoundException if the mission is not found.
     */
    @Transactional
    public void deleteMission(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new EntityNotFoundException("Mission not found with ID: " + id);
        }
        missionRepository.deleteById(id);
    }

    /**
     * Update a mission by its ID.
     * 
     * @param id the ID of the mission to update.
     * @throws EntityNotFoundException if the mission is not found.
     */
    @Transactional
    public Object updateMissionStatus(Long id, StatusEnum status) {
        return missionRepository.findById(id)
                .map(m -> {
                    validateMission(m, false);
                    m.setStatus(status);
                    return missionRepository.save(m);
                })
                .orElseThrow(() -> new EntityNotFoundException("Mission not found with ID: " + id));

    }
}