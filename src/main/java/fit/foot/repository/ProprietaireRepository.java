package fit.foot.repository;

import fit.foot.domain.Proprietaire;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Proprietaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProprietaireRepository extends JpaRepository<Proprietaire, Long> {}
