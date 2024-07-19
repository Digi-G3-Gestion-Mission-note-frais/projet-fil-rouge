package fr.projet.diginamic.backend.dtos;

import java.util.ArrayList;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** The Dto of the expense with all the expenseLines */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ExpenseWithLinesDto {
	
	/** The Id of the expense */
	private Long id;
	
	/** The date of the expense */
	private Date date;
	
    /** The date of the status */
	private String status;
	
    /** The expenseLines of this expense */
	public ArrayList<ExpenseLineDto> expenseLines;
}
