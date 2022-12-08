package fit.foot.repository;

import fit.foot.domain.Proprietaire;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Proprietaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProprietaireRepository extends ReactiveCrudRepository<Proprietaire, Long>, ProprietaireRepositoryInternal {
    @Query("SELECT * FROM proprietaire entity WHERE entity.user_id = :id")
    Flux<Proprietaire> findByUser(Long id);

    @Query("SELECT * FROM proprietaire entity WHERE entity.user_id IS NULL")
    Flux<Proprietaire> findAllWhereUserIsNull();

    @Override
    <S extends Proprietaire> Mono<S> save(S entity);

    @Override
    Flux<Proprietaire> findAll();

    @Override
    Mono<Proprietaire> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProprietaireRepositoryInternal {
    <S extends Proprietaire> Mono<S> save(S entity);

    Flux<Proprietaire> findAllBy(Pageable pageable);

    Flux<Proprietaire> findAll();

    Mono<Proprietaire> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Proprietaire> findAllBy(Pageable pageable, Criteria criteria);

}
