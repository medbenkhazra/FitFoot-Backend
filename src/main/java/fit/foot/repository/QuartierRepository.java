package fit.foot.repository;

import fit.foot.domain.Quartier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Quartier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuartierRepository extends ReactiveCrudRepository<Quartier, Long>, QuartierRepositoryInternal {
    @Query("SELECT * FROM quartier entity WHERE entity.ville_id = :id")
    Flux<Quartier> findByVille(Long id);

    @Query("SELECT * FROM quartier entity WHERE entity.ville_id IS NULL")
    Flux<Quartier> findAllWhereVilleIsNull();

    @Override
    <S extends Quartier> Mono<S> save(S entity);

    @Override
    Flux<Quartier> findAll();

    @Override
    Mono<Quartier> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface QuartierRepositoryInternal {
    <S extends Quartier> Mono<S> save(S entity);

    Flux<Quartier> findAllBy(Pageable pageable);

    Flux<Quartier> findAll();

    Mono<Quartier> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Quartier> findAllBy(Pageable pageable, Criteria criteria);

}
