package fit.foot.repository;

import fit.foot.domain.Complexe;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Complexe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComplexeRepository extends JpaRepository<Complexe, Long> {}
