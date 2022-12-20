package fit.foot.repository;

import fit.foot.domain.Joueur;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface JoueurRepositoryWithBagRelationships {
    Optional<Joueur> fetchBagRelationships(Optional<Joueur> joueur);

    List<Joueur> fetchBagRelationships(List<Joueur> joueurs);

    Page<Joueur> fetchBagRelationships(Page<Joueur> joueurs);
}
