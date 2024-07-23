package fr.projet.diginamic.backend.controllers.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.diginamic.backend.dtos.DisplayedMissionDTO;
import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.services.CSVGenerationService;
import fr.projet.diginamic.backend.services.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controller class for handling requests for managers' missions endpoint /api/admins/missions. This class handles CRUD operations for missions within the system, permitting the
 * creation, retrieval, update, and deletion of mission entries.
 */
@RestController
@RequestMapping("/api/managers/{id}/missions")
public class MissionControllerManager {

    @Autowired
	private MissionService missionService;

	@Autowired
    private CSVGenerationService csvGenerationService;

    /**
	 * Retrieves a paginated list of missions based on various filtering and sorting
	 * criteria.
	 * This endpoint supports pagination, sorting, and dynamic filtering to provide
	 * a flexible retrieval
	 * of mission data. The method allows filtering by mission status, nature, and a
	 * search term that can
	 * match either the username of the assigned user or the mission label.
	 *
	 * @param page            The page number to retrieve, with a default value of 0
	 *                        if not specified.
	 * @param size            The size of the page to retrieve, with a default value
	 *                        of 10 if not specified.
	 * @param order           The direction of sorting (asc or desc), with 'asc' as
	 *                        the default value.
	 * @param sortField       The field on which to sort the results, with
	 *                        'startDate' as the default field.
	 * @param status          Optional filter to limit results to missions with a
	 *                        specific status.
	 * @param natureMission   Optional filter to limit results to missions of a
	 *                        specific nature.
	 * @param userNameOrLabel Optional search term that matches either the username
	 *                        of the mission assignee or the mission label.
	 * @return A {@link ResponseEntity} object containing a {@link Page} of
	 *         {@link Mission} objects
	 *         that match the specified criteria. The response is always OK (200),
	 *         even if no missions match the filters.
	 */

	 @Operation(
        summary = "Get a paginated list of all missions",
        description = "Retrieve a paginated list of missions based on various filtering and sorting criteria. Supports pagination, sorting, and dynamic filtering."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of missions",
            content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content)
    })

	@GetMapping
	public ResponseEntity<?> getAllMissionsWithSpecsForManagers(
            @PathVariable Long id,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "order", defaultValue = "asc") String order,
			@RequestParam(value = "sort", defaultValue = "startDate") String sortField,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "nature", required = false) String natureMission,
			@RequestParam(value = "searchbar", required = false) String userNameOrLabel) {

        try {
			Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sortField));
			Page<DisplayedMissionDTO> missions = missionService.findAllMissionsWithSpecsByManagerId(id, status, natureMission, userNameOrLabel, pageable);
            return ResponseEntity.ok(missions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred." + e);
        }
	}
    
}
