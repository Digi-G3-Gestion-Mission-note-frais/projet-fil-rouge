package fr.projet.diginamic.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BountyReportDTO {
    private long totalNumberOfBounties;
    private double highestBountyAmount;
    private double totalAmountOfBounties;
    private Map<String, Double> totalBountiesPerMonth;
    private List<DisplayedMissionDTO> missionsWithBounties;
}

