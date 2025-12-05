package com.example.secure.controller;

import com.example.secure.model.Country;
import com.example.secure.model.LocationType;
import com.example.secure.repo.CountryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CountryController {

    @Autowired
    private CountryRepo countryRepo;

 // --- CREATE ---
    @PostMapping("/addCountry")
    public String addCountry(@RequestParam String countryName,
                             @RequestParam String locationTypeStr) { // Отримуємо тип як рядок
        
        // Перетворюємо рядок у Enum (MOUNTAINS, SEASIDE, NEUTRAL)
        LocationType type = LocationType.valueOf(locationTypeStr);
        
        // Використовуємо оновлений конструктор Country
        countryRepo.save(new Country(countryName, type));
        
        return "redirect:/dashboard";
    }

    // --- DELETE ---
    @GetMapping("/deleteCountry/{id}")
    public String deleteCountry(@PathVariable Long id) {
        countryRepo.deleteById(id);
        return "redirect:/dashboard";
    }

    // --- EDIT ---
    @GetMapping("/editCountry/{id}")
    public String editCountry(@PathVariable Long id, Model model) {
        Country country = countryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        
        model.addAttribute("country", country);
        model.addAttribute("editType", "country");
        // Можна передати values() enum-а, якщо ви хочете генерувати select динамічно, 
        // але ваш HTML вже має хардкод опцій, тому це не обов'язково.
        return "edit_page";
    }

    // --- UPDATE ---
    @PostMapping("/updateCountry")
    public String updateCountry(@RequestParam Long id, 
                                @RequestParam String name,
                                @RequestParam(defaultValue = "false") boolean isOpen,
                                @RequestParam String locationTypeStr) { // Отримуємо тип
        
        Country country = countryRepo.findById(id).orElseThrow();
        
        country.setName(name);
        country.setOpen(isOpen);
        
        // Оновлюємо тип місцевості
        country.setLocationType(LocationType.valueOf(locationTypeStr));
        
        countryRepo.save(country);
        return "redirect:/dashboard";
    }
}