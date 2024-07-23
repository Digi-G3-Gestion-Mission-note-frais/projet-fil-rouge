package fr.projet.diginamic.backend.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import com.itextpdf.text.BaseColor;
// import com.itextpdf.text.Document;
// import com.itextpdf.text.DocumentException;
// import com.itextpdf.text.Font;
// import com.itextpdf.text.PageSize;
// import com.itextpdf.text.Phrase;
// import com.itextpdf.text.pdf.BaseFont;
// import com.itextpdf.text.pdf.PdfWriter;

import fr.projet.diginamic.backend.dtos.ExpenseDto;
import fr.projet.diginamic.backend.dtos.ExpenseWithLinesDto;
import fr.projet.diginamic.backend.entities.Expense;
import fr.projet.diginamic.backend.repositories.interfaces.ExpenseRepository;
import fr.projet.diginamic.backend.utils.ExpenseMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

/**Service for all expense's related method*/
@Service
public class ExpenseService {
	@Autowired
	private ExpenseRepository expenseRepo;
	
	@Autowired
	private ExpenseMapper expenseMapper;
	
	/**Method get all expenses and transform them into ExpenseDto
     * @return the List of all expenses
     */
	public ArrayList<ExpenseDto> getExpenses(){
		ArrayList<Expense> expenses= expenseRepo.findAll();
		ArrayList<ExpenseDto> expensesDto= new ArrayList();
		for(Expense expense: expenses) {
			expensesDto.add(expenseMapper.BeanToDto(expense));
		}
		return expensesDto;
	}
	
	/**Method to get an expense by its id and transform it into an ExpenseDto
	 * @param id, the id of the expense to get
     * @return the expenseLine found
     */
	public ExpenseWithLinesDto getExpense(Long id){
		Expense expense= expenseRepo.findById(id).orElse(null);
		ExpenseWithLinesDto expenseDto= expenseMapper.BeanToDtoWithLines(expense);
		return expenseDto;
	}

	/**Method to get an expense by its id 
	 * @param id, the id of the expense to get
     * @return the expense found 
	 * @throws EntityNotFoundException if not found
     */
	public Expense getExpenseBean(Long id){
		return expenseRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Expense not found with ID: " + id));
	}
	
	/**Method to save an expense
	 * @param expense, the id of the expense to save
     * @return the expense saved
     */
	public Expense saveExpense(ExpenseDto expense){
		Expense expenseSave= expenseMapper.dtoToBean(expense);
		expenseSave= expenseRepo.save(expenseSave);
		return expenseSave;
	}
	
//	public void exportExpense(Long id, HttpServletResponse response) throws IOException, DocumentException{
//		Expense expense= expenseRepo.findById(id).orElse(null);
//		
//		response.setHeader("Content-Disposition", "attachment; filename=\"NoteDeFrais.pdf\"");
//		Document document = new Document(PageSize.A4);
//		PdfWriter.getInstance(document, response.getOutputStream());
//		document.open();
//		document.addTitle("Fiche");
//		document.newPage();
//		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
//		Phrase p1 = new Phrase("Nom departement: "+dGov.getNom()+", code departement:"+dGov.getCode() , new Font(baseFont, 32.0f, 1, new BaseColor(0, 51, 80)));
//		document.add(p1);
//		
//		for(Ville ville: villes) {
//			Phrase p = new Phrase("Nom ville: "+ville.getNom()+", nombre d'habitants:"+ville.getNbHabitant() , new Font(baseFont, 32.0f, 1, new BaseColor(0, 51, 80)));
//			document.add(p);
//		}
//		document.close();
//		response.flushBuffer();
//	}
}
