package com.example.secure.model;

import javax.persistence.*;
import java.util.Set;

@Entity // Вказує, що це сутність (таблиця)
@Table(name = "countries") // Назва таблиці в БД
public class Country {

    @Id // Вказує, що це Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоінкремент (SERIAL)
    private Long id;

    @Column(nullable = false, unique = true) // Поле не може бути порожнім і є унікальним
    private String name;

    // Вказує на зв'язок "Один-до-Багатьох"
    // 'mappedBy = "country"' означає, що поле 'country' в класі 'Resort' керує цим зв'язком
    // 'cascade = CascadeType.ALL' означає: якщо видалити країну, всі пов'язані курорти теж видаляться
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Resort> resorts;

    // Конструктор за замовчуванням (необхідний для JPA/Hibernate)
    public Country() {
    }

    // Наш конструктор для зручності
    public Country(String name) {
        this.name = name;
    }

    // --- Getters and Setters ---
    // (Вони необхідні для JPA)
    
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }
    public Set<Resort> getResorts() { 
        return resorts; 
    }
    public void setResorts(Set<Resort> resorts) { 
        this.resorts = resorts; 
    }
}
