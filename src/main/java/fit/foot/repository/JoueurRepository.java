package fit.foot.repository;

import fit.foot.domain.Joueur;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Joueur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JoueurRepository extends ReactiveCrudRepository<Joueur, Long>, JoueurRepositoryInternal {
    @Override
    Mono<Joueur> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Joueur> findAllWithEagerRelationships();

    @Override
    Flux<Joueur> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM joueur entity WHERE entity.user_id = :id")
    Flux<Joueur> findByUser(Long id);

    @Query("SELECT * FROM joueur entity WHERE entity.user_id IS NULL")
    Flux<Joueur> findAllWhereUserIsNull();

    @Query(
        "SELECT entity.* FROM joueur entity JOIN rel_joueur__equipe joinTable ON entity.id = joinTable.equipe_id WHERE joinTable.equipe_id = :id"
    )
    Flux<Joueur> findByEquipe(Long id);

    @Query("SELECT * FROM joueur entity WHERE entity.quartier_id = :id")
    Flux<Joueur> findByQuartier(Long id);

    @Query("SELECT * FROM joueur entity WHERE entity.quartier_id IS NULL")
    Flux<Joueur> findAllWhereQuartierIsNull();

    @Override
    <S extends Joueur> Mono<S> save(S entity);

    @Override
    Flux<Joueur> findAll();

    @Override
    Mono<Joueur> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface JoueurRepositoryInternal {
    <S extends Joueur> Mono<S> save(S entity);

    Flux<Joueur> findAllBy(Pageable pageable);

    Flux<Joueur> findAll();

    Mono<Joueur> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Joueur> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Joueur> findOneWithEagerRelationships(Long id);

    Flux<Joueur> findAllWithEagerRelationships();

    Flux<Joueur> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
