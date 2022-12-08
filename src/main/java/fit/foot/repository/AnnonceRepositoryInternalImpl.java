package fit.foot.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import fit.foot.domain.Annonce;
import fit.foot.domain.enumeration.STATUS;
import fit.foot.repository.rowmapper.AnnonceRowMapper;
import fit.foot.repository.rowmapper.JoueurRowMapper;
import fit.foot.repository.rowmapper.TerrainRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Annonce entity.
 */
@SuppressWarnings("unused")
class AnnonceRepositoryInternalImpl extends SimpleR2dbcRepository<Annonce, Long> implements AnnonceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TerrainRowMapper terrainMapper;
    private final JoueurRowMapper joueurMapper;
    private final AnnonceRowMapper annonceMapper;

    private static final Table entityTable = Table.aliased("annonce", EntityManager.ENTITY_ALIAS);
    private static final Table terrainTable = Table.aliased("terrain", "terrain");
    private static final Table responsableTable = Table.aliased("joueur", "responsable");

    public AnnonceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TerrainRowMapper terrainMapper,
        JoueurRowMapper joueurMapper,
        AnnonceRowMapper annonceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Annonce.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.terrainMapper = terrainMapper;
        this.joueurMapper = joueurMapper;
        this.annonceMapper = annonceMapper;
    }

    @Override
    public Flux<Annonce> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Annonce> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AnnonceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TerrainSqlHelper.getColumns(terrainTable, "terrain"));
        columns.addAll(JoueurSqlHelper.getColumns(responsableTable, "responsable"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(terrainTable)
            .on(Column.create("terrain_id", entityTable))
            .equals(Column.create("id", terrainTable))
            .leftOuterJoin(responsableTable)
            .on(Column.create("responsable_id", entityTable))
            .equals(Column.create("id", responsableTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Annonce.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Annonce> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Annonce> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Annonce process(Row row, RowMetadata metadata) {
        Annonce entity = annonceMapper.apply(row, "e");
        entity.setTerrain(terrainMapper.apply(row, "terrain"));
        entity.setResponsable(joueurMapper.apply(row, "responsable"));
        return entity;
    }

    @Override
    public <S extends Annonce> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
