package mk.ukim.finki.wp.kol2024g1.service.impl;

import mk.ukim.finki.wp.kol2024g1.model.Hotel;
import mk.ukim.finki.wp.kol2024g1.model.Reservation;
import mk.ukim.finki.wp.kol2024g1.model.RoomType;
import mk.ukim.finki.wp.kol2024g1.model.exceptions.InvalidHotelIdException;
import mk.ukim.finki.wp.kol2024g1.model.exceptions.InvalidReservationIdException;
import mk.ukim.finki.wp.kol2024g1.repository.HotelRepository;
import mk.ukim.finki.wp.kol2024g1.repository.ReservationRepository;
import mk.ukim.finki.wp.kol2024g1.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository, HotelRepository hotelRepository) {
        this.reservationRepository = reservationRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public List<Reservation> listAll() {
        return this.reservationRepository.findAll();
    }

    @Override
    public Reservation findById(Long id) {
        return this.reservationRepository.findById(id).orElseThrow(InvalidReservationIdException::new);
    }

    @Override
    public Reservation create(String guestName, LocalDate dateCreated, Integer daysOfStay, RoomType roomType, Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(InvalidHotelIdException::new);
        Reservation reservation = new Reservation(guestName,dateCreated,daysOfStay,roomType,hotel);

        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation update(Long id, String guestName, LocalDate dateCreated, Integer daysOfStay, RoomType roomType, Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(InvalidHotelIdException::new);
        Reservation reservation = reservationRepository.findById(id).orElseThrow(InvalidReservationIdException::new);

        reservation.setGuestName(guestName);
        reservation.setDateCreated(dateCreated);
        reservation.setDaysOfStay(daysOfStay);
        reservation.setRoomType(roomType);
        reservation.setHotel(hotel);

        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation delete(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(InvalidReservationIdException::new);
        reservationRepository.deleteById(id);

        return reservation;
    }

    @Override
    public Reservation extendStay(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(InvalidReservationIdException::new);

        int extendedDays = reservation.getDaysOfStay()+1;
        reservation.setDaysOfStay(extendedDays);

        return reservationRepository.save(reservation);
    }

    @Override
    public Page<Reservation> findPage(String guestName, RoomType roomType, Long hotel, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        if (guestName == null) guestName = "";

        if (roomType == null && hotel == null) {
            return reservationRepository.findAllByGuestNameContaining(guestName, pageable);
        } else if (roomType == null) {
            return reservationRepository.findAllByGuestNameContainingAndHotelId(guestName, hotel, pageable);
        } else if (hotel == null) {
            return reservationRepository.findAllByGuestNameContainingAndRoomType(guestName, roomType, pageable);
        } else {
            return reservationRepository.findAllByGuestNameContainingAndRoomTypeAndHotelId(guestName, roomType, hotel, pageable);
        }
    }


}
