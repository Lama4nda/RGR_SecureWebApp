package com.example.secure.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotels")
@Data 
@NoArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int stars;
    private int totalRooms;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resort_id", nullable = false) // Ключ до таблиці курортів
    private Resort resort;

    // Наш конструктор
    public Hotel(String name, int stars, int totalRooms, Resort resort) {
        this.name = name;
        this.stars = stars;
        this.totalRooms = totalRooms;
        this.resort = resort;
    }
}
