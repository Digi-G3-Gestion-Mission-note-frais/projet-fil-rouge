package fr.projet.diginamic.backend.utils;

import org.springframework.stereotype.Service;

import fr.projet.diginamic.backend.dtos.NatureMissionDTO;
import fr.projet.diginamic.backend.entities.NatureMission;

/** Service to transform NatureMission in Dtos and vice-versa */
@Service
public class NatureMissionMapper {

    /**
     * Method to transform a NatureMissionDto to a NatureMission
     *
     * @param dto the dto to transform
     * @return the natureMission object after the mapping
     */
    public NatureMission dtoToBean(NatureMissionDTO dto) {
        NatureMission entity = new NatureMission();
        entity.setLabel(dto.getLabel());
        entity.setAdr(dto.getAdr()); // Correct method name
        entity.setIsBilled(dto.getIsBilled()); // Correct method name
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setBountyRate(dto.getBountyRate());
        entity.setIsEligibleToBounty(dto.getIsEligibleToBounty()); // Add missing field
        return entity;
    }

    /**
     * Method to transform a NatureMission to a NatureMissionDto
     *
     * @param entity, the bean to transform
     * @return the natureMissionDto object after the mapping
     */
    public NatureMissionDTO beanToDto(NatureMission entity) {
        NatureMissionDTO dto = new NatureMissionDTO();
        dto.setLabel(entity.getLabel());
        dto.setAdr(entity.getAdr()); // Correct method name
        dto.setIsBilled(entity.getIsBilled()); // Correct method name
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setBountyRate(entity.getBountyRate());
        dto.setIsEligibleToBounty(entity.getIsEligibleToBounty()); // Add missing field
        return dto;
    }
}