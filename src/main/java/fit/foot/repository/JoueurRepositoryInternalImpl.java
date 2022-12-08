package fit.foot.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import fit.foot.domain.Equipe;
import fit.foot.domain.Joueur;
import fit.foot.domain.enumeration.GENDER;
import fit.foot.repository.rowmapper.JoueurRowMapper;
import fit.foot.repository.rowmapper.QuartierRowMapper;
import fit.foot.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
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
 * Spring Data R2DBC custom repository implementation for the Joueur entity.
 */
@SuppressWarnings("unused")
class JoueurRepositoryInternalImpl extends SimpleR2dbcRepository<Joueur, Long> implements JoueurRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final QuartierRowMapper quartierMapper;
    private final JoueurRowMapper joueurMapper;

    private static final Table entityTable = Table.aliased("joueur", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table quartierTable = Table.aliased("quartier", "quartier");

    private static final EntityManager.LinkTable equipeLink = new EntityManager.LinkTable("rel_joueur__equipe", "joueur_id", "equipe_id");

    public JoueurRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        QuartierRowMapper quartierMapper,
        JoueurRowMapper joueurMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Joueur.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.quartierMapper = quartierMapper;
        this.joueurMapper = joueurMapper;
    }

    @Override
    public Flux<Joueur> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Joueur> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = JoueurSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(QuartierSqlHelper.getColumns(quartierTable, "quartier"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(quartierTable)
            .on(Column.create("quartier_id", entityTable))
            .equals(Column.create("id", quartierTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Joueur.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Joueur> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Joueur> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Joueur> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Joueur> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Joueur> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Joueur process(Row row, RowMetadata metadata) {
        Joueur entity = joueurMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setQuartier(quartierMapper.apply(row, "quartier"));
        return entity;
    }

    @Override
    public <S extends Joueur> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Joueur> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(equipeLink, entity.getId(), entity.getEquipes().stream().map(Equipe::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(equipeLink, entityId);
    }
}
