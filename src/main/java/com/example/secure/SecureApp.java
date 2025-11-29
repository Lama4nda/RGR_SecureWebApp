package com.example.secure;

import com.example.secure.model.Country;
import com.example.secure.model.Hotel;
import com.example.secure.model.Resort;
import com.example.secure.model.User;
import com.example.secure.repo.CountryRepo;
import com.example.secure.repo.HotelRepo;
import com.example.secure.repo.ResortRepo;
import com.example.secure.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecureApp {

    private static final Logger log = LoggerFactory.getLogger(SecureApp.class);

    public static void main(String[] args) {
        SpringApplication.run(SecureApp.class, args);
        log.info("--- ВЕБ-ЗАСТОСУНОК ЗАПУЩЕНО ---");
        log.info("Відкрийте браузер і перейдіть на: http://localhost:8080");
    }

    @Bean
    public CommandLineRunner loadInitialData(CountryRepo countryRepo, 
                                             ResortRepo resortRepo, 
                                             HotelRepo hotelRepo,
                                             UserRepository userRepo,
                                             PasswordEncoder passwordEncoder) {
        return (args) -> {
            
            // 1. Створення користувачів (якщо їх немає)
            if (userRepo.count() == 0) {
                log.info("[INIT] Створення користувачів...");
                
                // Адмін: логін 'admin', пароль 'admin', роль 'ADMIN'
                User admin = new User("admin", passwordEncoder.encode("admin"), "ADMIN");
                userRepo.save(admin);

                // Юзер: логін 'user', пароль 'user', роль 'USER'
                User user = new User("user", passwordEncoder.encode("user"), "USER");
                userRepo.save(user);
                
                log.info("[INIT] Користувачі створені: admin/admin та user/user");
            } else {
                log.info("[INIT] Користувачі вже існують.");
            }

            // 2. Створення даних (якщо база порожня)
            if (countryRepo.count() == 0) {
                log.info("[INIT] База даних порожня. Починаємо заповнення...");

                // --- Створюємо Країни ---
                Country ukraine = new Country("Україна");
                Country turkey = new Country("Туреччина");
                Country egypt = new Country("Єгипет");
                
                countryRepo.save(ukraine);
                countryRepo.save(turkey);
                countryRepo.save(egypt);
                log.info("[INIT] Країни додано.");

                // --- Створюємо Курорти ---
                Resort bukovel = new Resort("Буковель", ukraine);
                Resort antalya = new Resort("Анталія", turkey);
                Resort sharm = new Resort("Шарм-ель-Шейх", egypt);
                
                resortRepo.save(bukovel);
                resortRepo.save(antalya);
                resortRepo.save(sharm);
                log.info("[INIT] Курорти додано.");

                // --- Створюємо Готелі ---
                // new Hotel(Назва, Зірки, КІМНАТИ, Курорт)
                Hotel radisson = new Hotel("Radisson Blu", 5, 50, bukovel); 
                Hotel rixos = new Hotel("Rixos Premium", 5, 100, antalya);
                Hotel savoy = new Hotel("Savoy", 5, 70, sharm);

                hotelRepo.save(radisson);
                hotelRepo.save(rixos);
                hotelRepo.save(savoy);
                log.info("[INIT] Готелі додано.");
            } else {
                log.info("[INIT] База даних вже заповнена.");
            }
            
            log.info("--- [INIT] Початкове завантаження повністю завершено ---");
        };
    }
}