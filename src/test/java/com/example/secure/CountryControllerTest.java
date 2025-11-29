package com.example.secure;

import com.example.secure.controller.CountryController;
import com.example.secure.model.Country;
import com.example.secure.repo.CountryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CountryControllerTest {

    @Mock
    private CountryRepo countryRepo;

    @Mock
    private Model model;

    @InjectMocks
    private CountryController countryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- ТЕСТ 1: Додавання країни ---
    @Test
    void testAddCountry() {
        String countryName = "Україна";

        // addCountry зазвичай приймає тільки ім'я (isOpen = true за замовчуванням в конструкторі)
        String viewName = countryController.addCountry(countryName);

        verify(countryRepo, times(1)).save(any(Country.class));
        assertEquals("redirect:/dashboard", viewName);
    }

    // --- ТЕСТ 2: Видалення країни ---
    @Test
    void testDeleteCountry() {
        Long countryId = 1L;

        String viewName = countryController.deleteCountry(countryId);

        verify(countryRepo, times(1)).deleteById(countryId);
        assertEquals("redirect:/dashboard", viewName);
    }

    // --- ТЕСТ 3: Відкриття форми редагування (Edit - Успіх) ---
    @Test
    void testEditCountry_Success() {
        Long countryId = 1L;
        Country mockCountry = new Country("Польща");
        mockCountry.setId(countryId);

        when(countryRepo.findById(countryId)).thenReturn(Optional.of(mockCountry));

        String viewName = countryController.editCountry(countryId, model);

        verify(model).addAttribute("country", mockCountry);
        verify(model).addAttribute("editType", "country");
        assertEquals("edit_page", viewName);
    }

    // --- ТЕСТ 4: Редагування неіснуючої країни (Edit - Помилка) ---
    @Test
    void testEditCountry_NotFound() {
        Long countryId = 99L;

        when(countryRepo.findById(countryId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            countryController.editCountry(countryId, model);
        });
    }

    // --- ТЕСТ 5: Оновлення країни (Update) ---
    @Test
    void testUpdateCountry() {
        Long countryId = 1L;
        String newName = "Нова Назва";
        boolean newStatus = true; // <--- НОВИЙ ПАРАМЕТР (isOpen)

        Country existingCountry = new Country("Стара Назва");
        existingCountry.setId(countryId);
        existingCountry.setOpen(false); // Спочатку закрито

        when(countryRepo.findById(countryId)).thenReturn(Optional.of(existingCountry));

        // Викликаємо метод з ТРЬОМА аргументами (ID, Name, isOpen)
        String viewName = countryController.updateCountry(countryId, newName, newStatus);

        // Перевіряємо, що дані змінилися
        assertEquals(newName, existingCountry.getName());
        assertEquals(newStatus, existingCountry.isOpen()); // Перевіряємо, що статус оновився

        verify(countryRepo).save(existingCountry);
        assertEquals("redirect:/dashboard", viewName);
    }
}