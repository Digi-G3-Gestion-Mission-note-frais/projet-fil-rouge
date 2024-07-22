package fr.projet.diginamic.backend.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import fr.projet.diginamic.backend.entities.UserEntity;
import fr.projet.diginamic.backend.repositories.interfaces.UserRepository;
import fr.projet.diginamic.backend.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
	private UserRepository userRepo;
	
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

	/**Method get all expenses of a user and transform them into ExpenseDto
	 * @return the List of all expenses og this user.
	 */
	public Page<ExpenseDto> getMyExpenses(int page, int size, Long id){
		Pageable pagination = PageRequest.of(page, size);
		Page<Expense>expenses=expenseRepo.findByMission_User_Id(id, pagination);
		Page<ExpenseDto> expensesDto = expenses.map(expenseMapper::BeanToDto);
		return expensesDto;
	}

	/**Method get all expenses of a manager, their associates and transform them into ExpenseDto
	 * @return the List of all expenses og this a manager and their associates.
	 */
	public Page<ExpenseDto> getExpensesForManager(int page, int size, Long id){
		UserEntity u= userRepo.findById(id).orElse(null);
		Pageable pagination = PageRequest.of(page, size);
		Page<Expense>expenses=expenseRepo.findByMission_User_Id(id, pagination);
		Page<Expense>expensesUsers=expenseRepo.findByMission_User_Manager(u, pagination);
		Page<Expense> allExpenses= PageUtils.mergePages(expenses, expensesUsers, page, size);
		Page<ExpenseDto> expensesDto = allExpenses.map(expenseMapper::BeanToDto);
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

	/**Method to create a pdf from an expense
	 * @param id, the id of the expense for the pdf
	 * @param response, interface to send an http response with a pdf
	 */
public void exportExpense(Long id, HttpServletResponse response) throws IOException, DocumentException{
		Expense expense= expenseRepo.findById(id).orElse(null);
        if (expense == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Expense not found");
            return;
        }
		
		response.setHeader("Content-Disposition", "attachment; filename=\"NoteDeFrais.pdf\"");
		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		document.addTitle("Note de frais de " +expense.getMission().getUser().getFirstName()+" "+expense.getMission().getUser().getLastName());

		// Add logo to the upper right corner
		Image logo = Image.getInstance("backend/src/main/resources/logo.png");
		logo.setAbsolutePosition(document.right() - 100, document.top() - 50);  // Adjust position as needed
		logo.scaleToFit(100, 50);  // Adjust size as needed


		document.newPage();
		document.add(logo);
		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

		Paragraph p1 = new Paragraph("Note de frais de " +expense.getMission().getUser().getFirstName()+" "+expense.getMission().getUser().getLastName() , new Font(baseFont, 18.0f, 1, new BaseColor(0, 51, 80)));
		Paragraph p2 = new Paragraph ("Libelle de mission: " +expense.getMission().getLabel() , new Font(baseFont, 18.0f, 1, new BaseColor(0, 0, 0)));
		p1.setAlignment(Element.ALIGN_CENTER);
		p1.setSpacingAfter(20);
		p2.setAlignment(Element.ALIGN_CENTER);
		p2.setSpacingAfter(20);
		document.add(p1);
		document.add(p2);
		
		PdfPTable table = new PdfPTable(4);
		addTableHeader(table);
		addRows(table, expense);
		document.add(table);

		// Add black line and text at the bottom
		PdfContentByte cb = writer.getDirectContent();
		cb.setColorStroke(BaseColor.BLACK);
		cb.setLineWidth(1);
		cb.moveTo(document.left(), document.bottom() + 50);
		cb.lineTo(document.right(), document.bottom() + 50);
		cb.stroke();

		Font footerFont = new Font(baseFont, 12, Font.NORMAL, BaseColor.BLACK);
		Phrase footerText = new Phrase("Logiciel de gestion de mission", footerFont);
		ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, footerText, document.left(), document.bottom() + 35, 0);



		document.close();
		response.flushBuffer();
	}
	/**Method to create the table header on a pdf
	 * @param table, the pdfTable in construction
	 */
private void addTableHeader(PdfPTable table) {
    Stream.of("Date", "Type de frais", "Montant", "Tva")
      .forEach(columnTitle -> {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(new BaseColor(158, 217, 112));
        header.setBorderWidth(0);
		  header.setBorderWidthBottom(1);
		  header.setBorderColorBottom(BaseColor.BLACK);
        header.setPhrase(new Phrase(columnTitle));
        table.addCell(header);
    });
    
   
}
	/**Method to create the row on the table with the specific data
	 * @param table, the pdfTable in construction
	 */
private void addRows(PdfPTable table, Expense expense) {
	List<ExpenseLine> expenseLines= expense.getExpenseLines();
	for (int i =0; i<expenseLines.size(); i++) {

		createCell(table,expenseLines.get(i).getDate().toString());
		createCell(table,expenseLines.get(i).getExpenseType().getType());
		createCell(table,String.valueOf(expenseLines.get(i).getAmount()));
		createCell(table,String.valueOf(expenseLines.get(i).getTva()));
	}


}
	/**Method to create the cell with specific display
	 * @param table, the pdfTable in construction
	 * @param phrase, the string contain in this specific cell
	 */
	private void createCell(PdfPTable table, String phrase) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(new BaseColor(235, 236, 235));
		cell.setBorderWidth(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPhrase(new Phrase(phrase));
		table.addCell(cell);
	}
}
