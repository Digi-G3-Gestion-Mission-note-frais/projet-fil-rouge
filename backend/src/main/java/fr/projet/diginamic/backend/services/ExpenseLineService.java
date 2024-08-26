package fr.projet.diginamic.backend.services;

import java.util.ArrayList;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.projet.diginamic.backend.dtos.ExpenseDto;
import fr.projet.diginamic.backend.dtos.ExpenseLineDto;
import fr.projet.diginamic.backend.entities.Expense;
import fr.projet.diginamic.backend.entities.ExpenseLine;
import fr.projet.diginamic.backend.entities.ExpenseType;
import fr.projet.diginamic.backend.repositories.interfaces.ExpenseLineRepository;
import fr.projet.diginamic.backend.repositories.interfaces.ExpenseTypeRepository;
import fr.projet.diginamic.backend.utils.ExpenseLineMapper;
import org.springframework.transaction.annotation.Transactional;

/** Service for all ExpenseLine's related method */
@Service
public class ExpenseLineService {
	@Autowired
	private ExpenseLineRepository expenseLineRepo;

	@Autowired
	private ExpenseTypeRepository expenseTypeRepo;

	@Autowired
	private ExpenseLineMapper expenseLineMapper;
	
	/** method to get all ExpenseLines and transform them into ExpenseLineDto
     * @return expenseLinesDto, a Dto with all ExpenseLines
     */
	public Page<ExpenseLineDto> getExpenseLines(int page, int size){
		Pageable pagination = PageRequest.of(page, size);
    	Page<ExpenseLine>expenseLines=expenseLineRepo.findAll(pagination);
    	Page<ExpenseLineDto> expenseLinesDto = expenseLines.map(expenseLineMapper::BeanToDto);
		return expenseLinesDto;
	}

	/**
	 * Method to get an expenseLine his id transform it into ExpenseLineDto
	 * 
	 * @param id, the id of the expenseLine
	 * @return An expenseLineDto
	 */
	public ExpenseLineDto getExpenseLine(Long id)  {
		ExpenseLine expenseLine = expenseLineRepo.findById(id).orElseThrow(()->new EntityNotFoundException("Expense not find with id: " +id ));
		ExpenseLineDto expenseLineDto = expenseLineMapper.BeanToDto(expenseLine);
		return expenseLineDto;
	}

	/**
	 * Method to save an expenseLine
	 * 
	 * @param expenseLine, the expenseLine to save
	 * @return the expenseLine who was saved
	 */
	public ExpenseLine saveExpenseLine(ExpenseLineDto expenseLine) {
		ExpenseLine expenseLineSave = expenseLineMapper.dtoToBean(expenseLine);
		expenseLineSave = expenseLineRepo.save(expenseLineSave);
		return expenseLineSave;
	}

	/**
	 * Method to delete an expenseLine by its id
	 * 
	 * @param id, the id of the expenseLine to delete
	 * @return the expenseLine deleted
	 */
	public ExpenseLine deleteExpenseLine(Long id) {
		ExpenseLine expenseLine = expenseLineRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("ExpenseLine not find with id: " +id ));
		expenseLineRepo.delete(expenseLine);
		return expenseLine;
	}

	/**
	 * Method to modify an expenseLine
	 * 
	 * @param expenseLine, the new expenseLine
	 * @param id,          the id of the expenseLine to modify
	 * @return the expenseLine after modification
	 */
	@Transactional
	public ExpenseLine modifyExpenseLine(ExpenseLineDto expenseLine, Long id) {

		ExpenseLine expenseLineBdd = expenseLineRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + id));

		expenseLineBdd.setTva(expenseLine.getTva());
		expenseLineBdd.setAmount(expenseLine.getAmount());
		expenseLineBdd.setDate(expenseLine.getDate());

		// Handle ExpenseType
		ExpenseType expType = expenseTypeRepo.findByType(expenseLine.getExpenseType());
		if (expType == null) {
			ExpenseType expTypeNew = new ExpenseType();
			expTypeNew.setType(expenseLine.getExpenseType());
			expType = expenseTypeRepo.save(expTypeNew);
		}
		expenseLineBdd.setExpenseType(expType);

		return expenseLineRepo.save(expenseLineBdd);
	}

}
