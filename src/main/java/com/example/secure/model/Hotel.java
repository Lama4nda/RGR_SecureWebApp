package com.example.secure.model;

import javax.persistence.*;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int stars;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resort_id", nullable = false) // Ключ до таблиці курортів
    private Resort resort;

    // Конструктор за замовчуванням (необхідний для JPA)
    public Hotel() {
    }

    // Наш конструктор
    public Hotel(String name, int stars, Resort resort) {
        this.name = name;
        this.stars = stars;
        this.resort = resort;
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
    public int getStars() { 
        return stars; 
    }
    public void setStars(int stars) { 
        this.stars = stars; 
    }
    public Resort getResort() { 
        return resort; 
    }
    public void setResort(Resort resort) { 
        this.resort = resort; 
    }
}
