package com.example.secure.repo;

import com.example.secure.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HotelRepo extends JpaRepository<Hotel, Long> {
    
    List<Hotel> findAllByOrderByIdAsc();
}
