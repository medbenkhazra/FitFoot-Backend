package fit.foot.repository;

import fit.foot.domain.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Reservation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReservationRepository extends ReactiveCrudRepository<Reservation, Long>, ReservationRepositoryInternal {
    @Query("SELECT * FROM reservation entity WHERE entity.terrain_id = :id")
    Flux<Reservation> findByTerrain(Long id);

    @Query("SELECT * FROM reservation entity WHERE entity.terrain_id IS NULL")
    Flux<Reservation> findAllWhereTerrainIsNull();

    @Override
    <S extends Reservation> Mono<S> save(S entity);

    @Override
    Flux<Reservation> findAll();

    @Override
    Mono<Reservation> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ReservationRepositoryInternal {
    <S extends Reservation> Mono<S> save(S entity);

    Flux<Reservation> findAllBy(Pageable pageable);

    Flux<Reservation> findAll();

    Mono<Reservation> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Reservation> findAllBy(Pageable pageable, Criteria criteria);

}
