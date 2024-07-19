package fr.projet.diginamic.backend.entities;

import java.util.Date;

import fr.projet.diginamic.backend.enums.StatusEnum;
import fr.projet.diginamic.backend.enums.TransportEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The Mission entity class represents a mission assigned to an
 * employee{@link UserEntity}. It includes details about the mission such as
 * start and
 * end dates, the nature of the mission, the starting and destination cities,
 * transport type, and the associated costs etc.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "MISSION")
public class Mission {

	/**
	 * Unique identifier for the mission.
	 */
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * Short descriptive title of the mission.
	 */
	@Column(name = "label", length = 150)
	private String label;

	/**
	 * Daily rate for the mission.
	 */
	@Min(value = 1)
	@Column(name = "daily_rate", nullable = false)
	private Double dailyRate;

	/**
	 * Current status of the mission (e.g., pending, approved).
	 */
	@Column(name = "status", length = 150, nullable = false)
	@Enumerated(EnumType.STRING)
	private StatusEnum status;

	/**
	 * Starting date of the mission.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "start_date", nullable = false)
	private Date startDate;

	/**
	 * Ending date of the mission.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date", nullable = false)
	private Date endDate;

	/**
	 * TransportEnum mode for the mission.
	 */
	@Size(min = 2, max = 150)
	@Column(name = "transport", length = 150)
	@Enumerated(EnumType.STRING)
	private TransportEnum transport;

	/**
	 * Departure city for the mission.
	 */
	@Size(min = 2, max = 150)
	@Column(name = "departure_city", length = 150, nullable = false)
	private String departureCity;

	/**
	 * Arrival city for the mission.
	 */
	@Size(min = 2, max = 150)
	@Column(name = "arrival_city", length = 150, nullable = false)
	private String arrivalCity;

	/**
	 * Date when the bonus is given.
	 */
	@Column(name = "bonus_date")
	private Date bonusDate;

	/**
	 * Bonus amount for the mission.
	 */
	@Min(value = 0)
	@Column(name = "bonus_amount")
	private Double bonusAmount;

	/**
	 * The employee assigned to the mission.
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private UserEntity user;

	/**
	 * Nature of the mission.
	 */
	@ManyToOne
	@JoinColumn(name = "nature_mission_id", referencedColumnName = "id", nullable = false)
	private NatureMission natureMission;

	/**
	 * Expense report associated with the mission.
	 */
	@OneToOne
	@JoinColumn(name = "expense_id", referencedColumnName = "id")
	private Expense expense;

}
