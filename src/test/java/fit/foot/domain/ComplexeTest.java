package fit.foot.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fit.foot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComplexeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Complexe.class);
        Complexe complexe1 = new Complexe();
        complexe1.setId(1L);
        Complexe complexe2 = new Complexe();
        complexe2.setId(complexe1.getId());
        assertThat(complexe1).isEqualTo(complexe2);
        complexe2.setId(2L);
        assertThat(complexe1).isNotEqualTo(complexe2);
        complexe1.setId(null);
        assertThat(complexe1).isNotEqualTo(complexe2);
    }
}
