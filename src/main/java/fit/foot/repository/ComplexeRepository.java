package fit.foot.repository;

import fit.foot.domain.Complexe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Complexe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComplexeRepository extends ReactiveCrudRepository<Complexe, Long>, ComplexeRepositoryInternal {
    @Query("SELECT * FROM complexe entity WHERE entity.quartier_id = :id")
    Flux<Complexe> findByQuartier(Long id);

    @Query("SELECT * FROM complexe entity WHERE entity.quartier_id IS NULL")
    Flux<Complexe> findAllWhereQuartierIsNull();

    @Query("SELECT * FROM complexe entity WHERE entity.proprietaire_id = :id")
    Flux<Complexe> findByProprietaire(Long id);

    @Query("SELECT * FROM complexe entity WHERE entity.proprietaire_id IS NULL")
    Flux<Complexe> findAllWhereProprietaireIsNull();

    @Override
    <S extends Complexe> Mono<S> save(S entity);

    @Override
    Flux<Complexe> findAll();

    @Override
    Mono<Complexe> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ComplexeRepositoryInternal {
    <S extends Complexe> Mono<S> save(S entity);

    Flux<Complexe> findAllBy(Pageable pageable);

    Flux<Complexe> findAll();

    Mono<Complexe> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Complexe> findAllBy(Pageable pageable, Criteria criteria);

}
