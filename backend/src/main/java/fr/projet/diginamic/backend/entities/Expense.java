package fr.projet.diginamic.backend.entities;

import java.util.ArrayList;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** The entity of the expense */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public class Expense {
	/** The Id of the expense */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;
	/** The date of the expense */
	@Column(name = "date")
	@Temporal(TemporalType.DATE)
	private Date date;
	/** The date of the status */
	@Column(name = "status")
	private String status;

	/** The expenseLines of this expense */
	@OneToMany(mappedBy = "expense")
	public ArrayList<ExpenseLine> expenseLines;

	/** The mission linked to the expense */
	@OneToOne
	private Mission mission;

}
