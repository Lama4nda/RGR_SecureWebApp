package com.example.secure.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "resorts")
public class Resort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Вказує на зв'язок "Багато-до-Одного"
    // 'fetch = FetchType.LAZY' - оптимізація (не завантажувати країну, поки не попросять)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false) // Назва колонки-ключа в БД
    private Country country;

    @OneToMany(mappedBy = "resort", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Hotel> hotels;

    // Конструктор за замовчуванням (необхідний для JPA)
    public Resort() {
    }

    // Наш конструктор
    public Resort(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    // --- Getters and Setters ---
    
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
    public Country getCountry() { 
        return country; 
    }
    public void setCountry(Country country) { 
        this.country = country; 
    }
    public Set<Hotel> getHotels() { 
        return hotels; 
    }
    public void setHotels(Set<Hotel> hotels) { 
        this.hotels = hotels; 
    }
}
