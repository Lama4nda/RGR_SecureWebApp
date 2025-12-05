package com.example.secure.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "resorts")
@Data 
@NoArgsConstructor
public class Resort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private boolean isOpen = true;
    // --- СЕЗОННІСТЬ ---
    @Enumerated(EnumType.STRING) // Зберігаємо як текст ("SUMMER", "WINTER"...)
    @Column(nullable = false)
    private ResortSeason season;
     // --- Що є на курорті ---
    private boolean hasSki;          // Лижі/Зимовий спорт
    private boolean hasBeach;        // Море/Пляж
    private boolean hasExcursions;   // Екскурсії
    private boolean hasSanatorium;   // Санаторій/Лікування
    private boolean hasEntertainment; // Розваги
    // Вказує на зв'язок "Багато-до-Одного"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false) // Назва колонки-ключа в БД
    private Country country;

    @OneToMany(mappedBy = "resort", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Hotel> hotels;
    
    // Конструктор
    public Resort(String name, Country country, ResortSeason season) {
        this.name = name;
        this.country = country;
        this.season = season;
        this.isOpen = true;
    }
 // Цей метод автоматично запускається перед збереженням (save) або оновленням (update)
    @PrePersist
    @PreUpdate
    private void validateSeasonRules() {
        if (season == ResortSeason.SUMMER) {
            // Правило 1: Літній курорт зобов'язаний мати Море.
            if (!hasBeach) {
                throw new IllegalStateException("Помилка: Літній курорт '" + name + "' зобов'язаний мати Море/Пляж!");
            }
            // Правило 2: Літній курорт НЕ може мати Лижі.
            if (hasSki) {
                throw new IllegalStateException("Помилка: Літній курорт '" + name + "' не може мати Лижі!");
            }
        } 
        else if (season == ResortSeason.WINTER) {
            // Правило 1: Зимовий курорт зобов'язаний мати Лижі.
            if (!hasSki) {
                throw new IllegalStateException("Помилка: Зимовий курорт '" + name + "' зобов'язаний мати Лижі!");
            }
            // Правило 2: Зимовий курорт НЕ може мати Море.
            if (hasBeach) {
                throw new IllegalStateException("Помилка: Зимовий курорт '" + name + "' не може мати Море/Пляж!");
            }
        } 
        else if (season == ResortSeason.ALL_SEASON) {
            // Універсальний: Не може бути ні моря, ні лиж (щоб не було прив'язки до сезону).
            if (hasBeach || hasSki) {
                throw new IllegalStateException("Помилка: Універсальний курорт '" + name + "' не повинен мати сезонних атрибутів (Моря або Лиж).");
            }
        }
     // 2. ПЕРЕВІРКА: СУМІСНІСТЬ З МІСЦЕВІСТЮ КРАЇНИ
        if (country != null) { // Перевіряємо, чи встановлена країна
            LocationType locType = country.getLocationType();

            // А) МІСТО В ГОРАХ
            if (locType == LocationType.MOUNTAINS) {
                if (season == ResortSeason.SUMMER) {
                    throw new IllegalStateException("Географічна помилка: У гірській місцевості ('" + country.getName() + "') не можна будувати Літній курорт!");
                }
            }
            
            // Б) МІСТО У МОРЯ
            else if (locType == LocationType.SEASIDE) {
                if (season == ResortSeason.WINTER) {
                    throw new IllegalStateException("Географічна помилка: На морському узбережжі ('" + country.getName() + "') не можна будувати Зимовий курорт!");
                }
            }
            
            // В) НЕЙТРАЛЬНЕ МІСТО (Немає гір, немає моря)
            else if (locType == LocationType.NEUTRAL) {
                if (season == ResortSeason.SUMMER || season == ResortSeason.WINTER) {
                    throw new IllegalStateException("Географічна помилка: У місті без гір та моря ('" + country.getName() + "') можливі лише Універсальні курорти!");
                }
            }
        }
    }
}
