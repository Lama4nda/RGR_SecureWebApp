package com.example.secure; 

import com.example.secure.repo.CountryRepo;
import com.example.secure.repo.HotelRepo;
import com.example.secure.repo.ResortRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class WebController {

    // Підключаємо всі репозиторії
    @Autowired
    private CountryRepo countryRepo;
    @Autowired
    private ResortRepo resortRepo;
    @Autowired
    private HotelRepo hotelRepo;
 // === 1. ГОЛОВНА СТОРІНКА (ПРИВІТАННЯ) ===
    // Доступна всім (налаштовано в SecurityConfig)
    @GetMapping("/")
    public String home() {
        return "home"; // Повертає шаблон home.html
    }

    // === 2. СТОРІНКА ЛОГІНУ ===
    // Доступна всім
    @GetMapping("/login")
    public String login() {
        return "login"; // Повертає шаблон login.html
    }

    // === 3. ПАНЕЛЬ УПРАВЛІННЯ (DASHBOARD) ===
    // Доступна тільки авторизованим користувачам (USER або ADMIN)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Завантажуємо всі дані для відображення таблиць
        model.addAttribute("countries", countryRepo.findAllByOrderByIdAsc());
        model.addAttribute("resorts", resortRepo.findAllByOrderByIdAsc());
        model.addAttribute("hotels", hotelRepo.findAllByOrderByIdAsc());
        return "index"; // Повертає сторінку index.html з даними
    }
}