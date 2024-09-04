package fr.projet.diginamic.backend.services;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.projet.diginamic.backend.dtos.NatureMissionDTO;
import fr.projet.diginamic.backend.entities.NatureMission;
import fr.projet.diginamic.backend.repositories.NatureMissionRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Service class for managing NatureMission entities.
 */
@Service
public class NatureMissionService {

    

    @Autowired
    private NatureMissionRepository natureMissionRepository;

    /**
     * Get a list of all NatureMissions.
     *
     * @return a list of NatureMissionDTO.
     */
    public List<NatureMissionDTO> getAllNatureMissions() {
        List<NatureMission> natureMissions = natureMissionRepository.findAll();
        return natureMissions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a NatureMission by ID.
     *
     * @param id the ID of the NatureMission.
     * @return an Optional of NatureMissionDTO.
     */
    public Optional<NatureMissionDTO> getNatureMissionById(Long id) {
        Optional<NatureMission> natureMission = natureMissionRepository.findById(id);
        return natureMission.map(this::convertToDTO);
    }

    /**
     * Get a NatureMission by ID.
     *
     * @param id the ID of the NatureMission.
     * @return an Optional of NatureMissionDTO.
     */
    public NatureMission getNatureMissionBeanById(Long id) {
        return natureMissionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("NatureMission not found with id " + id));
    }

    /**
 * Get the ID of a NatureMission by its label.
 *
 * @param label the label of the NatureMission.
 * @return the ID of the NatureMission.
 */
    @Transactional(readOnly = true)
    public Long getIdByLabel(String label) {
    NatureMission natureMission = natureMissionRepository.findByLabel(label)
        .orElseThrow(() -> new RuntimeException("NatureMission not found"));
    return natureMission.getId();
    }


    

    



    /**
     * Create a new NatureMission.
     *
     * @param natureMissionDTO the NatureMissionDTO to create.
     * @return the created NatureMissionDTO.
     */
    public NatureMissionDTO createNatureMission(NatureMissionDTO natureMissionDTO) {
        // Check for unique label
        System.out.println(natureMissionDTO);
        if (natureMissionRepository.existsByLabelAndEndDateIsNull(natureMissionDTO.getLabel())) {
            throw new RuntimeException("A nature with the same label is already active.");

        }

        // Set start date to today
        natureMissionDTO.setStartDate(new Date());

        NatureMission natureMission = convertToEntity(natureMissionDTO);
        NatureMission savedNatureMission = natureMissionRepository.save(natureMission);
        return convertToDTO(savedNatureMission);
    }

    



    /**
     * Update an existing NatureMission.
     *
     * @param id the ID of the NatureMission to update.
     * @param natureMissionDTO the updated NatureMissionDTO.
     * @return the updated NatureMissionDTO.
     */
    public NatureMissionDTO updateNatureMission(Long id, NatureMissionDTO natureMissionDTO) {
        NatureMission natureMission = natureMissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NatureMission not found"));

        // Check if the nature is used in missions
        if (natureMission.getMissions().isEmpty()) {
            // Update current nature directly
            natureMission.setLabel(natureMissionDTO.getLabel());
            natureMission.setAdr(natureMissionDTO.getAdr());
            natureMission.setIsBilled(natureMissionDTO.getIsBilled());
            natureMission.setStartDate(natureMissionDTO.getStartDate());
            natureMission.setEndDate(natureMissionDTO.getEndDate());
            natureMission.setBountyRate(natureMissionDTO.getBountyRate());
            natureMission.setIsEligibleToBounty(natureMissionDTO.getIsEligibleToBounty());

            NatureMission updatedNatureMission = natureMissionRepository.save(natureMission);
            return convertToDTO(updatedNatureMission);
        }
        // Set end date for current nature
        natureMission.setEndDate(new Date());

        // Create new nature with updated values and start date tomorrow
        NatureMissionDTO newNatureMissionDTO = new NatureMissionDTO();
        newNatureMissionDTO.setLabel(natureMissionDTO.getLabel());
        newNatureMissionDTO.setAdr(natureMissionDTO.getAdr());
        newNatureMissionDTO.setIsBilled(natureMissionDTO.getIsBilled());

        Date tomorrow = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        newNatureMissionDTO.setStartDate(tomorrow);
        newNatureMissionDTO.setEndDate(natureMissionDTO.getEndDate());
        newNatureMissionDTO.setBountyRate(natureMissionDTO.getBountyRate());
        newNatureMissionDTO.setIsEligibleToBounty(natureMissionDTO.getIsEligibleToBounty());

        return createNatureMission(newNatureMissionDTO);
    }

    /**
     * Delete a NatureMission by ID.
     *
     * @param id the ID of the NatureMission to delete.
     * @return a boolean indicating success or failure of deletion.
     */
    @Transactional
    public boolean deleteNatureMission(Long id) {
        
        NatureMission natureMission = natureMissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NatureMission not found"));

        if (natureMission.getMissions().isEmpty()) {
            // Physical deletion if not used
            natureMissionRepository.deleteById(id);
        } else {
            // Logical deletion if used
            natureMission.setEndDate(new Date());
            natureMissionRepository.save(natureMission);
        }
        return true;
    }

    // Helper methods to convert between entity and DTO
    private NatureMissionDTO convertToDTO(NatureMission entity) {
        if (entity == null) return null;
        
        return new NatureMissionDTO(
                
                entity.getLabel(),
                entity.getAdr(),
                entity.getIsBilled(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getBountyRate(),
                entity.getIsEligibleToBounty()
        );
    }
    
    private NatureMission convertToEntity(NatureMissionDTO dto) {
        if (dto == null) return null;
        
        NatureMission natureMission = new NatureMission();
        // Id peut être null lors de la création
        natureMission.setLabel(dto.getLabel());
        natureMission.setAdr(dto.getAdr());
        natureMission.setIsBilled(dto.getIsBilled());
        natureMission.setStartDate(dto.getStartDate());
        natureMission.setEndDate(dto.getEndDate());
        natureMission.setBountyRate(dto.getBountyRate());
        natureMission.setIsEligibleToBounty(dto.getIsEligibleToBounty());
        natureMission.setMissions(new HashSet<>()); // Gérer les missions si nécessaire
    
        return natureMission;
    }
    
}