package fit.foot.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import fit.foot.domain.Equipe;
import fit.foot.repository.rowmapper.AnnonceRowMapper;
import fit.foot.repository.rowmapper.EquipeRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Equipe entity.
 */
@SuppressWarnings("unused")
class EquipeRepositoryInternalImpl extends SimpleR2dbcRepository<Equipe, Long> implements EquipeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AnnonceRowMapper annonceMapper;
    private final EquipeRowMapper equipeMapper;

    private static final Table entityTable = Table.aliased("equipe", EntityManager.ENTITY_ALIAS);
    private static final Table annonceTable = Table.aliased("annonce", "annonce");

    public EquipeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AnnonceRowMapper annonceMapper,
        EquipeRowMapper equipeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Equipe.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.annonceMapper = annonceMapper;
        this.equipeMapper = equipeMapper;
    }

    @Override
    public Flux<Equipe> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Equipe> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EquipeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AnnonceSqlHelper.getColumns(annonceTable, "annonce"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(annonceTable)
            .on(Column.create("annonce_id", entityTable))
            .equals(Column.create("id", annonceTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Equipe.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Equipe> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Equipe> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Equipe process(Row row, RowMetadata metadata) {
        Equipe entity = equipeMapper.apply(row, "e");
        entity.setAnnonce(annonceMapper.apply(row, "annonce"));
        return entity;
    }

    @Override
    public <S extends Equipe> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
