package com.example.secure.repo;

import com.example.secure.model.Booking;
import com.example.secure.model.User;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    
    // SQL логіка: Знайти всі бронювання для цього готелю, 
    // де дати перетинаються з бажаними датами нового бронювання.
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.hotel.id = :hotelId " +
           "AND (b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)")
    long countOverlappingBookings(@Param("hotelId") Long hotelId, 
                                  @Param("checkIn") LocalDate checkIn, 
                                  @Param("checkOut") LocalDate checkOut);
 // Рахує, скільки номерів зайнято САМЕ СЬОГОДНІ
 // Логіка: (Дата заїзду <= сьогодні) І (Дата виїзду > сьогодні)
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.hotel.id = :hotelId " +
           "AND b.checkInDate <= :today AND b.checkOutDate > :today")
    long countActiveBookings(@Param("hotelId") Long hotelId, 
                             @Param("today") LocalDate today);
 // НОВИЙ МЕТОД: Знайти всі бронювання користувача (сортуємо за датою заїзду)
    List<Booking> findByUserOrderByCheckInDateAsc(User user);
}