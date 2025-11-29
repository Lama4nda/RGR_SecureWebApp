package com.example.secure.model;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    // Тут буде текст: "Хочу: Лижі, Екскурсії"
    private String userActivities;

    public Booking(Hotel hotel, User user, LocalDate checkInDate, LocalDate checkOutDate) {
        this.hotel = hotel;
        this.user = user;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }
}
