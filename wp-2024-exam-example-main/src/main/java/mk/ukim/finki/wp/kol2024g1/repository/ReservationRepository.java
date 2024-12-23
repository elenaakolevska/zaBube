package mk.ukim.finki.wp.kol2024g1.repository;

import mk.ukim.finki.wp.kol2024g1.model.Reservation;
import mk.ukim.finki.wp.kol2024g1.model.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findAllByGuestNameContainingAndRoomTypeAndHotelId(
            String guestName, RoomType roomType, Long hotelId, Pageable pageable);

    Page<Reservation> findAllByGuestNameContaining(String guestName, Pageable pageable);

    Page<Reservation> findAllByGuestNameContainingAndHotelId(String guestName, Long hotel, Pageable pageable);

    Page<Reservation> findAllByGuestNameContainingAndRoomType(String guestName, RoomType roomType, Pageable pageable);
}
