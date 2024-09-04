package fr.projet.diginamic.backend.dtos;

import java.util.Date;

import fr.projet.diginamic.backend.enums.StatusEnum;
import fr.projet.diginamic.backend.enums.TransportEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * Data Transfer Object (DTO) representing a mission to be displayed.
 * This DTO is used for transferring mission data between layers of the application.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor

@ToString
public class DisplayedMissionDTO {

     /** The unique identifier of the mission */
    private Long id;

    /** The label or name of the mission */
    private String label;

     /** The total price of the mission, calculated based on its duration and daily rate */
    private Double totalPrice = 0.0;

    /** The current status of the mission */
    private StatusEnum status;

     /** The start date of the mission */
    private Date startDate;

    /** The end date of the mission */
    private Date endDate;

    /** The transport mode used for the mission */
    private TransportEnum transport;

    /** The city from which the mission starts */
    private String departureCity;

    /** The city at which the mission ends */
    private String arrivalCity;

    /** The date when the bounty is assigned, if applicable */
    private Date bountyDate;

    /** The amount of the bounty for the mission, if applicable */
    private Double bountyAmount = 0.0;

    /** The label of the nature of the mission */
    private String labelNatureMission;

    /** The unique identifier of the user assigned to the mission */
    private Long userId;

    /** The unique identifier of the nature of the mission */
    private Long natureMissionId;

    /** The unique identifier of the associated expense */
    private ExpenseWithLinesDto expense;
}

