package fit.foot.repository;

import fit.foot.domain.Complexe;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Complexe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComplexeRepository extends JpaRepository<Complexe, Long> {
    List<Complexe> findByNomIgnoreCaseIsContaining(String nom);

    List<Complexe> findByProprietaireUserLogin(String login);
}
