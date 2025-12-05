package com.example.secure;

import com.example.secure.controller.CountryController;
import com.example.secure.model.Country;
import com.example.secure.model.LocationType;
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

    // --- ТЕСТ 1: Додавання країни (Create) ---
    @Test
    void testAddCountry() {
        String countryName = "Україна";
        String locationTypeStr = "MOUNTAINS"; // Новий параметр: Тип місцевості

        // Викликаємо метод з двома параметрами (Назва, Тип)
        String viewName = countryController.addCountry(countryName, locationTypeStr);

        // Перевіряємо, що збереження відбулося
        verify(countryRepo, times(1)).save(any(Country.class));
        assertEquals("redirect:/dashboard", viewName);
    }

    // --- ТЕСТ 2: Видалення країни (Delete) ---
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
        // Використовуємо новий конструктор з LocationType
        Country mockCountry = new Country("Польща", LocationType.NEUTRAL);
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
        boolean newStatus = true; // isOpen
        String newLocationStr = "SEASIDE"; // Новий тип місцевості

        // Створюємо стару версію країни
        Country existingCountry = new Country("Стара Назва", LocationType.MOUNTAINS);
        existingCountry.setId(countryId);
        existingCountry.setOpen(false);

        when(countryRepo.findById(countryId)).thenReturn(Optional.of(existingCountry));

        // Викликаємо метод updateCountry з 4 параметрами: ID, Назва, Статус, Тип
        String viewName = countryController.updateCountry(countryId, newName, newStatus, newLocationStr);

        // Перевіряємо, що всі поля оновилися
        assertEquals(newName, existingCountry.getName());
        assertEquals(newStatus, existingCountry.isOpen());
        assertEquals(LocationType.SEASIDE, existingCountry.getLocationType()); // Перевірка зміни типу

        verify(countryRepo).save(existingCountry);
        assertEquals("redirect:/dashboard", viewName);
    }
}