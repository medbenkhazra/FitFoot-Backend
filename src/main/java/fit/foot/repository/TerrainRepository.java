package fit.foot.repository;

import fit.foot.domain.Terrain;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Terrain entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TerrainRepository extends JpaRepository<Terrain, Long> {}
