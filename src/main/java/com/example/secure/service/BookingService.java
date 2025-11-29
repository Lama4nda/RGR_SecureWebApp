package com.example.secure.service;

import com.example.secure.model.Booking;
import com.example.secure.model.Hotel;
import com.example.secure.model.User; // Припускаємо, що User існує
import com.example.secure.repo.BookingRepo;
import com.example.secure.repo.HotelRepo;
import com.example.secure.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor // Lombok створить конструктор для всіх final полів
public class BookingService {

    private final BookingRepo bookingRepo;
    private final HotelRepo hotelRepo;
    private final UserRepository userRepo;

    @Transactional
    public void bookRoom(Long hotelId, Long userId, LocalDate checkIn, LocalDate checkOut, String activities) {
        // 1. Знаходимо готель
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Готель не знайдено: " + hotelId));
        
        // 2. Перевірка країни
        if (!hotel.getResort().getCountry().isOpen()) {
            throw new IllegalStateException("Бронювання неможливе: Кордони країни '" + 
                hotel.getResort().getCountry().getName() + "' зачинені.");
        }

        // 3. Перевірка курорту
        if (!hotel.getResort().isOpen()) {
            throw new IllegalStateException("Бронювання неможливе: Курорт '" + 
                hotel.getResort().getName() + "' тимчасово не приймає гостей.");
        }
        
        // 4. Перевірка наявності вільних місць
        // Запитуємо у репозиторія, скільки бронювань перетинаються з нашими датами
        long occupiedRooms = bookingRepo.countOverlappingBookings(hotelId, checkIn, checkOut);

        // Якщо зайнято стільки ж або більше, ніж всього кімнат -> помилка
        if (occupiedRooms >= hotel.getTotalRooms()) {
            throw new IllegalStateException("Всі номери зайняті на ці дати!");
        }

        // 5. Якщо місця є — створюємо бронювання
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        Booking booking = new Booking();
        booking.setHotel(hotel);
        booking.setUser(user);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setUserActivities(activities);

        bookingRepo.save(booking);
    }
    // Скасування броні
    @Transactional
    public void cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Бронювання не знайдено"));

        // Чи це бронювання цього користувача?
        if (!booking.getUser().getUsername().equals(username)) {
            throw new SecurityException("Ви не можете скасувати чуже бронювання!");
        }

        bookingRepo.delete(booking);
    }
}