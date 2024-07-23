package fr.projet.diginamic.backend.specs;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;

import fr.projet.diginamic.backend.entities.Mission;
import fr.projet.diginamic.backend.entities.UserEntity;

public class MissionSpecifications {

    public static Specification<Mission> hasManagerId (Long id){

        return (root, query, criteriaBuilder) -> {
            if(id == null){
                return criteriaBuilder.conjunction();
            }
            Join<Mission, UserEntity> userJoin = root.join("user", JoinType.LEFT);          
            return criteriaBuilder.equal(userJoin.get("manager").get("id"), id);
        };
    }

    /**
     * Specification to filter missions by status using the 'type' field of the
     * StatusEnum.
     * 
     * @param statusStr The status description to filter missions by.
     * @return A Specification object that filters missions based on their status
     *         description.
     */
    public static Specification<Mission> hasStatus(String statusStr) {
        return (root, query, criteriaBuilder) -> {
            if (statusStr == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), statusStr);
        };
    }

    /**
     * Specification to filter missions by the label of their nature.
     * 
     * @param nature The label of the nature to filter missions by.
     * @return A Specification<Mission> object that filters missions based on their
     *         nature's label.
     */
    public static Specification<Mission> hasNature(String nature) {
        return (root, query, criteriaBuilder) -> {
            if (nature == null) {
                return criteriaBuilder.conjunction(); // Returns all if no nature is specified
            }
            return criteriaBuilder.equal(root.join("NatureMission").get("label"), nature);
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
    public static Specification<Mission> filterMissionsByCriteriaForEmployee(String status, String nature,
            String label) {
        Specification<Mission> spec = Specification.where(null);
        if (status != null && !status.isEmpty()) {
            spec = spec.and(hasStatus(status));
        }
        if (nature != null && !nature.isEmpty()) {
            spec = spec.and(hasNature(nature));
        }
        if (label != null && !label.isEmpty()) {
            spec = spec.and(hasLabel(label));
        }
        return spec;
    }

}
