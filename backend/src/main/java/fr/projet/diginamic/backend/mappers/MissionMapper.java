package fr.projet.diginamic.backend.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.diginamic.backend.dtos.CreateMissionDTO;
import fr.projet.diginamic.backend.dtos.DisplayedMissionDTO;
import fr.projet.diginamic.backend.entities.Expense;
import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.entities.NatureMission;
import fr.projet.diginamic.backend.entities.UserEntity;
import fr.projet.diginamic.backend.enums.StatusEnum;
import fr.projet.diginamic.backend.services.ExpenseService;
import fr.projet.diginamic.backend.services.MissionService;
import fr.projet.diginamic.backend.services.NatureMissionService;
import fr.projet.diginamic.backend.services.UserService;
import fr.projet.diginamic.backend.utils.CalculateMissionPricing;

@Service
public class MissionMapper {

    @Autowired
    NatureMissionService natureMissionService;

    @Autowired
    UserService userService;

    @Autowired
    CalculateMissionPricing calculateMissionPricing;
    
    @Autowired
    ExpenseService expenseService;

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
        dto.setExpenseId(mission.getExpense() != null ? mission.getExpense().getId() : null);

        // Calculate the total price based on the daily rate and duration
        long duration = calculateMissionPricing.getDifferenceDays(mission.getStartDate(), mission.getEndDate()) ; 
        if (mission.getNatureMission().getIsBilled()) {
            double dailyRate = mission.getNatureMission().getAdr();
            dto.setTotalPrice(duration * dailyRate);
        } else {
            dto.setTotalPrice(0.0);
        }

        // Set bounty amount if the mission is completed and eligible for a bounty
        // TODO: handle bounty date : ask the group if can remove it
        if (mission.getStatus() == StatusEnum.FINISHED && mission.getNatureMission().getIsEligibleToBounty()) {
            double bountyRate = mission.getNatureMission().getBountyRate() / 100.0;
            dto.setBountyAmount(dto.getTotalPrice() * bountyRate);
            dto.setBountyDate(mission.getEndDate()); // Bounty date set to the end date of the mission?
        } else {
            dto.setBountyAmount(0.0);
        }
        dto.setLabelNatureMission(mission.getNatureMission().getLabel());
        return dto;
    }

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
        // Calculate the total price based on the daily rate and duration
        long duration = calculateMissionPricing.getDifferenceDays(dto.getStartDate(), dto.getEndDate()); 
        if (natureMisison.getIsBilled()) {
            double dailyRate = natureMisison.getAdr();
            mission.setTotalPrice(duration * dailyRate);
        } else {
            mission.setTotalPrice(0.0);
        }

        UserEntity user = userService.getOne(dto.getUserId());
        mission.setUser(user);
        return mission;
    }

      public Mission displayedMissionDTOToBean(DisplayedMissionDTO dto) {
        MissionService missionService = new MissionService();

        Mission mission = missionService.findOneMission(dto.getId());
        mission.setLabel(dto.getLabel());
        mission.setStatus(dto.getStatus());
        mission.setStartDate(dto.getStartDate());
        mission.setEndDate(dto.getEndDate());
        mission.setTransport(dto.getTransport());
        mission.setDepartureCity(dto.getDepartureCity());
        mission.setArrivalCity(dto.getArrivalCity());

        UserEntity user = userService.getOne(dto.getUserId());
        NatureMission natureMisison = natureMissionService.getNatureMissionBeanById(dto.getNatureMissionId());

        Expense expense = dto.getExpenseId() != null ? expenseService.getExpenseBean(dto.getExpenseId()) : null;
        mission.setUser(user);
        mission.setNatureMission(natureMisison);
        mission.setExpense(expense);

        calculateMissionPricing.calculatePricing(mission);

        return mission;
    }

}
