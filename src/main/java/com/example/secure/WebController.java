package com.example.secure; 

import com.example.secure.model.Country;
import com.example.secure.model.Hotel;
import com.example.secure.model.Resort;
import com.example.secure.repo.CountryRepo;
import com.example.secure.repo.HotelRepo;
import com.example.secure.repo.ResortRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    // --- ОБРОБНИКИ ДЛЯ СТВОРЕННЯ (CREATE) ---

    @PostMapping("/addCountry")
    public String addCountry(@RequestParam String countryName) {
        // Spring Data JPA автоматично створює безпечний INSERT
        countryRepo.save(new Country(countryName));
        return "redirect:/dashboard"; // Перезавантажуємо головну сторінку
    }

    @PostMapping("/addResort")
    public String addResort(@RequestParam String resortName, 
                            @RequestParam Long countryId) {
        // Знаходимо країну, до якої прив'язати курорт
        Country country = countryRepo.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid country Id:" + countryId));
        
        resortRepo.save(new Resort(resortName, country));
        return "redirect:/dashboard"; // Перезавантажуємо головну сторінку
    }

    @PostMapping("/addHotel")
    public String addHotel(@RequestParam String hotelName, 
                           @RequestParam int stars, 
                           @RequestParam Long resortId) {
        // Знаходимо курорт, до якого прив'язати готель
        Resort resort = resortRepo.findById(resortId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resort Id:" + resortId));
        
        hotelRepo.save(new Hotel(hotelName, stars, resort));
        return "redirect:/dashboard"; // Перезавантажуємо головну сторінку
    }

    // --- ОБРОБНИКИ ДЛЯ ВИДАЛЕННЯ (DELETE) ---

    @GetMapping("/deleteCountry/{id}")
    public String deleteCountry(@PathVariable Long id) {
        // Spring Data JPA автоматично створює безпечний DELETE
        countryRepo.deleteById(id);
        return "redirect:/dashboard"; // Перезавантажуємо головну сторінку
    }

    @GetMapping("/deleteResort/{id}")
    public String deleteResort(@PathVariable Long id) {
        resortRepo.deleteById(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/deleteHotel/{id}")
    public String deleteHotel(@PathVariable Long id) {
        hotelRepo.deleteById(id);
        return "redirect:/dashboard";
    }
    
 // --- МЕТОДИ РЕДАГУВАННЯ (EDIT & UPDATE) ---

 // 1. Редагування КРАЇНИ
    @GetMapping("/editCountry/{id}")
    public String editCountry(@PathVariable Long id, Model model) {
        Country country = countryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        
        model.addAttribute("country", country);
        model.addAttribute("editType", "country"); // Вказуємо, ЩО ми редагуємо
        return "edit_page"; // Всі йдуть на одну сторінку!
    }

    @PostMapping("/updateCountry")
    public String updateCountry(@RequestParam Long id, @RequestParam String name) {
        Country country = countryRepo.findById(id).orElseThrow();
        country.setName(name);
        countryRepo.save(country);
        return "redirect:/dashboard";
    }

    // 2. Редагування КУРОРТУ
    @GetMapping("/editResort/{id}")
    public String editResort(@PathVariable Long id, Model model) {
        Resort resort = resortRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        
        model.addAttribute("resort", resort);
        // Треба передати список всіх країн, щоб можна було змінити країну курорту
        model.addAttribute("allCountries", countryRepo.findAllByOrderByIdAsc());
        model.addAttribute("editType", "resort"); // Вказуємо тип
        return "edit_page";
    }

    @PostMapping("/updateResort")
    public String updateResort(@RequestParam Long id, @RequestParam String name, @RequestParam Long countryId) {
        Resort resort = resortRepo.findById(id).orElseThrow();
        Country country = countryRepo.findById(countryId).orElseThrow();
        
        resort.setName(name);
        resort.setCountry(country);
        resortRepo.save(resort);
        return "redirect:/dashboard";
    }

    // 3. Редагування ГОТЕЛЮ
    @GetMapping("/editHotel/{id}")
    public String editHotel(@PathVariable Long id, Model model) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        
        model.addAttribute("hotel", hotel);
        // Передаємо список курортів для випадаючого списку
        model.addAttribute("allResorts", resortRepo.findAllByOrderByIdAsc());
        model.addAttribute("editType", "hotel"); // Вказуємо тип
        return "edit_page";
    }

    @PostMapping("/updateHotel")
    public String updateHotel(@RequestParam Long id, @RequestParam String name, 
                              @RequestParam int stars, @RequestParam Long resortId) {
        Hotel hotel = hotelRepo.findById(id).orElseThrow();
        Resort resort = resortRepo.findById(resortId).orElseThrow();
        
        hotel.setName(name);
        hotel.setStars(stars);
        hotel.setResort(resort);
        hotelRepo.save(hotel);
        return "redirect:/dashboard";
    }
}