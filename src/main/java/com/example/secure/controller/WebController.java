package com.example.secure.controller; 

import com.example.secure.model.Hotel; // Не забудьте імпортувати модель Hotel
import com.example.secure.repo.CountryRepo;
import com.example.secure.repo.HotelRepo;
import com.example.secure.repo.ResortRepo;
import com.example.secure.repo.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller 
public class WebController {

    @Autowired private CountryRepo countryRepo;
    @Autowired private ResortRepo resortRepo;
    @Autowired private HotelRepo hotelRepo;
    @Autowired private BookingRepo bookingRepo;
    
    // === 1. ГОЛОВНА СТОРІНКА ===
    @GetMapping("/")
    public String home() {
        return "home"; 
    }

    // === 2. СТОРІНКА ЛОГІНУ ===
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    // === 3. ПАНЕЛЬ УПРАВЛІННЯ (DASHBOARD) - ОНОВЛЕНО ===
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 1. Завантажуємо списки країн і курортів (як було)
        model.addAttribute("countries", countryRepo.findAllByOrderByIdAsc());
        model.addAttribute("resorts", resortRepo.findAllByOrderByIdAsc());

        // 2. Отримуємо список всіх готелів
        List<Hotel> hotels = hotelRepo.findAllByOrderByIdAsc();
        model.addAttribute("hotels", hotels);

        // 3. --- НОВА ЛОГІКА: Рахуємо вільні місця ---
        
        // Створюємо мапу: ID готелю -> Кількість вільних кімнат
        Map<Long, Integer> freeRoomsMap = new HashMap<>();
        LocalDate today = LocalDate.now(); // Поточна дата

        for (Hotel hotel : hotels) {
            // Запитуємо у репозиторія, скільки активних бронювань є на сьогодні
            // (Переконайтеся, що метод countActiveBookings є у BookingRepo!)
            long occupied = bookingRepo.countActiveBookings(hotel.getId(), today);
            
            // Всього кімнат - зайняті = вільні
            int free = hotel.getTotalRooms() - (int) occupied;

            // Записуємо в мапу (Math.max(0, free) гарантує, що не буде від'ємних чисел)
            freeRoomsMap.put(hotel.getId(), Math.max(0, free));
        }

        // Передаємо цю мапу на сторінку
        model.addAttribute("freeRoomsMap", freeRoomsMap);

        return "index"; 
    }
}