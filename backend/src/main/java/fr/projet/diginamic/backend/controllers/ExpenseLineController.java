package fr.projet.diginamic.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.diginamic.backend.dtos.ExpenseLineDto;
import fr.projet.diginamic.backend.entities.ExpenseLine;
import fr.projet.diginamic.backend.services.ExpenseLineService;

/** Controller for all ExpenseLine*/
@RestController
@RequestMapping("/api/expenseLines")
public class ExpenseLineController {
	
	@Autowired
	ExpenseLineService expenseLineService;
	
	/** Endpoint to obtain a list of all ExpenseLines
     * @return the expense found
     * @throws Exception if there is no result
     */
	 @GetMapping
	    public Page<ExpenseLineDto> getExpenseLines(@RequestParam int page, @RequestParam int size) {
		 Page<ExpenseLineDto> expenseLines= expenseLineService.getExpenseLines(page, size);
	    	return expenseLines;
	    }
	 
	 /** Endpoint to save one ExpenseLine 
	     * @param expenseLine, the ExpenseLine to save
	     * @return a responseEntity with a succes message
	     * @throws Exception if the save doesn't work
	     */
	 @PostMapping
	    public  ResponseEntity<String> saveExpenseLine(@RequestBody ExpenseLineDto expenseLine) throws Exception {
		 ExpenseLine expenseLineSave= expenseLineService.saveExpenseLine(expenseLine);
		 if(expenseLineSave== null) {
	    		throw new Exception("The expenseLine was not save");
	    	}
	        return new ResponseEntity<>("Success", HttpStatus.CREATED);
	    }
	 /** Endpoint to obtain one ExpenseLine by the id
	     * @param id, the expenseLine id
	     * @return the expenseLine found
	     * @throws Exception if there is no result
	     */
	 @GetMapping("/{id}")
	    public ExpenseLineDto getExpenseLine(@PathVariable Long id) throws Exception {
		 ExpenseLineDto expenseLine= expenseLineService.getExpenseLine(id);
	        if(expenseLine== null) {
	    		throw new Exception("No expenseLine found");
	    	}
	    	return expenseLine;
	    }
	 
	 /** Endpoint to delete one Expense by the id
	     * @param id, the expense id
	     * @return ResponseEntity with a success message
	     * @throws Exception if there is nothing to delete
	     */
	 @DeleteMapping
	    public ResponseEntity<String> DeleteExpenseLine(@PathVariable Long id) throws Exception {
		 ExpenseLine expenseLine= expenseLineService.deleteExpenseLine(id);
	        if(expenseLine== null) {
	    		throw new Exception("The expenseLine was not deleted");
	    	}
	        return new ResponseEntity<>("Success", HttpStatus.CREATED);
	    }
	 
	 /** Endpoint to modify one Expense by the id
	     * @param expenseLine, the new expenseLine
	     * @param id, the expense id
	     * @return ResponseEntity with a success message
	     * @throws Exception if there is no result
	     */
	 @PutMapping("/{id}")
	    public ResponseEntity<String> modifyExpenseLine(@RequestBody ExpenseLineDto expenseLine, @PathVariable Long id) throws Exception{
	        ExpenseLine expenseLineModify = expenseLineService.modifyExpenseLine(expenseLine,id);
	        return new ResponseEntity<>("Success", HttpStatus.OK);
	    }

}
