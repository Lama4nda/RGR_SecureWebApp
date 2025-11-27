package com.example.secure.repo;

import com.example.secure.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CountryRepo extends JpaRepository<Country, Long> {
    
    // Spring Data JPA автоматично згенерує SQL-запит: "SELECT * FROM countries ORDER BY id ASC"
    List<Country> findAllByOrderByIdAsc();
}
