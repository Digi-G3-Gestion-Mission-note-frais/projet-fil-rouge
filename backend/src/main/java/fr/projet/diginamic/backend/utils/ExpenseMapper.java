package fr.projet.diginamic.backend.utils;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.projet.diginamic.backend.dtos.ExpenseDto;
import fr.projet.diginamic.backend.dtos.ExpenseLineDto;
import fr.projet.diginamic.backend.dtos.ExpenseWithLinesDto;
import fr.projet.diginamic.backend.entities.Expense;
import fr.projet.diginamic.backend.entities.ExpenseLine;
import fr.projet.diginamic.backend.repositories.interfaces.ExpenseTypeRepository;

/**
 * Method to transform a ExpenseLine to an ExpenseLineDto
 * 
 * @param ExpenseLine, the bean to transform
 * @return the expenseLineDto object after the mapping
 */
@Service
public class ExpenseMapper {

	@Autowired
	ExpenseTypeRepository expenseTypeRepository;

	/**
	 * Method to transform a Expense to an ExpenseDto
	 * 
	 * @param exp, the dto to transform
	 * @return the expense object after the mapping
	 */
	public Expense dtoToBean(ExpenseDto exp) {
		Expense newExp = new Expense();
		newExp.setDate(exp.getDate());
		newExp.setStatus(exp.getStatus());
		return newExp;

	}

	/**
	 * Method to transform a Expense to an ExpenseDto
	 * 
	 * @param exp, the bean to transform
	 * @return the expenseDto object after the mapping
	 */
	public ExpenseDto BeanToDto(Expense exp) {
		ExpenseDto newExp = new ExpenseDto();
		newExp.setDate(exp.getDate());
		newExp.setStatus(exp.getStatus());
		newExp.setId(exp.getId());
		return newExp;

	}

	/**
	 * Method to transform a ExpenseWithLinesDto to an Expense
	 * 
	 * @param exp, the dto to transform
	 * @return the expense object after the mapping
	 */
	public Expense dtoWithLinesToBean(ExpenseWithLinesDto exp) {
		ArrayList<ExpenseLine> lines = new ArrayList<>();
		for (ExpenseLineDto line : exp.getExpenseLines()) {
			ExpenseLine newLine = new ExpenseLine();
			newLine.setTva(line.getTva());
			newLine.setAmount(line.getAmount());
			newLine.setExpenseType(expenseTypeRepository.findByType(line.getExpenseType()));
			newLine.setDate(line.getDate());
			lines.add(newLine);
		}

		Expense newExp = new Expense();
		newExp.setDate(exp.getDate());
		newExp.setStatus(exp.getStatus());
		newExp.setExpenseLines(lines);
		return newExp;

	}

	/**
	 * Method to transform a Expense to an ExpenseWithLinesDto
	 * 
	 * @param exp, the bean to transform
	 * @return the ExpenseWithLinesDto object after the mapping
	 */
	public ExpenseWithLinesDto BeanToDtoWithLines(Expense exp) {

		ArrayList<ExpenseLineDto> lines = new ArrayList<>();
		for (ExpenseLine line : exp.getExpenseLines()) {
			ExpenseLineDto newLine = new ExpenseLineDto();
			newLine.setTva(line.getTva());
			newLine.setAmount(line.getAmount());
			newLine.setExpenseType(line.getExpenseType().getType());
			newLine.setDate(line.getDate());
			newLine.setId(line.getId());
			lines.add(newLine);
		}

		ExpenseWithLinesDto newExp = new ExpenseWithLinesDto();
		newExp.setDate(exp.getDate());
		newExp.setStatus(exp.getStatus());
		newExp.setExpenseLines(lines);
		newExp.setId(exp.getId());
		return newExp;

	}

}