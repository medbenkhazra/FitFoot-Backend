package fit.foot.repository;

import fit.foot.domain.Equipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Equipe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipeRepository extends ReactiveCrudRepository<Equipe, Long>, EquipeRepositoryInternal {
    @Query("SELECT * FROM equipe entity WHERE entity.annonce_id = :id")
    Flux<Equipe> findByAnnonce(Long id);

    @Query("SELECT * FROM equipe entity WHERE entity.annonce_id IS NULL")
    Flux<Equipe> findAllWhereAnnonceIsNull();

    @Override
    <S extends Equipe> Mono<S> save(S entity);

    @Override
    Flux<Equipe> findAll();

    @Override
    Mono<Equipe> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EquipeRepositoryInternal {
    <S extends Equipe> Mono<S> save(S entity);

    Flux<Equipe> findAllBy(Pageable pageable);

    Flux<Equipe> findAll();

    Mono<Equipe> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Equipe> findAllBy(Pageable pageable, Criteria criteria);

}
