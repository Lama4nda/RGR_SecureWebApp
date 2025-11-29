package com.example.secure.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity // Вказує, що це сутність (таблиця)
@Table(name = "countries") // Назва таблиці в БД
@Data 
@NoArgsConstructor
public class Country {

    @Id // Вказує, що це Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоінкремент (SERIAL)
    private Long id;

    @Column(nullable = false, unique = true) // Поле не може бути порожнім і є унікальним
    private String name;
    
    @Column(nullable = false)
    private boolean isOpen = true;

    // Вказує на зв'язок "Один-до-Багатьох"
    // 'mappedBy = "country"' означає, що поле 'country' в класі 'Resort' керує цим зв'язком
    // 'cascade = CascadeType.ALL' означає: якщо видалити країну, всі пов'язані курорти теж видаляться
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Resort> resorts;

    // Наш конструктор для зручності
    public Country(String name) {
        this.name = name;
        this.isOpen = true;
    }
}
