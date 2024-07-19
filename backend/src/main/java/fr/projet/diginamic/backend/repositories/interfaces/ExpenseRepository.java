package fr.projet.diginamic.backend.repositories.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.projet.diginamic.backend.entities.Expense;

/** Repository of all Expense method to search from database*/
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	
	/**Method to get all expenses on the database
     * @return An arrayList of all expenses
     */
	Page<Expense> findAll(Pageable pagination);
	

}
