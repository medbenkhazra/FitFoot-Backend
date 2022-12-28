package fit.foot.web.rest;

import fit.foot.domain.Reservation;
import fit.foot.domain.enumeration.TimeSlot;
import fit.foot.repository.ReservationRepository;
import fit.foot.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fit.foot.domain.Reservation}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ReservationResource {

    private final Logger log = LoggerFactory.getLogger(ReservationResource.class);

    private static final String ENTITY_NAME = "reservation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReservationRepository reservationRepository;

    public ReservationResource(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * {@code POST  /reservations} : Create a new reservation.
     *
     * @param reservation the reservation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new reservation, or with status {@code 400 (Bad Request)} if
     *         the reservation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) throws URISyntaxException {
        log.debug("REST request to save Reservation : {}", reservation);
        if (reservation.getId() != null) {
            throw new BadRequestAlertException("A new reservation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Reservation result;
        boolean timeSlotFound = Arrays
            .stream(TimeSlot.values())
            .anyMatch(slot ->
                slot.getStartTime().equals(reservation.getHeureDebut().toLocalTime()) &&
                slot.getEndTime().equals(reservation.getHeureFin().toLocalTime())
            );
        if (!timeSlotFound) {
            throw new IllegalArgumentException("Invalid time slot");
        }
        if (isTimeSlotAvailable(reservation.getHeureDebut(), reservation.getHeureFin())) {
            // Save the reservation
            result = reservationRepository.save(reservation);
        } else {
            throw new IllegalArgumentException("Time slot is not available for reservation");
        }
        return ResponseEntity
            .created(new URI("/api/reservations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reservations/:id} : Updates an existing reservation.
     *
     * @param id          the id of the reservation to save.
     * @param reservation the reservation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated reservation,
     *         or with status {@code 400 (Bad Request)} if the reservation is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the reservation
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservation> updateReservation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Reservation reservation
    ) throws URISyntaxException {
        log.debug("REST request to update Reservation : {}, {}", id, reservation);
        if (reservation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reservationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Reservation result = reservationRepository.save(reservation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reservation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reservations/:id} : Partial updates given fields of an
     * existing reservation, field will ignore if it is null
     *
     * @param id          the id of the reservation to save.
     * @param reservation the reservation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated reservation,
     *         or with status {@code 400 (Bad Request)} if the reservation is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the reservation is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the reservation
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reservations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Reservation> partialUpdateReservation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Reservation reservation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reservation partially : {}, {}", id, reservation);
        if (reservation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reservationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Reservation> result = reservationRepository
            .findById(reservation.getId())
            .map(existingReservation -> {
                if (reservation.getDate() != null) {
                    existingReservation.setDate(reservation.getDate());
                }
                if (reservation.getHeureDebut() != null) {
                    existingReservation.setHeureDebut(reservation.getHeureDebut());
                }
                if (reservation.getHeureFin() != null) {
                    existingReservation.setHeureFin(reservation.getHeureFin());
                }

                return existingReservation;
            })
            .map(reservationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reservation.getId().toString())
        );
    }

    /**
     * {@code GET  /reservations} : get all the reservations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of reservations in body.
     */
    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        log.debug("REST request to get all Reservations");
        return reservationRepository.findAll();
    }

    /**
     * {@code GET  /reservations/:id} : get the "id" reservation.
     *
     * @param id the id of the reservation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the reservation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reservations/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        log.debug("REST request to get Reservation : {}", id);
        Optional<Reservation> reservation = reservationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(reservation);
    }

    /**
     * {@code DELETE  /reservations/:id} : delete the "id" reservation.
     *
     * @param id the id of the reservation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.debug("REST request to delete Reservation : {}", id);
        reservationRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    // an endpoint that gets all the time slots available for a given date
    @GetMapping("/reservations/timeslots/{date}")
    public List<TimeSlot> getTimeSlots(@PathVariable String date) {
        // get all the reservations for the given date
        List<Reservation> reservations = reservationRepository.findByDate(LocalDate.parse(date));
        // get all the time slots
        List<TimeSlot> timeSlots = Arrays.asList(TimeSlot.values());
        timeSlots = new ArrayList<>(timeSlots);

        List<TimeSlot> timeSlotsToRemove = new ArrayList<>();

        for (Reservation reservation : reservations) {
            for (TimeSlot timeSlot : timeSlots) {
                // if start time is between the start and end time of the TimeSlot remove it or they are equal

                if (
                    timeSlot.getStartTime().isAfter(reservation.getHeureDebut().toLocalTime()) &&
                    timeSlot.getStartTime().isBefore(reservation.getHeureFin().toLocalTime()) ||
                    timeSlot.getStartTime().equals(reservation.getHeureDebut().toLocalTime()) ||
                    timeSlot.getStartTime().equals(reservation.getHeureFin().toLocalTime())
                ) {
                    timeSlotsToRemove.add(timeSlot);
                }
            }
        }

        timeSlots.removeAll(timeSlotsToRemove);
        // timeSlotsToRemove = new ArrayList<>();
        // remove the time slots that are already reserved
        // for (Reservation reservation : reservations) {
        //     for (TimeSlot timeSlot : timeSlots) {
        //         if (timeSlot.getStartTime().equals(reservation.getHeureDebut())
        //                 && timeSlot.getEndTime().equals(reservation.getHeureFin())) {
        //             timeSlots.remove(timeSlot);
        //         }
        //     }
        // }
        return timeSlots;
    }

    public boolean isTimeSlotAvailable(ZonedDateTime start, ZonedDateTime end) {
        // check if the time slot is valid and not in the past
        if (start.isBefore(ZonedDateTime.now()) || end.isBefore(ZonedDateTime.now())) {
            throw new BadRequestAlertException("Invalid time slot", ENTITY_NAME, "timeslotinvalid");
        }
        // Check if the time slot overlaps with any existing reservations
        Optional<Reservation> reservations = reservationRepository.findByHeureDebutBeforeAndHeureFinAfter(end, start);
        // check if reservation is one of the time slots
        boolean isTimeSlot = false;
        for (TimeSlot timeSlot : TimeSlot.values()) {
            if (timeSlot.getStartTime().equals(start.toLocalTime()) && timeSlot.getEndTime().equals(end.toLocalTime())) {
                isTimeSlot = true;
            }
        }
        if (!isTimeSlot) {
            throw new BadRequestAlertException("Invalid time slot", ENTITY_NAME, "timeslotinvalid");
        }
        return isTimeSlot && (!reservations.isPresent());
    }
}
