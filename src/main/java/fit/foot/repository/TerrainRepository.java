package fit.foot.repository;

import fit.foot.domain.Terrain;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Terrain entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TerrainRepository extends ReactiveCrudRepository<Terrain, Long>, TerrainRepositoryInternal {
    @Query("SELECT * FROM terrain entity WHERE entity.id not in (select annonce_id from annonce)")
    Flux<Terrain> findAllWhereAnnonceIsNull();

    @Query("SELECT * FROM terrain entity WHERE entity.complexe_id = :id")
    Flux<Terrain> findByComplexe(Long id);

    @Query("SELECT * FROM terrain entity WHERE entity.complexe_id IS NULL")
    Flux<Terrain> findAllWhereComplexeIsNull();

    @Override
    <S extends Terrain> Mono<S> save(S entity);

    @Override
    Flux<Terrain> findAll();

    @Override
    Mono<Terrain> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TerrainRepositoryInternal {
    <S extends Terrain> Mono<S> save(S entity);

    Flux<Terrain> findAllBy(Pageable pageable);

    Flux<Terrain> findAll();

    Mono<Terrain> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Terrain> findAllBy(Pageable pageable, Criteria criteria);

}
