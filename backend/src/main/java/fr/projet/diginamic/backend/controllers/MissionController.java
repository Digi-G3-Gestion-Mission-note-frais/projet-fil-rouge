package fr.projet.diginamic.backend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.enums.StatusEnum;
import fr.projet.diginamic.backend.services.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller class for handling requests for the /missions endpoint. This class
 * handles CRUD operations for missions within the system, permitting the
 * creation, retrieval, update, and deletion of mission entries.
 */
@Tag(name = "MissionController", description = "Controller class for handling requests for the /missions endpoint. This class handles CRUD operations for missions within the system, permitting the creation, retrieval, update, and deletion of mission entries.")
@RestController
@RequestMapping("/api/missions")
public class MissionController {

	@Autowired
	private MissionService missionService;

	/**
	 * Create a new mission with detailed validation error response.
	 *
	 * @param mission The mission details to create.
	 * @param result  The binding result that holds the validation errors.
	 * @return The created mission or validation errors.
	 */
	@PostMapping
	@Operation(summary = "Create a new mission", description = "Create a new mission with detailed validation error response. Returns the created mission or validation errors if validation fails.")
	@ApiResponse(responseCode = "201", description = "Mission created successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = Mission.class)) })
	@ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))

	public ResponseEntity<?> createMission(@Valid @RequestBody Mission mission, BindingResult result) {
		if (result.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			result.getAllErrors().forEach(error -> {
				String fieldName = ((FieldError) error).getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			});
			return ResponseEntity.badRequest().body(errors);
		}
		Mission savedMission = missionService.createMission(mission);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedMission);
	}

	/**
	 * Retrieves a paginated list of all missions. This endpoint provides a simple
	 * way to access
	 * a slice of the mission database using pagination parameters. If no pagination
	 * parameters are
	 * provided, default values are used.
	 *
	 * @param page The zero-based page index of the page to retrieve. Defaults to 0
	 *             if not specified,
	 *             which means the first page.
	 * @param size The size of the page to retrieve. Defaults to 10 if not
	 *             specified, which controls
	 *             the number of missions returned in a single response.
	 * @return A {@link ResponseEntity} object containing a {@link Page} of
	 *         {@link Mission} objects.
	 *         The response encapsulates the paginated result set along with HTTP
	 *         status code 200 (OK),
	 *         indicating successful retrieval of the data.
	 */
	// @GetMapping
	// public ResponseEntity<Page<Mission>> getAllMissions(@RequestParam(value =
	// "page", defaultValue = "0") int page,
	// @RequestParam(value = "size", defaultValue = "10") int size) {
	// Pageable pageable = PageRequest.of(page, size);
	// Page<Mission> pageResult = missionService.findAllMissions(pageable);
	// return ResponseEntity.ok(pageResult);
	// }

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
	@GetMapping
	@Operation(summary = "Retrieve missions with filters", description = "Retrieves a paginated list of missions based on various filtering and sorting criteria. This allows for flexible retrieval of mission data including the ability to filter by status, nature of mission, and a search term matching the mission label or username of the assigned user.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved the filtered missions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
	public ResponseEntity<Page<Mission>> getAllMissionsWithSpecs(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "order", defaultValue = "asc") String order,
			@RequestParam(value = "sort", defaultValue = "startDate") String sortField,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "nature", required = false) String natureMission,
			@RequestParam(value = "searchbar", required = false) String userNameOrLabel) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sortField));
		Page<Mission> missions = missionService.findAllMissionsWithSpecs(status, natureMission, userNameOrLabel,
				pageable);

		return ResponseEntity.ok(missions);
	}

	/**
	 * Retrieve a single mission by its ID.
	 * 
	 * @param id The ID of the mission to retrieve.
	 * @return The requested mission.
	 */
	@GetMapping("/{id}")
	@Operation(summary = "Retrieve a mission by ID", description = "Fetches a detailed view of a single mission by its unique identifier.")
	@ApiResponse(responseCode = "200", description = "Mission found and returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mission.class)))
	@ApiResponse(responseCode = "404", description = "Mission not found", content = @Content(mediaType = "application/json"))
	public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
		return ResponseEntity.ok(missionService.findOneMission(id));
	}

	/**
	 * Update an existing mission.
	 * 
	 * @param id      The ID of the mission to update.
	 * @param mission Updated details for the mission.
	 *                * @param result The binding result that holds the validation
	 *                errors.
	 * @return The updated mission or errors.
	 */
	@PutMapping("/{id}")
	@Operation(summary = "Update an existing mission", description = "Updates the details of an existing mission. If validation fails, returns errors.")
	@ApiResponse(responseCode = "200", description = "Mission updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mission.class)))
	@ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
	@ApiResponse(responseCode = "404", description = "Mission not found", content = @Content)

	public ResponseEntity<?> updateMission(@PathVariable Long id, @Valid @RequestBody Mission mission,
			BindingResult result) {
		if (result.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			result.getAllErrors().forEach((error) -> {
				String fieldName = ((FieldError) error).getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			});
			return ResponseEntity.badRequest().body(errors);
		}
		return ResponseEntity.ok(missionService.updateMission(id, mission));
	}

	/**
	 * Updates the status of a mission. This endpoint is restricted to managers and
	 * administrators.
	 * Allows setting the mission status to either "Validé" or "Rejeté".
	 *
	 * @param missionId the ID of the mission to update
	 * @param status    the new status to be set for the mission
	 *                  * @param result The binding result that holds the validation
	 *                  errors.
	 * @return a ResponseEntity with status OK if the update is successful
	 * @throws AccessDeniedException if the user is not authorized to update the
	 *                               mission status
	 */
	@PutMapping("/{id}/status")
	@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
	@Operation(summary = "Update the status of a mission", description = "Updates the status of a specific mission to either 'Validé' or 'Rejeté'. Access is restricted to users with manager or administrator roles.",
			// security = @SecurityRequirement(name = "roleBasedAuth"),
			tags = {
					"Mission Management" })
	@ApiResponse(responseCode = "200", description = "Mission status updated successfully", content = @Content(mediaType = "application/json"))
	@ApiResponse(responseCode = "400", description = "Validation errors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
	@ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json"))
	@ApiResponse(responseCode = "404", description = "Mission not found", content = @Content(mediaType = "application/json"))

	public ResponseEntity<?> updateMissionStatus(@PathVariable Long id, @Valid @RequestParam StatusEnum status,
			BindingResult result) {
		if (result.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			result.getAllErrors().forEach((error) -> {
				String fieldName = ((FieldError) error).getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			});
			return ResponseEntity.badRequest().body(errors);
		}
		return ResponseEntity.ok(missionService.updateMissionStatus(id, status));
	}

	/**
	 * Delete a mission by its ID.
	 * 
	 * @param id The ID of the mission to delete.
	 * @return A status of 204 No Content on successful deletion.
	 */
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a mission", description = "Deletes a mission from the system by its unique identifier. This operation is irreversible.")
	@ApiResponse(responseCode = "204", description = "Mission deleted successfully, no content to return.", content = @Content)
	@ApiResponse(responseCode = "404", description = "Mission not found, unable to delete.", content = @Content)

	public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
		missionService.deleteMission(id);
		return ResponseEntity.noContent().build();
	}
}
