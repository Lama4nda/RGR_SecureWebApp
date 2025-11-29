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
     // --- Що є на курорті ---
    private boolean hasSki;          // Лижі/Зимовий спорт
    private boolean hasBeach;        // Море/Пляж
    private boolean hasExcursions;   // Екскурсії
    private boolean hasSanatorium;   // Санаторій/Лікування
    private boolean hasEntertainment; // Розваги
    // Вказує на зв'язок "Багато-до-Одного"
    // 'fetch = FetchType.LAZY' - оптимізація (не завантажувати країну, поки не попросять)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false) // Назва колонки-ключа в БД
    private Country country;

    @OneToMany(mappedBy = "resort", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Hotel> hotels;
    
    // Конструктор
    public Resort(String name, Country country) {
        this.name = name;
        this.country = country;
        this.isOpen = true;
    }
}
