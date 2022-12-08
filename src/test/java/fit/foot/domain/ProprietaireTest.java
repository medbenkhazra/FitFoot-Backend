package fit.foot.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fit.foot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProprietaireTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Proprietaire.class);
        Proprietaire proprietaire1 = new Proprietaire();
        proprietaire1.setId(1L);
        Proprietaire proprietaire2 = new Proprietaire();
        proprietaire2.setId(proprietaire1.getId());
        assertThat(proprietaire1).isEqualTo(proprietaire2);
        proprietaire2.setId(2L);
        assertThat(proprietaire1).isNotEqualTo(proprietaire2);
        proprietaire1.setId(null);
        assertThat(proprietaire1).isNotEqualTo(proprietaire2);
    }
}
