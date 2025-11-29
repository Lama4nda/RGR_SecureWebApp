package com.example.secure.controller;

import com.example.secure.model.Country;
import com.example.secure.model.Resort;
import com.example.secure.repo.CountryRepo;
import com.example.secure.repo.ResortRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResortController {

    @Autowired private ResortRepo resortRepo;
    @Autowired private CountryRepo countryRepo; // Потрібен, щоб вибрати країну

    // --- CREATE ---
    // Тепер можна ставити галочки активностей одразу при створенні
    @PostMapping("/addResort")
    public String addResort(@RequestParam String resortName, 
                            @RequestParam Long countryId,
                            // Параметри активностей
                            @RequestParam(defaultValue = "false") boolean hasSki,
                            @RequestParam(defaultValue = "false") boolean hasBeach,
                            @RequestParam(defaultValue = "false") boolean hasExcursions,
                            @RequestParam(defaultValue = "false") boolean hasSanatorium,
                            @RequestParam(defaultValue = "false") boolean hasEntertainment) {
        
        Country country = countryRepo.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid country Id:" + countryId));
        
        Resort resort = new Resort(resortName, country);
        
        // Встановлюємо активності
        resort.setHasSki(hasSki);
        resort.setHasBeach(hasBeach);
        resort.setHasExcursions(hasExcursions);
        resort.setHasSanatorium(hasSanatorium);
        resort.setHasEntertainment(hasEntertainment);
        
        resortRepo.save(resort);
        return "redirect:/dashboard";
    }

    // --- DELETE ---
    @GetMapping("/deleteResort/{id}")
    public String deleteResort(@PathVariable Long id) {
        resortRepo.deleteById(id);
        return "redirect:/dashboard";
    }

    // --- EDIT ---
    @GetMapping("/editResort/{id}")
    public String editResort(@PathVariable Long id, Model model) {
        Resort resort = resortRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        
        model.addAttribute("resort", resort);
        model.addAttribute("allCountries", countryRepo.findAllByOrderByIdAsc());
        model.addAttribute("editType", "resort");
        return "edit_page";
    }

 // --- UPDATE RESORT ---
    @PostMapping("/updateResort")
    public String updateResort(@RequestParam Long id, 
                               @RequestParam String name,
                               @RequestParam Long countryId,
                               // Статус відкрито/закрито
                               @RequestParam(defaultValue = "false") boolean isOpen,
                               // Нові параметри активностей
                               @RequestParam(defaultValue = "false") boolean hasSki,
                               @RequestParam(defaultValue = "false") boolean hasBeach,
                               @RequestParam(defaultValue = "false") boolean hasExcursions,
                               @RequestParam(defaultValue = "false") boolean hasSanatorium,
                               @RequestParam(defaultValue = "false") boolean hasEntertainment) {
        
        Resort resort = resortRepo.findById(id).orElseThrow();
        Country country = countryRepo.findById(countryId).orElseThrow();
        
        resort.setName(name);
        resort.setCountry(country);
        resort.setOpen(isOpen); 
        
        // Оновлюємо галочки активностей
        resort.setHasSki(hasSki);
        resort.setHasBeach(hasBeach);
        resort.setHasExcursions(hasExcursions);
        resort.setHasSanatorium(hasSanatorium);
        resort.setHasEntertainment(hasEntertainment);
        
        resortRepo.save(resort);
        return "redirect:/dashboard";
    }
}