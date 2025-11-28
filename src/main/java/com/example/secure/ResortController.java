package com.example.secure;

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
    @PostMapping("/addResort")
    public String addResort(@RequestParam String resortName, @RequestParam Long countryId) {
        Country country = countryRepo.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid country Id:" + countryId));
        
        resortRepo.save(new Resort(resortName, country));
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

    // --- UPDATE ---
    @PostMapping("/updateResort")
    public String updateResort(@RequestParam Long id, @RequestParam String name, @RequestParam Long countryId) {
        Resort resort = resortRepo.findById(id).orElseThrow();
        Country country = countryRepo.findById(countryId).orElseThrow();
        
        resort.setName(name);
        resort.setCountry(country);
        resortRepo.save(resort);
        return "redirect:/dashboard";
    }
}