package com.example.secure.controller;

import com.example.secure.model.Hotel;
import com.example.secure.model.Resort;
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
public class HotelController {

    @Autowired private HotelRepo hotelRepo;
    @Autowired private ResortRepo resortRepo; // Потрібен, щоб вибрати курорт
    // --- CREATE ---
    @PostMapping("/addHotel")
    public String addHotel(@RequestParam String hotelName, 
                           @RequestParam int stars, 
                           @RequestParam int totalRooms,
                           @RequestParam Long resortId) {
        
        Resort resort = resortRepo.findById(resortId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resort Id:" + resortId));
        
        // Переконайтесь, що конструктор Hotel приймає totalRooms
        hotelRepo.save(new Hotel(hotelName, stars, totalRooms, resort)); 
        return "redirect:/dashboard";
    }

    // --- DELETE ---
    @GetMapping("/deleteHotel/{id}")
    public String deleteHotel(@PathVariable Long id) {
        hotelRepo.deleteById(id);
        return "redirect:/dashboard";
    }

    // --- EDIT ---
    @GetMapping("/editHotel/{id}")
    public String editHotel(@PathVariable Long id, Model model) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        
        model.addAttribute("hotel", hotel);
        model.addAttribute("allResorts", resortRepo.findAllByOrderByIdAsc());
        model.addAttribute("editType", "hotel");
        return "edit_page";
    }

    @PostMapping("/updateHotel")
    public String updateHotel(@RequestParam Long id, 
                              @RequestParam String name, 
                              @RequestParam int stars, 
                              @RequestParam int totalRooms, // Не забудьте це поле
                              @RequestParam Long resortId) {
        Hotel hotel = hotelRepo.findById(id).orElseThrow();
        Resort resort = resortRepo.findById(resortId).orElseThrow();
        
        hotel.setName(name);
        hotel.setStars(stars);
        hotel.setTotalRooms(totalRooms);
        hotel.setResort(resort);
        
        hotelRepo.save(hotel);
        return "redirect:/dashboard";
    }
}