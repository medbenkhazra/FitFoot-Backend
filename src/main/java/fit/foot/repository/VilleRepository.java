package fit.foot.repository;

import fit.foot.domain.Ville;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Ville entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VilleRepository extends ReactiveCrudRepository<Ville, Long>, VilleRepositoryInternal {
    @Override
    <S extends Ville> Mono<S> save(S entity);

    @Override
    Flux<Ville> findAll();

    @Override
    Mono<Ville> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface VilleRepositoryInternal {
    <S extends Ville> Mono<S> save(S entity);

    Flux<Ville> findAllBy(Pageable pageable);

    Flux<Ville> findAll();

    Mono<Ville> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Ville> findAllBy(Pageable pageable, Criteria criteria);

}
