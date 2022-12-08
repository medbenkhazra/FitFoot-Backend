package fit.foot.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import fit.foot.domain.Complexe;
import fit.foot.repository.rowmapper.ComplexeRowMapper;
import fit.foot.repository.rowmapper.ProprietaireRowMapper;
import fit.foot.repository.rowmapper.QuartierRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Complexe entity.
 */
@SuppressWarnings("unused")
class ComplexeRepositoryInternalImpl extends SimpleR2dbcRepository<Complexe, Long> implements ComplexeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final QuartierRowMapper quartierMapper;
    private final ProprietaireRowMapper proprietaireMapper;
    private final ComplexeRowMapper complexeMapper;

    private static final Table entityTable = Table.aliased("complexe", EntityManager.ENTITY_ALIAS);
    private static final Table quartierTable = Table.aliased("quartier", "quartier");
    private static final Table proprietaireTable = Table.aliased("proprietaire", "proprietaire");

    public ComplexeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        QuartierRowMapper quartierMapper,
        ProprietaireRowMapper proprietaireMapper,
        ComplexeRowMapper complexeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Complexe.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.quartierMapper = quartierMapper;
        this.proprietaireMapper = proprietaireMapper;
        this.complexeMapper = complexeMapper;
    }

    @Override
    public Flux<Complexe> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Complexe> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ComplexeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(QuartierSqlHelper.getColumns(quartierTable, "quartier"));
        columns.addAll(ProprietaireSqlHelper.getColumns(proprietaireTable, "proprietaire"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(quartierTable)
            .on(Column.create("quartier_id", entityTable))
            .equals(Column.create("id", quartierTable))
            .leftOuterJoin(proprietaireTable)
            .on(Column.create("proprietaire_id", entityTable))
            .equals(Column.create("id", proprietaireTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Complexe.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Complexe> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Complexe> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Complexe process(Row row, RowMetadata metadata) {
        Complexe entity = complexeMapper.apply(row, "e");
        entity.setQuartier(quartierMapper.apply(row, "quartier"));
        entity.setProprietaire(proprietaireMapper.apply(row, "proprietaire"));
        return entity;
    }

    @Override
    public <S extends Complexe> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
