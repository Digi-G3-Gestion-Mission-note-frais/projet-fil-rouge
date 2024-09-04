package fr.projet.diginamic.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for providing a comprehensive report on mission bounties.
 * This DTO aggregates various statistics and details about the bounties of missions within a specified period.
 * Utilizes Lombok annotations to reduce boilerplate code for getters and setters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BountyReportDTO {
    /** The total number of bounties awarded */
    private long totalNumberOfBounties;

    /** The highest single bounty amount awarded */
    private double highestBountyAmount;

    /** The total amount of all bounties awarded */
    private double totalAmountOfBounties;

    /** A map of total bounties per month, with the month as the key and the total bounty amount as the value */
    private Map<String, Double> totalBountiesPerMonth;

    /** A list of missions that have awarded bounties */
    private List<DisplayedMissionDTO> missionsWithBounties;
}
