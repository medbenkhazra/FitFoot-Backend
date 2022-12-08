package fit.foot.repository;

import fit.foot.domain.Annonce;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Annonce entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnnonceRepository extends ReactiveCrudRepository<Annonce, Long>, AnnonceRepositoryInternal {
    @Query("SELECT * FROM annonce entity WHERE entity.terrain_id = :id")
    Flux<Annonce> findByTerrain(Long id);

    @Query("SELECT * FROM annonce entity WHERE entity.terrain_id IS NULL")
    Flux<Annonce> findAllWhereTerrainIsNull();

    @Query("SELECT * FROM annonce entity WHERE entity.id not in (select equipe_id from equipe)")
    Flux<Annonce> findAllWhereEquipeIsNull();

    @Query("SELECT * FROM annonce entity WHERE entity.responsable_id = :id")
    Flux<Annonce> findByResponsable(Long id);

    @Query("SELECT * FROM annonce entity WHERE entity.responsable_id IS NULL")
    Flux<Annonce> findAllWhereResponsableIsNull();

    @Override
    <S extends Annonce> Mono<S> save(S entity);

    @Override
    Flux<Annonce> findAll();

    @Override
    Mono<Annonce> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AnnonceRepositoryInternal {
    <S extends Annonce> Mono<S> save(S entity);

    Flux<Annonce> findAllBy(Pageable pageable);

    Flux<Annonce> findAll();

    Mono<Annonce> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Annonce> findAllBy(Pageable pageable, Criteria criteria);

}
