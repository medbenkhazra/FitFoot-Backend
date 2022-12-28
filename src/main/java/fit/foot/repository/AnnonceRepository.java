package fit.foot.repository;

import fit.foot.domain.Annonce;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Annonce entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    List<Annonce> findByResponsableIdOrderByHeureDebutDesc(Long responsableId);
}
