package com.example.secure.repo;

import com.example.secure.model.Resort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResortRepo extends JpaRepository<Resort, Long> {

    List<Resort> findAllByOrderByIdAsc();
}
