package fr.projet.diginamic.backend.entities;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class representing the nature of a mission
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public class NatureMission {

    /** The id of the nature of the mission */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The label of the nature of the mission */
    @Column(name = "label")
    private String label;

    /** The ceiling TJM of the nature of the mission */
    @Column(name = "Adr")

    private Double adr = 0.0;


    /** The billing status of the nature of the mission */
    @Column(name = "is_billed")
    private Boolean isBilled;

    /** The start date of the nature of the mission */
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    /** The end date of the nature of the mission */
    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    /** The bonus percentage of the nature of the mission */
    @Column(name = "Bounty_Rate")

    private Double bountyRate = 0.0;

    @Column(name = "is_eligible_to_bounty")
    private Boolean isEligibleToBounty;

    /** The missions associated with this nature */
    @OneToMany(mappedBy = "natureMission")
    private Set<Mission> missions;

    public NatureMission(Long id, String label, Double adr, Boolean isBilled, Date startDate, Date endDate, Double bountyRate, Boolean isEligibleToBounty) {
        this.id = id;
        this.label = label;
        this.adr = adr;
        this.isBilled = isBilled;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bountyRate = bountyRate;
        this.isEligibleToBounty = isEligibleToBounty;
        
    }
}