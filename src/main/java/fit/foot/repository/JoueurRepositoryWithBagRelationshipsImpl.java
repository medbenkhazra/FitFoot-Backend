package fit.foot.repository;

import fit.foot.domain.Joueur;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class JoueurRepositoryWithBagRelationshipsImpl implements JoueurRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Joueur> fetchBagRelationships(Optional<Joueur> joueur) {
        return joueur.map(this::fetchEquipes);
    }

    @Override
    public Page<Joueur> fetchBagRelationships(Page<Joueur> joueurs) {
        return new PageImpl<>(fetchBagRelationships(joueurs.getContent()), joueurs.getPageable(), joueurs.getTotalElements());
    }

    @Override
    public List<Joueur> fetchBagRelationships(List<Joueur> joueurs) {
        return Optional.of(joueurs).map(this::fetchEquipes).orElse(Collections.emptyList());
    }

    Joueur fetchEquipes(Joueur result) {
        return entityManager
            .createQuery("select joueur from Joueur joueur left join fetch joueur.equipes where joueur is :joueur", Joueur.class)
            .setParameter("joueur", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Joueur> fetchEquipes(List<Joueur> joueurs) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, joueurs.size()).forEach(index -> order.put(joueurs.get(index).getId(), index));
        List<Joueur> result = entityManager
            .createQuery("select distinct joueur from Joueur joueur left join fetch joueur.equipes where joueur in :joueurs", Joueur.class)
            .setParameter("joueurs", joueurs)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
