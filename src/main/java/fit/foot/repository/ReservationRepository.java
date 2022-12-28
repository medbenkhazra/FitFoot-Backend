package fit.foot.repository;

import fit.foot.domain.Reservation;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reservation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByHeureDebutBeforeAndHeureFinAfter(ZonedDateTime heureDebut, ZonedDateTime heureFin);

    List<Reservation> findByDateAndTerrainId(LocalDate parse, Long terrainId);
}
