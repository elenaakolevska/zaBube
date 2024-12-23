package mk.ukim.finki.wp.kol2024g1.web;

import mk.ukim.finki.wp.kol2024g1.model.Hotel;
import mk.ukim.finki.wp.kol2024g1.model.Reservation;
import mk.ukim.finki.wp.kol2024g1.model.RoomType;
import mk.ukim.finki.wp.kol2024g1.service.HotelService;
import mk.ukim.finki.wp.kol2024g1.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ReservationsController {

    private final ReservationService reservationService;
    private final HotelService hotelService;

    public ReservationsController(ReservationService reservationService, HotelService hotelService) {
        this.reservationService = reservationService;
        this.hotelService = hotelService;
    }

    /**
     * This method should use the "list.html" template to display all reservations.
     * The method should be mapped on paths '/' and '/reservations'.
     * The arguments that this method takes are optional and can be 'null'.
     * In the case when the arguments are not passed (both are 'null') all reservations should be displayed.
     * If some, or all of the arguments are not 'null', the filtered reservations that are the result of the call
     * listAll method from the ReservationService should be displayed.
     * If you want to return a paginated result, you should also pass the page number and the page size as arguments.
     *
     * @param guestName
     * @param roomType
     * @param hotel
     * @param pageNum
     * @param pageSize
     * @return The view "list.html".
     */


    @GetMapping({"/", "/reservations"})

    public String listAll(@RequestParam(required = false) String guestName,
                          @RequestParam(required = false) RoomType roomType,
                          @RequestParam(required = false) Long hotel,
                          @RequestParam(defaultValue = "0") Integer pageNum,
                          @RequestParam(defaultValue = "3") Integer pageSize,
                          Model model) {

        Page<Reservation> reservationPage;

        if (guestName == null && roomType == null && hotel == null) {
            // Ако нема филтри, врати сите резервации
            reservationPage = reservationService.findPage(null, null, null, pageNum, pageSize);
        } else {
            // Ако има барем еден филтер, врати филтрирани резервации
            reservationPage = reservationService.findPage(guestName, roomType, hotel, pageNum, pageSize);
        }

        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", reservationPage.getTotalPages());
        model.addAttribute("guestName", guestName);
        model.addAttribute("roomType", roomType);
        model.addAttribute("hotel", hotel);
        model.addAttribute("roomTypes", RoomType.values());

        return "list.html";
    }

    /**
     * This method should display the "form.html" template.
     * The method should be mapped on path '/reservations/add'.
     *
     * @return The view "form.html".
     */

    @GetMapping( "/reservations/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAdd(Model model) {
        List<Hotel> hotels =  this.hotelService.listAll();

        model.addAttribute("hotels", hotels);
        model.addAttribute("roomTypes", RoomType.values());

        return "form.html";
    }

    /**
     * This method should display the "form.html" template.
     * However, in this case, all 'input' elements should be filled with the appropriate value for the reservations that is updated.
     * The method should be mapped on path '/reservations/edit/[id]'.
     *
     * @return The view "form.html".
     */


    @GetMapping( "/reservations/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEdit(@PathVariable Long id, Model model) {

        Reservation reservation = this.reservationService.findById(id);
        List<Hotel> hotels = this.hotelService.listAll();

        model.addAttribute("reservation", reservation);
        model.addAttribute("hotels", hotels);
        model.addAttribute("roomTypes", RoomType.values());


        return "form.html";
    }



    /**
     * This method should create a reservation given the arguments it takes.
     * The method should be mapped on path '/reservations'.
     * After the reservation is created, all reservations should be displayed.
     *
     * @return The view "list.html".
     */

    @PostMapping( "/reservations")
    public String create(@RequestParam String guestName,
                         @RequestParam LocalDate dateCreated,
                         @RequestParam Integer daysOfStay,
                         @RequestParam RoomType roomType,
                         @RequestParam Long hotelId) {

        reservationService.create(guestName, dateCreated, daysOfStay, roomType, hotelId);
        return "redirect:/reservations";
    }

    /**
     * This method should update a reservation given the arguments it takes.
     * The method should be mapped on path '/reservations/[id]'.
     * After the reservation is updated, all reservations should be displayed.
     *
     * @return The view "list.html".
     */

    @PostMapping( "/reservations/{id}")

    public String update(@PathVariable Long id,
                        @RequestParam String guestName,
                         @RequestParam LocalDate dateCreated,
                         @RequestParam Integer daysOfStay,
                         @RequestParam RoomType roomType,
                         @RequestParam Long hotelId) {
        reservationService.update(id, guestName, dateCreated, daysOfStay, roomType, hotelId);
        return "redirect:/reservations";

    }

    /**
     * This method should delete the reservation that has the appropriate identifier.
     * The method should be mapped on path '/reservations/delete/[id]'.
     * After the reservation is deleted, all reservations should be displayed.
     *
     * @return The view "list.html".
     */

    @PostMapping( "/reservations/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        this.reservationService.delete(id);
        return "redirect:/reservations";
    }

    /**
     * This method should implement the logic for extending the duration,
     * by adding one day to the daysOfStay.
     * The method should be mapped on path '/reservations/extend/[id]'.
     * After the operation, all reservations should be displayed.
     *
     * @return The view "list.html".
     */
    @PostMapping( "/reservations/extend/{id}")
    @PreAuthorize("hasRole('USER')")

    public String extend(@PathVariable Long id) {
        this.reservationService.extendStay(id);
        return "redirect:/reservations";
    }
}
