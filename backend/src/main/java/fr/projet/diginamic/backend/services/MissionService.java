package fr.projet.diginamic.backend.services;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.projet.diginamic.backend.dtos.BountyReportDTO;
import fr.projet.diginamic.backend.dtos.CreateMissionDTO;
import fr.projet.diginamic.backend.dtos.DisplayedMissionDTO;
import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.entities.NatureMission;
import fr.projet.diginamic.backend.entities.UserEntity;
import fr.projet.diginamic.backend.enums.StatusEnum;
import fr.projet.diginamic.backend.enums.TransportEnum;
import fr.projet.diginamic.backend.exceptions.MissionServiceException;
import fr.projet.diginamic.backend.mappers.MissionMapper;
import fr.projet.diginamic.backend.repositories.MissionRepository;
import fr.projet.diginamic.backend.repositories.interfaces.UserRepository;
import fr.projet.diginamic.backend.specs.MissionSpecifications;
import fr.projet.diginamic.backend.utils.CalculateMissionPricing;
import jakarta.persistence.EntityNotFoundException;

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
    UserRepository userRepository;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    NatureMissionService natureMissionService;

    @Autowired
    MissionMapper missionMapper;

    @Autowired
    CalculateMissionPricing calculateMissionPricing;

    //---------------------------------- CREATE MISSION  ------------------------------------ 
    /**
     * Save a mission entity.
     * 
     * @param mission the mission to save.
     * @return the saved mission entity.
     */
    @Transactional
    public Mission createMission(Mission mission, String userEmail) {

        UserEntity user = userService.getOneByEmail(userEmail);
        if(user == null){
            throw new EntityNotFoundException("Failed to create mission: User not found with email " + userEmail);
        }
        mission.setUser(user);
        mission.setStatus(StatusEnum.INITIAL);
        CalculateMissionPricing.calculateTotalPrice(mission);
        validateMission(mission, true);
        return missionRepository.save(mission);
    }

    /**
     * Creates a new mission.
     * This method converts a CreateMissionDTO to a Mission entity, sets its initial status,
     * validates the mission, saves it to the repository, and then converts it to a DisplayedMissionDTO.
     *
     * @param dto The CreateMissionDTO object containing the details of the mission to be created.
     * @return A DisplayedMissionDTO object representing the newly created mission.
     * @throws IllegalArgumentException if the mission data is invalid.
     */
    @Transactional
    public DisplayedMissionDTO createMission(CreateMissionDTO dto, String userEmail) {
       
        Mission bean = missionMapper.fromMissionFormToBean(dto);
        UserEntity user = userService.getOneByEmail(userEmail);
        
        if(user == null){
            throw new EntityNotFoundException("Failed to create mission: User not found with email " + userEmail);
  
        }
        bean.setUser(user);
        bean.setStatus(StatusEnum.INITIAL);
        CalculateMissionPricing.calculateTotalPrice(bean);
        validateMission(bean, true);
        Mission newMissionBean = missionRepository.save(bean);
        return missionMapper.fromBeantoDisplayedMissionDTO(newMissionBean);
    }
    
    //---------------------------------- FIND ONE MISSION  ------------------------------------ 
    /**
     * Retrieve a single mission by its ID.
     * 
     * @param id the ID of the mission to retrieve.
     * @return the found mission entity.
     * @throws EntityNotFoundException if the mission is not found.
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public DisplayedMissionDTO findOneMissionDto(Long id) {
        return missionRepository.findById(id)
                .map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m))
                .orElseThrow(() -> new EntityNotFoundException("Mission not found with ID: " + id));
    }

    //---------------------------------- FIND ALL MISSIONS  ------------------------------------ 
    /**
     * Retrieve all missions.
     * 
     * @return a list of missions.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<DisplayedMissionDTO> findAllMissions(Pageable pageable) {
        return missionRepository.findAll(pageable).map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<DisplayedMissionDTO> findAllMissions() {
        return missionRepository.findAll().stream().map(missionMapper::fromBeantoDisplayedMissionDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<DisplayedMissionDTO> findMissionsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        return missionRepository.findByUser_Id(userId).stream().map(missionMapper::fromBeantoDisplayedMissionDTO).toList();
    }

    //------------------------------ FIND ALL MISSIONS FOR ADMIN BY SPECS ------------------------------ 
    /**
     * Retrieve all missions that match a given specification.
     * 
     * @param nature     the specification for filtering missions.
     * @param pageable the pagination information.
     * @return a page of missions that match the specification.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<DisplayedMissionDTO> findAllMissionsWithSpecsForAdmin(String status, String nature, String labelOrUsername,
            Pageable pageable) {

        Specification<Mission> spec = MissionSpecifications.createSpecificationForAdmin(status, nature, labelOrUsername);
               
        return missionRepository.findAll(spec, pageable).map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m));
    }

    //--------------------------- FIND ALL MISSIONS FOR CURRENT USER BY SPECS --------------------------- 
    /**
     * Retrieve all missions for the connected user that match a given specification.
     * 
     * @param spec     the specification for filtering missions.
     * @param pageable the pagination information.
     * @return a page of missions that match the specification.
     */
    @Transactional(readOnly = true)
    public Page<DisplayedMissionDTO> findAllMissionsWithSpecsForCurrentUser(String email, String status, String nature, String label, String withExpense,
            Pageable pageable){

                UserEntity user = userService.getOneByEmail(email);
                if(user == null){
                    throw new EntityNotFoundException("User not found with email " + email);
                }
                Long userId = user.getId();
            
                Specification<Mission> spec = createSpecificationForEmployee(userId, status, nature, label, withExpense);

                try{
                    return missionRepository.findAll(spec, pageable).map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m));
                    
                } catch(Exception e){
                    throw new MissionServiceException("An error occurred while retrieving missions: ", e);   
                }
    }

    //--------------------------- FIND ALL MISSIONS BY MANAGER ID BY SPECS --------------------------- 
    /**
     * Retrieve all missions of collaborators under supervision of a given manager and that match a given specification.
     * 
     * @param email     the manager's email to retrieve the associated user data for filtering employees missions who are under his/her supervision.
     * @param spec     the specification for filtering missions.
     * @param pageable the pagination information.
     * @return a page of missions that match the specification.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Transactional(readOnly = true)
    public Page<DisplayedMissionDTO> findAllMissionsWithSpecsByManagerId(String email, String status, String nature, String label,
            Pageable pageable){

                UserEntity manager = userService.getOneByEmail(email);
                if(manager == null){
                    throw new EntityNotFoundException("User not found with email " + email);
                }
                Long managerId = manager.getId();
              
                Specification<Mission> spec = createSpecificationForManager(managerId, status, nature, label);

                try{
                    return missionRepository.findAll(spec, pageable).map(m -> missionMapper.fromBeantoDisplayedMissionDTO(m));
                    
                } catch(Exception e){
                    throw new MissionServiceException("An error occurred while retrieving missions: ", e);   
                }
    }

    //-------------------------------- UPDATE MISSION --------------------------------  
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
        Mission mission = missionMapper.fromDisplayedMissionDTOToBean(updatedMission);
        
        if (mission.getStatus() == StatusEnum.REJECTED) {
            mission.setStatus(StatusEnum.INITIAL);
        }
        
        CalculateMissionPricing.calculateTotalPrice(mission);
        validateMission(mission, false);
        missionRepository.save(mission);
        return missionMapper.fromBeantoDisplayedMissionDTO(mission);
    }

    //-------------------------------- DELETE MISSION --------------------------------  
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
    //-------------------------------- UPDATE MISSION STATUS --------------------------------  
    /**
     * Update a mission by its ID.
     * 
     * @param id the ID of the mission to update.
     * @throws EntityNotFoundException if the mission is not found.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Transactional
    public DisplayedMissionDTO updateMissionStatus(Long id, String status) {
        if(status == null || status.isEmpty()){
            throw new IllegalArgumentException("Status value cannot be null or empty."); 
        }
        StatusEnum statusEnum;
        try {
            statusEnum = StatusEnum.valueOf(status.toUpperCase());   
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value " + status + " " + e);
        }

        if(statusEnum != StatusEnum.VALIDATED && statusEnum != StatusEnum.REJECTED){
            throw new IllegalArgumentException("Invalid status value " + status);
        }

        return missionRepository.findById(id)
                .map(m -> {
                    validateMission(m, false);
                    m.setStatus(statusEnum);
                    Mission bean = missionRepository.save(m);
                    return missionMapper.fromBeantoDisplayedMissionDTO(bean);
                })
                .orElseThrow(() -> new EntityNotFoundException("Mission not found with ID: " + id));
    }

    //---------------------------------- VALIDATE MISSION  ------------------------------------ 
    /**
     * Validates mission data before saving.
     * 
     * @param mission the mission to validate
     * @param isNew   flag indicating if this is a new mission
     * @throws IllegalArgumentException if validation fails
     */
    private void validateMission(Mission mission, boolean isNew) {
        Date today = new Date();
        boolean isManager = false;
        Mission oldMission = isNew ? null : findOneMission(mission.getId());

        validateStartDate(mission.getStartDate(), today, isNew);
        validateEndDate(mission.getStartDate(), mission.getEndDate());
        validateTransport(mission.getTransport(), mission.getStartDate());
        
        if (!isNew) {
            validateStatusForUpdate(oldMission, isManager);
        }

        validateNatureOfMission(mission.getNatureMission(), today);
    }

    /**
     * Validates the start date of the mission.
     * 
     * @param startDate the start date of the mission
     * @param today     the current date
     * @param isNew     flag indicating if this is a new mission
     * @throws IllegalArgumentException if the start date is invalid
     */
    private void validateStartDate(Date startDate, Date today, boolean isNew) {
        if (!startDate.after(today)) {
            if (isNew) {
                throw new IllegalArgumentException("Mission cannot start today or in the past.");
            } else {
                throw new IllegalArgumentException("Modifications to missions scheduled to start today are not allowed.");
            }
        }
    }

    /**
     * Validates the transport type and booking time for flights.
     * 
     * @param transport the transport type of the mission
     * @param startDate the start date of the mission
     * @throws IllegalArgumentException if the transport type or booking time is invalid
     */
    private void validateTransport(TransportEnum transport, Date startDate) {
        if (transport == TransportEnum.AIRPLANE) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            if (startDate.before(cal.getTime())) {
                throw new IllegalArgumentException("Flights must be booked at least 7 days in advance.");
            }
        }
    }

    /**
     * Validates the status of the mission for updates.
     * 
     * @param oldMission the previous state of the mission
     * @param isManager flag indicating if the user is a manager
     * @throws IllegalArgumentException if the status is invalid for the update
     */
    private void validateStatusForUpdate(Mission oldMission, boolean isManager) {
        if (isManager) {
            if (oldMission.getStatus() != StatusEnum.WAITING) {
                throw new IllegalArgumentException("Current status of mission doesn't allow updates by manager.");
            }
        } else {
            if (!(oldMission.getStatus() == StatusEnum.INITIAL || oldMission.getStatus() == StatusEnum.REJECTED)) {
                throw new IllegalArgumentException("Current status of mission doesn't allow updates by employee.");
            }
        }
    }

    /**
     * Validates the nature of the mission.
     * 
     * @param natureMission the nature of the mission
     * @param today         the current date
     * @throws IllegalArgumentException if the nature of the mission is invalid
     */
    private void validateNatureOfMission(NatureMission natureMission, Date today) {
        if (natureMission.getEndDate() != null && natureMission.getEndDate().before(today)) {
            throw new IllegalArgumentException("The nature of the mission is no longer valid.");
        }
    }

    /**
     * Validates the end date of the mission.
     * 
     * @param startDate the start date of the mission
     * @param endDate   the end date of the mission
     * @throws IllegalArgumentException if the end date is before the start date
     */
    private void validateEndDate(Date startDate, Date endDate) {
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
    }

    //-------------------------------- SPECIFICATIONS --------------------------------
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
    private Specification<Mission> createSpecificationForEmployee(Long userId, String status, String nature, String label, String withExpense) {
        return MissionSpecifications.filterMissionsByCriteriaForEmployee(userId, status, nature, label, withExpense);
    }

    //-------------------------------- BOUNTY REPORT --------------------------------
    
    /**
     * Retrieves a report of bounties for a user for the current year.
     * This method fetches all missions for a given user, filters the missions to include only those with bounties for the current year,
     * and calculates various statistics such as total number of bounties, total amount of bounties, and highest bounty amount.
     *
     * @param userId The ID of the user for whom to retrieve the bounty report.
     * @return A BountyReportDTO object containing the bounty statistics and a list of missions with bounties.
     * @throws IllegalArgumentException if the userId is null.
     */
    @Transactional(readOnly = true)
    public BountyReportDTO getBountiesReportForUser(Long userId){

        if (userId == null) {
            throw new IllegalArgumentException("Invalid userId");
        }

        List<Mission> userMissions = missionRepository.findByUser_Id(userId);

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

    /**
     * Checks if the bounty date of a mission falls within the current year.
     * This method compares the bounty date of a given mission to the current year to determine if it is within the same year.
     *
     * @param mission The mission to check.
     * @return true if the bounty date of the mission is within the current year, false otherwise.
     */
    public boolean isBountyDateInCurrentYear (Mission mission){
        
        if (mission.getBountyDate() == null) {
            return false;
        }
        String currentYear = new SimpleDateFormat("YYYY", Locale.FRENCH).format(new Date());
        String bountyYearDate = new SimpleDateFormat("YYYY", Locale.FRENCH).format(mission.getBountyDate());
        return currentYear.equals(bountyYearDate);
    }
}