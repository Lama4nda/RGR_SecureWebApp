package com.example.secure.controller;

import com.example.secure.model.ResortSeason;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Для передачі помилок

@Controller
public class ResortController {

    @Autowired private ResortRepo resortRepo;
    @Autowired private CountryRepo countryRepo; // Потрібен, щоб вибрати країну

    // --- CREATE ---
    @PostMapping("/addResort")
    public String addResort(@RequestParam String resortName, 
                            @RequestParam Long countryId,
                            @RequestParam String seasonStr,
                            @RequestParam(defaultValue = "false") boolean hasSki,
                            @RequestParam(defaultValue = "false") boolean hasBeach,
                            @RequestParam(defaultValue = "false") boolean hasExcursions,
                            @RequestParam(defaultValue = "false") boolean hasSanatorium,
                            @RequestParam(defaultValue = "false") boolean hasEntertainment,
                            RedirectAttributes redirectAttributes) {
        
        // Створюємо об'єкт тут, щоб він був доступний і в try, і в catch
        Resort resort = new Resort();

        try {
            Country country = countryRepo.findById(countryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid country Id:" + countryId));
            
            // Заповнюємо даними з форми
            resort.setName(resortName);
            resort.setCountry(country);
            resort.setSeason(ResortSeason.valueOf(seasonStr));
            
            resort.setHasSki(hasSki);
            resort.setHasBeach(hasBeach);
            resort.setHasExcursions(hasExcursions);
            resort.setHasSanatorium(hasSanatorium);
            resort.setHasEntertainment(hasEntertainment);
            
            // Спроба збереження
            resortRepo.saveAndFlush(resort);
            
        } catch (Exception e) {
            // --- ЛОГІКА ПОШУКУ ПОМИЛКИ ---
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            String cleanMessage = rootCause.getMessage();
            if (cleanMessage == null || cleanMessage.isEmpty()) {
                cleanMessage = "Помилка при створенні курорту.";
            }
            // 1. Передаємо текст помилки
            redirectAttributes.addFlashAttribute("errorMessage", cleanMessage);
            // 2. !!! ПЕРЕДАЄМО ЗАПОВНЕНИЙ ОБ'ЄКТ НАЗАД, ЩОБ ВІДНОВИТИ ПОЛЯ !!!
            redirectAttributes.addFlashAttribute("resortForm", resort);
        }
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
                               @RequestParam String seasonStr, // -- сезони
                               @RequestParam(defaultValue = "false") boolean isOpen,
                               @RequestParam(defaultValue = "false") boolean hasSki,
                               @RequestParam(defaultValue = "false") boolean hasBeach,
                               @RequestParam(defaultValue = "false") boolean hasExcursions,
                               @RequestParam(defaultValue = "false") boolean hasSanatorium,
                               @RequestParam(defaultValue = "false") boolean hasEntertainment,
                               Model model) { // 1. Додаємо Model
        
        // Знаходимо об'єкти
        Resort resort = resortRepo.findById(id).orElseThrow();
        Country country = countryRepo.findById(countryId).orElseThrow();
        
        // Оновлюємо дані об'єкта в пам'яті (щоб якщо буде помилка, користувач бачив свої зміни)
        resort.setName(name);
        resort.setCountry(country);
        resort.setOpen(isOpen);
        resort.setSeason(ResortSeason.valueOf(seasonStr));

        resort.setHasSki(hasSki);
        resort.setHasBeach(hasBeach);
        resort.setHasExcursions(hasExcursions);
        resort.setHasSanatorium(hasSanatorium);
        resort.setHasEntertainment(hasEntertainment);
        
        try {
            // 2. Спроба збереження. Тут спрацює перевірка @PreUpdate
            resortRepo.saveAndFlush(resort); // Використовуємо saveAndFlush для миттєвої перевірки
            
            return "redirect:/dashboard"; // Якщо успіх -> повертаемось

        } catch (Exception e) { 
            // 3. ЯКЩО ПОМИЛКА (наприклад, "Зимовий + Море")
            
        	// 1. Шукаємо найглибшу (кореневу) помилку
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            // 2. Беремо текст саме з неї
            String cleanMessage = rootCause.getMessage();
            // 3. Якщо текст раптом null, пишемо стандартне
            if (cleanMessage == null || cleanMessage.isEmpty()) {
                cleanMessage = "Помилка збереження даних";
            }
            // Передаємо чисте повідомлення
            model.addAttribute("errorMessage", cleanMessage);
            
            // Повертаємо об'єкт курорту з тими налаштуваннями, які вибрав користувач
            model.addAttribute("resort", resort);
            // Заново завантажуємо список країн, бо edit_page потребує його для <select>
            model.addAttribute("allCountries", countryRepo.findAllByOrderByIdAsc());
            // Вказуємо тип редагування, щоб відкрився правильний блок
            model.addAttribute("editType", "resort");
            // Залишаємось на тій самій сторінці
            return "edit_page";
        }
    }
}