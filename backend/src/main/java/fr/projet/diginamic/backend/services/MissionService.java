package fr.projet.diginamic.backend.services;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.projet.diginamic.backend.dtos.BountyReportDTO;
import fr.projet.diginamic.backend.dtos.CreateMissionDTO;
import fr.projet.diginamic.backend.dtos.DisplayedMissionDTO;
import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.enums.StatusEnum;
import fr.projet.diginamic.backend.enums.TransportEnum;
import fr.projet.diginamic.backend.mappers.MissionMapper;
import fr.projet.diginamic.backend.repositories.MissionRepository;
import fr.projet.diginamic.backend.specs.MissionSpecifications;
import fr.projet.diginamic.backend.utils.CalculateMissionPricing;
import jakarta.persistence.EntityNotFoundException;
//TODO: reimplement logic of isManager boolean
/**
 * Service class for managing mission entities.
 * Provides methods to perform CRUD operations on mission entities.
 */
@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    UserService userService;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    NatureMissionService natureMissionService;

    @Autowired
    MissionMapper missionMapper;

    @Autowired
    CalculateMissionPricing calculateMissionPricing;

    /**
     * Save a mission entity.
     * 
     * @param mission the mission to save.
     * @return the saved mission entity.
     */
    public Mission createMission(Mission mission) {
        mission.setStatus(StatusEnum.INITIAL);
        validateMission(mission, true);
        return missionRepository.save(mission);
    }

    public DisplayedMissionDTO createMission(CreateMissionDTO dto) {
        Mission bean = missionMapper.fromMissionFormToBean(dto);
        bean.setStatus(StatusEnum.INITIAL);
        validateMission(bean, true);
        Mission newMissionBean = missionRepository.save(bean);
        return missionMapper.fromBeantoDisplayedMissionDTO(newMissionBean);
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
        boolean isManager = false;
        Mission oldMission = findOneMission(mission.getId());
        // boolean isManager =
        // SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        // .contains(new SimpleGrantedAuthority("ROLE_MANAGER"));

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

        if(!isNew){
            // Check valid status for managers and non-managers
            if (isManager) {
                // TODO: /!\ fix logic: check old status and compare it with new one /!\
                // + convert string into enum if necessary (create util for it)
                // Check status is either INITIAL or REJECTED for new or modified missions :
                if (oldMission.getStatus() != StatusEnum.WAITING) {
                    throw new IllegalArgumentException("Current status of mission doesn't allow updates by manager.");
                }
            } else {
                if (!(oldMission.getStatus() == StatusEnum.INITIAL || oldMission.getStatus() == StatusEnum.REJECTED)) {
                    throw new IllegalArgumentException("Current status of mission doesn't allow updates by employee.");
                }
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
     * Retrieve a single mission by its ID.
     * 
     * @param id the ID of the mission to retrieve.
     * @return the found mission entity.
     * @throws EntityNotFoundException if the mission is not found.
     */
    public DisplayedMissionDTO findOneMissionDto(Long id) {
        return missionRepository.findById(id)
                .map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m))
                .orElseThrow(() -> new EntityNotFoundException("Mission not found with ID: " + id));
    }

    /**
     * Retrieve all missions.
     * 
     * @return a list of missions.
     */
    public Page<DisplayedMissionDTO> findAllMissions(Pageable pageable) {
        return missionRepository.findAll(pageable).map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m));
    }

    public List<DisplayedMissionDTO> findAllMissions() {
        return missionRepository.findAll().stream().map(missionMapper::fromBeantoDisplayedMissionDTO).toList();
    }

    /**
     * Retrieve all missions that match a given specification.
     * 
     * @param spec     the specification for filtering missions.
     * @param pageable the pagination information.
     * @return a page of missions that match the specification.
     */
    @Transactional(readOnly = true)
    public Page<DisplayedMissionDTO> findAllMissionsWithSpecsForAdmin(String status, String nature, String labelOrUsername,
            Pageable pageable) {

        Specification<Mission> spec = MissionSpecifications.createSpecificationForAdmin(status, nature, labelOrUsername);
               
        return missionRepository.findAll(spec, pageable).map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m));
    }

    /**
     * Retrieve all missions for the connected user that match a given specification.
     * 
     * @param spec     the specification for filtering missions.
     * @param pageable the pagination information.
     * @return a page of missions that match the specification.
     */
    @Transactional(readOnly = true)
    public Page<DisplayedMissionDTO> findAllMissionsWithSpecsForCurrentUser(String status, String nature, String label,
            Pageable pageable){
               long id = 1;
                Specification<Mission> spec = createSpecificationForEmployee(status, nature, label);

                try{
                    return missionRepository.findByUserId(id, spec, pageable).map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m));
                    
                } catch(EntityNotFoundException e){
                    throw new EntityNotFoundException("User not found with id " + id);
                }
    }

    /**
     * Retrieve all missions of collaborators under supervision of a given manager and that match a given specification.
     * 
     * @param managerId     the manager's id for filtering employees missions who are under his/her supervision.
     * @param spec     the specification for filtering missions.
     * @param pageable the pagination information.
     * @return a page of missions that match the specification.
     */
    @Transactional(readOnly = true)
    public Page<DisplayedMissionDTO> findAllMissionsWithSpecsByManagerId(Long managerId, String status, String nature, String label,
            Pageable pageable){
              
                Specification<Mission> spec = createSpecificationForManager(managerId, status, nature, label);

                try{
                    return missionRepository.findAll(spec, pageable).map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m));
                    
                } catch(EntityNotFoundException e){
                    throw new EntityNotFoundException("Manager not found with id " + managerId);
                }
    }

    public UserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
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
    private Specification<Mission> createSpecificationForManager(Long managerId, String status, String nature, String labelOrUsername) {
        return MissionSpecifications.filterMissionsByCriteriaForManager(managerId, status, nature, labelOrUsername);
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
     * @param id                the ID of the mission to retrieve and update.
     * @param updatedMissionDTO the updated mission data.
     * @return the updated mission DTO.
     * @throws EntityNotFoundException if the mission is not found.
     */
    @Transactional
    public DisplayedMissionDTO updateMission(Long id, DisplayedMissionDTO updatedMission) {
        Mission mission = missionMapper.displayedMissionDTOToBean(updatedMission);
        validateMission(mission, false);
        missionRepository.save(mission);
        return missionMapper.fromBeantoDisplayedMissionDTO(mission);
    }
    /**
     * Delete a mission by its ID.
     * 
     * @param id the ID of the mission to delete.
     * @throws EntityNotFoundException if the mission is not found.
     */
    @Transactional
    public void deleteMission(Long id) {
        Mission mission = missionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Mission not found with ID: " + id));
   
        if(mission.getStatus() == StatusEnum.FINISHED){
            throw new IllegalArgumentException("Cannot delete a mission that is already finished."); 
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
    public DisplayedMissionDTO updateMissionStatus(Long id, String status) {
        if(status == null || status.isEmpty()){
            throw new IllegalArgumentException("Status value cannot be null"); 
        }
        StatusEnum statusEnum;
        try {
            statusEnum = StatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value " + status);
        }
        return missionRepository.findById(id)
                .map(m -> {
                    // TODO: logic problem : need validation after update too: to check if status valid.
                    validateMission(m, false);
                    m.setStatus(statusEnum);
                    Mission bean = missionRepository.save(m);
                    return missionMapper.fromBeantoDisplayedMissionDTO(bean);
                })
                .orElseThrow(() -> new EntityNotFoundException("Mission not found with ID: " + id));
    }

    
    @Transactional(readOnly = true)
    public BountyReportDTO getBountiesReportForUser(Long userId){

        if (userId == null) {
            throw new IllegalArgumentException("Invalid userId");
        }

        List<Mission> userMissions = missionRepository.findByUserId(userId);

         List<DisplayedMissionDTO> currentYearMissionsWithBounties = userMissions.stream()
            .filter(this::isBountyDateInCurrentYear)
            .filter(m -> m.getBountyAmount() > 0.0)
            .map(bean -> missionMapper.fromBeantoDisplayedMissionDTO(bean))
            .collect(Collectors.toList());
        
        long totalNumberOfBounties = currentYearMissionsWithBounties.stream().filter(m -> m.getBountyAmount() > 0.0).count();

        double totalAmountOfBounties = currentYearMissionsWithBounties.stream().mapToDouble(DisplayedMissionDTO::getBountyAmount).sum();

        double highestBountyAmount = currentYearMissionsWithBounties.stream()
            .mapToDouble(DisplayedMissionDTO::getBountyAmount)
            .max()
            .orElse(0.0);

        // Get a map of <month, totalBounty> for months with bounties only: 
        Map<String, Double> totalBountiesPerMonth = calculateMissionPricing.summarizeBountiesPerMonth(currentYearMissionsWithBounties);

        List<String> listMonths = Arrays.asList(
                "JANVIER", "FÉVRIER", "MARS", "AVRIL", "MAI", "JUIN",
                "JUILLET", "AOÛT", "SEPTEMBRE", "OCTOBRE", "NOVEMBRE", "DÉCEMBRE"
        );

        for (String month : listMonths) {
            totalBountiesPerMonth.putIfAbsent(month, 0.0);
        }

        return new BountyReportDTO(totalNumberOfBounties,highestBountyAmount, totalAmountOfBounties, totalBountiesPerMonth,currentYearMissionsWithBounties);   
    }

    public boolean isBountyDateInCurrentYear (Mission mission){
        
        if (mission.getBountyDate() == null) {
            return false;
        }
        String currentYear = new SimpleDateFormat("YYYY", Locale.FRENCH).format(new Date());
        String bountyYearDate = new SimpleDateFormat("YYYY", Locale.FRENCH).format(mission.getBountyDate());
        return currentYear.equals(bountyYearDate);
    }
}