package fr.projet.diginamic.backend.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.diginamic.backend.dtos.CreateMissionDTO;
import fr.projet.diginamic.backend.dtos.DisplayedMissionDTO;
import fr.projet.diginamic.backend.entities.Expense;
import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.entities.NatureMission;
import fr.projet.diginamic.backend.entities.UserEntity;
import fr.projet.diginamic.backend.services.ExpenseService;
import fr.projet.diginamic.backend.services.NatureMissionService;
import fr.projet.diginamic.backend.services.UserService;
import fr.projet.diginamic.backend.utils.ExpenseMapper;

/**
 * Mapper class for converting between Mission entities and their corresponding DTOs.
 * This class handles the transformation logic for creating, updating, and displaying
 * mission data. It also includes calculations for mission pricing and bounties.
 */
@Service
public class MissionMapper {

    @Autowired
    NatureMissionService natureMissionService;

    @Autowired
    UserService userService;
    
    @Autowired
    ExpenseService expenseService;

    @Autowired
    ExpenseMapper expenseMapper;

    /**
     * Converts a Mission entity to a DisplayedMissionDTO.
     * This method includes calculations for total price and bounty amount.
     * 
     * @param mission The Mission entity to convert.
     * @return The corresponding DisplayedMissionDTO.
     */
    public DisplayedMissionDTO fromBeantoDisplayedMissionDTO(Mission mission) {
        DisplayedMissionDTO dto = new DisplayedMissionDTO();
        dto.setId(mission.getId());
        dto.setLabel(mission.getLabel());
        dto.setStatus(mission.getStatus());
        dto.setStartDate(mission.getStartDate());
        dto.setEndDate(mission.getEndDate());
        dto.setTransport(mission.getTransport());
        dto.setDepartureCity(mission.getDepartureCity());
        dto.setArrivalCity(mission.getArrivalCity());
        dto.setUserId(mission.getUser().getId());
        dto.setNatureMissionId(mission.getNatureMission().getId());
        dto.setExpense(mission.getExpense() != null ? expenseMapper.BeanToDtoWithLines(mission.getExpense()) : null);

        dto.setBountyAmount(mission.getBountyAmount());
        dto.setBountyDate(mission.getBountyDate());
        dto.setTotalPrice(mission.getTotalPrice());

        dto.setLabelNatureMission(mission.getNatureMission().getLabel());
        return dto;
    }

    /**
     * Converts a DisplayedMissionDTO to a Mission entity.
     * This method includes fetching related entities and recalculating mission pricing.
     * 
     * @param dto The DisplayedMissionDTO to convert.
     * @return The corresponding Mission entity.
     */
    public Mission fromDisplayedMissionDTOToBean(DisplayedMissionDTO dto) {
        Mission mission = new Mission(dto.getId());
        mission.setLabel(dto.getLabel());
        mission.setStatus(dto.getStatus());
        mission.setStartDate(dto.getStartDate());
        mission.setEndDate(dto.getEndDate());
        mission.setTransport(dto.getTransport());
        mission.setDepartureCity(dto.getDepartureCity());
        mission.setArrivalCity(dto.getArrivalCity());

        UserEntity user = userService.getOne(dto.getUserId());
        NatureMission natureMisison = natureMissionService.getNatureMissionBeanById(dto.getNatureMissionId());

        Expense expense = dto.getExpense() != null ? expenseMapper.dtoWithLinesToBean(dto.getExpense()) : null;
        mission.setUser(user);
        mission.setNatureMission(natureMisison);
        mission.setExpense(expense);

        return mission;
    }

     /**
     * Converts a Mission entity to a CreateMissionDTO.
     * 
     * @param bean The Mission entity to convert.
     * @return The corresponding CreateMissionDTO.
     */
    public CreateMissionDTO fromBeanToMissionForm(Mission bean) {
        CreateMissionDTO dto = new CreateMissionDTO();
        dto.setLabel(bean.getLabel());
        dto.setStatus(bean.getStatus());
        dto.setStartDate(bean.getStartDate());
        dto.setEndDate(bean.getEndDate());
        dto.setTransport(bean.getTransport());
        dto.setDepartureCity(bean.getDepartureCity());
        dto.setArrivalCity(bean.getArrivalCity());
        dto.setUserId(bean.getUser().getId());
        dto.setNatureMissionId(bean.getNatureMission().getId());
        return dto;
    }

    /**
     * Converts a CreateMissionDTO to a Mission entity.
     * 
     * @param dto The CreateMissionDTO to convert.
     * @return The corresponding Mission entity.
     */
    public Mission fromMissionFormToBean(CreateMissionDTO dto) {
        Mission mission = new Mission();
        mission.setLabel(dto.getLabel());
        mission.setStatus(dto.getStatus()); // TODO: double check that it is a StatusEnum ?
        mission.setStartDate(dto.getStartDate());
        mission.setEndDate(dto.getEndDate());
        mission.setTransport(dto.getTransport()); // TODO: double check that it is a TransportEnum ?
        mission.setDepartureCity(dto.getDepartureCity());
        mission.setArrivalCity(dto.getArrivalCity());
        mission.setBountyAmount(0.0);
        mission.setBountyDate(null);
    
        NatureMission natureMisison = natureMissionService.getNatureMissionBeanById(dto.getNatureMissionId());
        mission.setNatureMission(natureMisison);

        UserEntity user = userService.getOne(dto.getUserId());
        mission.setUser(user);
        return mission;
    }


}

