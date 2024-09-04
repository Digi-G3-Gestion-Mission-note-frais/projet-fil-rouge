package fr.projet.diginamic.backend.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for NatureMission entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NatureMissionDTO {
    private String label;
    private Double adr;
    private Boolean isBilled;
    private Date startDate;
    private Date endDate;
    private Double bountyRate;
    private Boolean isEligibleToBounty;

    // Assurez-vous que ce constructeur existe


    
    
    // Autres getters et setters ici
}

