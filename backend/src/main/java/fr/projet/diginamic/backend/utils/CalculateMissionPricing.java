package fr.projet.diginamic.backend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import fr.projet.diginamic.backend.dtos.DisplayedMissionDTO;
import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.enums.StatusEnum;

@Service
public class CalculateMissionPricing {

        public void calculatePricing(Mission mission) {

        if (mission.getNatureMission() != null) {
            long duration = getDifferenceDays(mission.getStartDate(), mission.getEndDate()); 
            double dailyRate = mission.getNatureMission().getAdr();
            double totalPrice = duration * dailyRate;
            mission.setTotalPrice(totalPrice);

            if (mission.getStatus() == StatusEnum.FINISHED && mission.getNatureMission().getIsEligibleToBounty()) {
                double bountyRate = mission.getNatureMission().getBountyRate() / 100.0;
                double bountyAmount = totalPrice * bountyRate;
                mission.setBountyAmount(bountyAmount);
                mission.setBountyDate(mission.getEndDate());
            }
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;// Include start day
    }

    /**
     * Summarizes bounties by month from a list of mission DTOs.
     * 
     * @param missions List of DisplayedMissionDTO
     * @return A map with month as key and sum of bounties as value.
     */
    public Map<String, Double> summarizeBountiesPerMonth(List<DisplayedMissionDTO> missions) {
        Map<String, Double> monthSum = new HashMap<>();
        for (DisplayedMissionDTO mission : missions) {
            String monthKey = new SimpleDateFormat("MMMM", Locale.FRENCH).format(mission.getStartDate()).toUpperCase();
            if (monthSum.containsKey(monthKey)){
                Double bountiesSum = monthSum.get(monthKey) + mission.getBountyAmount() + 1;
                monthSum.put(monthKey, bountiesSum);       
            } else {
                monthSum.put(monthKey, mission.getBountyAmount());
            }
        }
        return monthSum;
    }
    
}
