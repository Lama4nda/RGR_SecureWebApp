package com.example.secure;

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
    private CountryRepo countryRepo; // Створюємо "фейковий" репозиторій

    @Mock
    private Model model; // Створюємо "фейкову" модель для передачі даних у view

    @InjectMocks
    private CountryController countryController; // Вставляємо моки в наш контролер

    @BeforeEach
    void setUp() {
        // Ініціалізація моків перед кожним тестом
        MockitoAnnotations.openMocks(this);
    }

    // --- ТЕСТ 1: Додавання країни (Create) ---
    @Test
    void testAddCountry() {
        String countryName = "Україна";

        // Викликаємо метод контролера
        String viewName = countryController.addCountry(countryName);

        // Перевіряємо, що репозиторій викликав метод save один раз
        verify(countryRepo, times(1)).save(any(Country.class));

        // Перевіряємо, що метод повернув правильний редірект
        assertEquals("redirect:/dashboard", viewName);
    }

    // --- ТЕСТ 2: Видалення країни (Delete) ---
    @Test
    void testDeleteCountry() {
        Long countryId = 1L;

        // Викликаємо метод контролера
        String viewName = countryController.deleteCountry(countryId);

        // Перевіряємо, що репозиторій викликав deleteById з правильним ID
        verify(countryRepo, times(1)).deleteById(countryId);

        // Перевіряємо редірект
        assertEquals("redirect:/dashboard", viewName);
    }

    // --- ТЕСТ 3: Відкриття форми редагування (Edit - Успіх) ---
    @Test
    void testEditCountry_Success() {
        Long countryId = 1L;
        Country mockCountry = new Country("Польща");
        mockCountry.setId(countryId);

        // Навчаємо мок: коли запитають findById(1), поверни нашу країну
        when(countryRepo.findById(countryId)).thenReturn(Optional.of(mockCountry));

        // Викликаємо метод
        String viewName = countryController.editCountry(countryId, model);

        // Перевіряємо, що в модель додали атрибути
        verify(model).addAttribute("country", mockCountry);
        verify(model).addAttribute("editType", "country");

        // Перевіряємо, що повернулася правильна сторінка
        assertEquals("edit_page", viewName);
    }

    // --- ТЕСТ 4: Редагування неіснуючої країни (Edit - Помилка) ---
    @Test
    void testEditCountry_NotFound() {
        Long countryId = 99L;

        // Навчаємо мок: коли запитають findById(99), поверни порожній Optional
        when(countryRepo.findById(countryId)).thenReturn(Optional.empty());

        // Перевіряємо, що метод викине помилку IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            countryController.editCountry(countryId, model);
        });
    }

    // --- ТЕСТ 5: Оновлення країни (Update) ---
    @Test
    void testUpdateCountry() {
        Long countryId = 1L;
        String newName = "Нова Назва";
        Country existingCountry = new Country("Стара Назва");
        existingCountry.setId(countryId);

        // Навчаємо мок знайти країну
        when(countryRepo.findById(countryId)).thenReturn(Optional.of(existingCountry));

        // Викликаємо метод оновлення
        String viewName = countryController.updateCountry(countryId, newName);

        // Перевіряємо, що ім'я змінилося
        assertEquals(newName, existingCountry.getName());

        // Перевіряємо, що викликався save
        verify(countryRepo).save(existingCountry);

        // Перевіряємо редірект
        assertEquals("redirect:/dashboard", viewName);
    }
}