package fit.foot.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import fit.foot.domain.Terrain;
import fit.foot.repository.rowmapper.ComplexeRowMapper;
import fit.foot.repository.rowmapper.TerrainRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
 * Spring Data R2DBC custom repository implementation for the Terrain entity.
 */
@SuppressWarnings("unused")
class TerrainRepositoryInternalImpl extends SimpleR2dbcRepository<Terrain, Long> implements TerrainRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ComplexeRowMapper complexeMapper;
    private final TerrainRowMapper terrainMapper;

    private static final Table entityTable = Table.aliased("terrain", EntityManager.ENTITY_ALIAS);
    private static final Table complexeTable = Table.aliased("complexe", "complexe");

    public TerrainRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ComplexeRowMapper complexeMapper,
        TerrainRowMapper terrainMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Terrain.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.complexeMapper = complexeMapper;
        this.terrainMapper = terrainMapper;
    }

    @Override
    public Flux<Terrain> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Terrain> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TerrainSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ComplexeSqlHelper.getColumns(complexeTable, "complexe"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(complexeTable)
            .on(Column.create("complexe_id", entityTable))
            .equals(Column.create("id", complexeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Terrain.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Terrain> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Terrain> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Terrain process(Row row, RowMetadata metadata) {
        Terrain entity = terrainMapper.apply(row, "e");
        entity.setComplexe(complexeMapper.apply(row, "complexe"));
        return entity;
    }

    @Override
    public <S extends Terrain> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
