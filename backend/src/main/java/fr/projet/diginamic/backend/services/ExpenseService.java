package fr.projet.diginamic.backend.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import fr.projet.diginamic.backend.dtos.ExpenseDto;
import fr.projet.diginamic.backend.dtos.ExpenseWithLinesDto;
import fr.projet.diginamic.backend.entities.Expense;
import fr.projet.diginamic.backend.entities.ExpenseLine;
import fr.projet.diginamic.backend.repositories.interfaces.ExpenseRepository;
import fr.projet.diginamic.backend.utils.ExpenseMapper;
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
	public Page<ExpenseDto> getExpenses(int page, int size){
		Pageable pagination = PageRequest.of(page, size);
    	Page<Expense>expenses=expenseRepo.findAll(pagination);
    	Page<ExpenseDto> expensesDto = expenses.map(expenseMapper::BeanToDto);
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
	
	/**Method to save an expense
	 * @param expense, the id of the expense to save
     * @return the expense saved
     */
	public Expense saveExpense(ExpenseDto expense){
		Expense expenseSave= expenseMapper.dtoToBean(expense);
		expenseSave= expenseRepo.save(expenseSave);
		return expenseSave;
	}
	
public void exportExpense(Long id, HttpServletResponse response) throws IOException, DocumentException{
		Expense expense= expenseRepo.findById(id).orElse(null);
        if (expense == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Expense not found");
            return;
        }
		
		response.setHeader("Content-Disposition", "attachment; filename=\"NoteDeFrais.pdf\"");
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		document.addTitle("Note de frais de " +expense.getMission().getUser().getFirstName()+" "+expense.getMission().getUser().getLastName());
		document.newPage();
		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		Phrase p1 = new Phrase("Note de frais de " +expense.getMission().getUser().getFirstName()+" "+expense.getMission().getUser().getLastName() , new Font(baseFont, 32.0f, 1, new BaseColor(0, 51, 80)));
		Phrase p2 = new Phrase("Libelle de mission: " +expense.getMission().getLabel() , new Font(baseFont, 32.0f, 1, new BaseColor(0, 51, 80)));
		document.add(p1);
		document.add(p2);
		
		PdfPTable table = new PdfPTable(4);
		addTableHeader(table);
		addRows(table, expense);
		document.add(table);

		document.close();
		response.flushBuffer();
	}
private void addTableHeader(PdfPTable table) {
    Stream.of("Date", "Type de frais", "Montant", "Tva")
      .forEach(columnTitle -> {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(columnTitle));
        table.addCell(header);
    });
    
   
}
private void addRows(PdfPTable table, Expense expense) {
	ArrayList<ExpenseLine> expenseLines= expense.getExpenseLines();
	for (ExpenseLine expenseLine : expenseLines) {
		System.out.println("boucle");
		PdfPCell date = new PdfPCell();
		date.setPhrase(new Phrase(expenseLine.getDate().toString()));
		table.addCell(date);
		PdfPCell type = new PdfPCell();
		type.setPhrase(new Phrase(expenseLine.getExpenseType().getType()));
		table.addCell(type);
		PdfPCell amount = new PdfPCell();
		amount.setPhrase(new Phrase(String.valueOf(expenseLine.getAmount())));
		table.addCell(amount);
		PdfPCell tva = new PdfPCell();
		tva.setPhrase(new Phrase(String.valueOf(expenseLine.getTva())));
		table.addCell(tva);
	}
}
}
