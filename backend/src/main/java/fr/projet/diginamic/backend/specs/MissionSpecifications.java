package fr.projet.diginamic.backend.specs;

import org.springframework.data.jpa.domain.Specification;

import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.entities.NatureMission;
import fr.projet.diginamic.backend.entities.UserEntity;
import fr.projet.diginamic.backend.enums.StatusEnum;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

/**
 * Class containing various specifications for querying Mission entities.
 * Specifications are used to create dynamic and complex queries using the Criteria API.
 */
public class MissionSpecifications {

    /**
     * Specification to filter missions based on the manager's ID.
     * 
     * @param id the ID of the manager to filter by.
     * @return a Specification that filters missions where the manager's ID matches the given ID.
     */
    public static Specification<Mission> hasManagerId (Long id){

        return (root, query, criteriaBuilder) -> {
            if(id == null){
                return criteriaBuilder.conjunction();
            }
            Join<Mission, UserEntity> userJoin = root.join("user", JoinType.LEFT);          
            return criteriaBuilder.equal(userJoin.get("manager").get("id"), id);
        };
    }

    public static Specification<Mission> hasUserId (Long id){

        return (root, query, criteriaBuilder) -> {
            if(id == null){
                return criteriaBuilder.conjunction();
            }
            Join<Mission, UserEntity> userJoin = root.join("user", JoinType.LEFT);          
            return criteriaBuilder.equal(userJoin.get("id"), id);
        };
    }

    /**
     * Specification to filter missions by status using the 'type' field of the
     * StatusEnum. This specification attempts to match the provided status
     * string with a valid StatusEnum value. If the status string is null,
     * empty, or does not correspond to any valid StatusEnum value, no filtering
     * by status is applied, and the query continues without considering this
     * criterion.
     * 
     * @param statusStr The status description to filter missions by. It should
     *                  match one of the values defined in StatusEnum. The match
     *                  is case-insensitive.
     * @return A Specification object that filters missions based on their status
     *         description. If the status string is invalid, the specification 
     *         will not apply any filtering for the status.
     */
    public static Specification<Mission> hasStatus(String statusStr) {
        return (root, query, criteriaBuilder) -> {
            if (statusStr == null || statusStr.isEmpty()) {
                return criteriaBuilder.conjunction(); 
            }
            try {
                StatusEnum statusEnum = StatusEnum.fromString(statusStr);
                return criteriaBuilder.equal(root.get("status"), statusEnum);
            } catch (IllegalArgumentException e) {
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    /**
     * Specification to filter missions by the label of their nature. This
     * specification attempts to match the provided nature string with a valid
     * NatureMission label in a case-insensitive manner. If the nature string is null,
     * empty, or does not correspond to any valid NatureMission label, no filtering
     * by nature is applied, and the query continues without considering this
     * criterion.
     * 
     * @param natureStr The label of the nature to filter missions by. The match is
     *                  case-insensitive.
     * @return A Specification<Mission> object that filters missions based on their
     *         nature's label. If the nature string is invalid, the specification
     *         will not apply any filtering for the nature.
     */
    public static Specification<Mission> hasNature(String natureStr) {
        return (root, query, criteriaBuilder) -> {
            if (natureStr == null || natureStr.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filtering if nature is not specified
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.join("natureMission").get("label")),
                                        natureStr.toLowerCase());
        };
    }


    /**
     * Specification to filter missions by the user's name (either first or last
     * name).
     * 
     * @param userName The user name to filter by. It searches both 'firstName' and
     *                 'lastName'.
     * @return A Specification object that filters missions where the user's first
     *         or last name contains the provided username.
     */
    public static Specification<Mission> hasUserName(String userName) {
        return (root, query, criteriaBuilder) -> {
            if (userName == null) {
                return criteriaBuilder.disjunction();
            } else {
                Join<Mission, UserEntity> userJoin = root.join("user", JoinType.LEFT);
                String[] nameParts = userName.trim().toLowerCase().split("\\s+");
                
                Predicate namePredicate;
    
                if (nameParts.length == 1) {
                    Predicate firstNamePredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(userJoin.get("firstName")), "%" + nameParts[0] + "%");
    
                    Predicate lastNamePredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(userJoin.get("lastName")), "%" + nameParts[0] + "%");
    
                    namePredicate = criteriaBuilder.or(firstNamePredicate, lastNamePredicate);
                } else {
                    Predicate combinedPredicate = criteriaBuilder.and(
                            criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("firstName")), "%" + nameParts[0] + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("lastName")), "%" + nameParts[1] + "%")
                    );

                    Predicate swappedCombinedPredicate = criteriaBuilder.and(
                            criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("firstName")), "%" + nameParts[1] + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("lastName")), "%" + nameParts[0] + "%")
                    );

                    namePredicate = criteriaBuilder.or(combinedPredicate, swappedCombinedPredicate);
                }
                return namePredicate;
            }
        };
    }

    /**
     * Creates a specification to filter missions based on their label.
     * This method supports partial matching, meaning that any mission containing
     * the specified label substring in a case-insensitive manner will be returned.
     *
     * @param label The label or part of the label of the mission to filter by.
     *              If null, no filtering will be applied based on the label.
     * @return A Specification<Mission> that can be used to filter queries by the
     *         mission label.
     */
    public static Specification<Mission> hasLabel(String label) {
        return (root, query, criteriaBuilder) -> {
            if (label == null) {
                return criteriaBuilder.disjunction();
            } else {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("label")), "%" + label.toLowerCase() + "%");
            }
        };
    }

    public static Specification<Mission> hasExpense(String withExpense) {
        return (root, query, criteriaBuilder) -> {
            if (withExpense == null) {
                return criteriaBuilder.disjunction();
            } else {
                return criteriaBuilder.isNotNull(root.get("expense"));
            }
        };
    }

    /**
     * Creates a specification to find missions eligible for bounty calculation.
     * This specification filters missions based on the following criteria:
     * - The mission status is "FINISHED".
     * - The nature of the mission is marked as eligible for bounty.
     * - The bounty date is null, indicating that the bounty has not been calculated yet.
     *
     * @return a specification to find missions that are eligible for bounty calculation
     */
    public static Specification<Mission> missionEligibleForBountyToCalculate(){
        return (root, query, criteriaBuilder) -> {
            Join<Mission, NatureMission> natureMissionJoin = root.join("natureMission", JoinType.LEFT);
            Predicate hasStatusFinished = criteriaBuilder.equal(root.get("status"), StatusEnum.FINISHED);
            Predicate isEligibleToBounty = criteriaBuilder.isTrue(natureMissionJoin.get("isEligibleToBounty"));
            Predicate bountyDateIsNull = criteriaBuilder.isNull(root.get("bountyDate"));

            return criteriaBuilder.and(hasStatusFinished, isEligibleToBounty, bountyDateIsNull);
            
        };
    }

    /**
     * Combines multiple search and filter specifications into one based on various
     * mission attributes.
     * This specification allows filtering by status, nature, user name, and mission
     * label.
     *
     * @param managerId       The manager's id of the employees to filter by.
     * @param status          The status of the mission to filter by.
     * @param nature          The nature of the mission to filter by.
     * @param labelOrUsername Either a username or a mission's label to filter by.
     * 
     * @return A combined specification based on the provided filters.
     */

    public static Specification<Mission> createSpecificationForAdmin(String status, String nature,
            String labelOrUsername) {
        Specification<Mission> spec = Specification.where(null);

        if (status != null && !status.isEmpty()) {
            spec = spec.and(hasStatus(status));
        }

        if (nature != null && !nature.isEmpty()) {
            spec = spec.and(hasNature(nature));
        }

        if (labelOrUsername != null && !labelOrUsername.isEmpty()) {
            spec = spec.and(Specification.where(hasUserName(labelOrUsername)).or(hasLabel(labelOrUsername)));
        }
        return spec;
    }

    /**
     * Combines multiple search and filter specifications into one based on various
     * mission attributes.
     * This specification allows filtering by status, nature, user name, and mission
     * label.
     *
     * @param managerId       The manager's id of the employees to filter by.
     * @param status          The status of the mission to filter by.
     * @param nature          The nature of the mission to filter by.
     * @param labelOrUsername Either a username or a mission's label to filter by.
     * 
     * @return A combined specification based on the provided filters.
     */

    public static Specification<Mission> filterMissionsByCriteriaForManager(Long managerId, String status, String nature,
            String labelOrUsername) {
        Specification<Mission> spec = Specification.where(null);

        if(managerId != null){
            spec = spec.and(hasManagerId(managerId));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and(hasStatus(status));
        }

        if (nature != null && !nature.isEmpty()) {
            spec = spec.and(hasNature(nature));
        }

        if (labelOrUsername != null && !labelOrUsername.isEmpty()) {
            spec = spec.and(Specification.where(hasUserName(labelOrUsername)).or(hasLabel(labelOrUsername)));
        }
        return spec;
    }

    /**
     * Builds a composite specification for filtering missions based on status,
     * nature, and label.
     * This method is intended for use by employees to refine their mission queries,
     * allowing them to apply filters for status, nature of the mission, and label.
     * Each parameter is optional; if provided, it is used to filter the missions;
     * if not provided or empty, that criterion is not used in the filter.
     *
     * @param status The desired status of missions to filter by. If null or empty,
     *               no status filter is applied.
     * @param nature The nature of the missions to filter by. If null or empty, no
     *               nature filter is applied.
     * @param label  The label of the missions to filter by. If null or empty, no
     *               label filter is applied.
     * @return A Specification<Mission> that can be used to filter missions based on
     *         the provided criteria.
     */
    public static Specification<Mission> filterMissionsByCriteriaForEmployee(Long userId, String status, String nature,
            String label, String withExpense) {
        Specification<Mission> spec = Specification.where(null);

        if(userId != null){
            spec = spec.and(hasUserId(userId));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and(hasStatus(status));
        }
        if (nature != null && !nature.isEmpty()) {
            spec = spec.and(hasNature(nature));
        }
        if (label != null && !label.isEmpty()) {
            spec = spec.and(hasLabel(label));
        }

        if (withExpense != null && !withExpense.isEmpty()) {
            spec = spec.and(hasExpense(withExpense));
        }

        return spec;
    }

}