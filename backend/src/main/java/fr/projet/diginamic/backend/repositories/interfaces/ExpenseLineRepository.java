package fr.projet.diginamic.backend.repositories.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.projet.diginamic.backend.entities.ExpenseLine;

/** Repository of all ExpenseLine method to search from database*/
@Repository
public interface ExpenseLineRepository extends JpaRepository<ExpenseLine, Long> {
	
	/**Method to get all expenseLines on the database
     * @return An arrayList of all expenseLines
     */
	Page<ExpenseLine> findAll(Pageable pagination);
	

}
