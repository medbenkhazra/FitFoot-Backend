package fit.foot.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import fit.foot.domain.Quartier;
import fit.foot.repository.rowmapper.QuartierRowMapper;
import fit.foot.repository.rowmapper.VilleRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Quartier entity.
 */
@SuppressWarnings("unused")
class QuartierRepositoryInternalImpl extends SimpleR2dbcRepository<Quartier, Long> implements QuartierRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final VilleRowMapper villeMapper;
    private final QuartierRowMapper quartierMapper;

    private static final Table entityTable = Table.aliased("quartier", EntityManager.ENTITY_ALIAS);
    private static final Table villeTable = Table.aliased("ville", "ville");

    public QuartierRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        VilleRowMapper villeMapper,
        QuartierRowMapper quartierMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Quartier.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.villeMapper = villeMapper;
        this.quartierMapper = quartierMapper;
    }

    @Override
    public Flux<Quartier> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Quartier> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = QuartierSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(VilleSqlHelper.getColumns(villeTable, "ville"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(villeTable)
            .on(Column.create("ville_id", entityTable))
            .equals(Column.create("id", villeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Quartier.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Quartier> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Quartier> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Quartier process(Row row, RowMetadata metadata) {
        Quartier entity = quartierMapper.apply(row, "e");
        entity.setVille(villeMapper.apply(row, "ville"));
        return entity;
    }

    @Override
    public <S extends Quartier> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
