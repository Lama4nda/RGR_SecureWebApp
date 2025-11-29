package com.example.secure.controller;

import com.example.secure.model.Booking;
import com.example.secure.model.Hotel;
import com.example.secure.model.User;
import com.example.secure.repo.BookingRepo;
import com.example.secure.repo.HotelRepo;
import com.example.secure.repo.UserRepository;
import com.example.secure.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final HotelRepo hotelRepo;
    private final UserRepository userRepo;
    private final BookingRepo bookingRepo;

 // 1. Показати сторінку бронювання для конкретного готелю
    @GetMapping("/book/{hotelId}")
    public String showBookingPage(@PathVariable Long hotelId, Model model) {
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Готель не знайдено"));
        
        model.addAttribute("hotel", hotel);
        return "booking_page";
    }

    // 2. Обробити натискання кнопки "Забронювати" (ОНОВЛЕНО)
    @PostMapping("/book/submit")
    public String submitBooking(
            @RequestParam Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            // НОВИЙ ПАРАМЕТР: Список активностей (може бути null, якщо нічого не обрано)
            @RequestParam(required = false) List<String> activities,
            @AuthenticationPrincipal UserDetails currentUser, 
            RedirectAttributes redirectAttributes) {

        try {
            User user = userRepo.findByUsername(currentUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
            
            // ЛОГІКА: Перетворюємо список ["Лижі", "СПА"] у рядок "Лижі, СПА"
            // Якщо список null (користувач нічого не обрав), записуємо "Нічого не обрано"
            String activitiesString = (activities != null && !activities.isEmpty()) 
                    ? String.join(", ", activities) 
                    : "Без додаткових побажань";

            // Викликаємо оновлений метод сервісу (додали 5-й аргумент)
            bookingService.bookRoom(hotelId, user.getId(), checkIn, checkOut, activitiesString);

            redirectAttributes.addFlashAttribute("successMessage", "Успішно заброньовано!");
            
            // Перенаправляємо на сторінку "Мої бронювання", щоб користувач побачив результат
            return "redirect:/my-bookings"; 

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/book/" + hotelId;
        }
    }

    // 3. СТОРІНКА "МОЇ БРОНЮВАННЯ"
    @GetMapping("/my-bookings")
    public String showMyBookings(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepo.findByUsername(currentUser.getUsername())
                .orElseThrow();
        
        List<Booking> bookings = bookingRepo.findByUserOrderByCheckInDateAsc(user);
        model.addAttribute("bookings", bookings);
        
        return "my_bookings";
    }

    // 4. ДІЯ СКАСУВАННЯ
    @PostMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, 
                                @AuthenticationPrincipal UserDetails currentUser,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Бронювання скасовано.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/my-bookings";
    }
}