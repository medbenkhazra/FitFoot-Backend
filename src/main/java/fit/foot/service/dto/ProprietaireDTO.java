package fit.foot.service.dto;

import java.io.Serializable;

/**
 * A DTO representing a user, with his authorities.
 */
public class ProprietaireDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String cin;

    private String rib;

    private String numTel;

    private AdminUserDTO user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProprietaireDTO id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCin() {
        return this.cin;
    }

    public ProprietaireDTO cin(String cin) {
        this.setCin(cin);
        return this;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getRib() {
        return this.rib;
    }

    public ProprietaireDTO rib(String rib) {
        this.setRib(rib);
        return this;
    }

    public void setRib(String rib) {
        this.rib = rib;
    }

    public String getNumTel() {
        return this.numTel;
    }

    public ProprietaireDTO numTel(String numTel) {
        this.setNumTel(numTel);
        return this;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public AdminUserDTO getUser() {
        return this.user;
    }

    public void setUser(AdminUserDTO user) {
        this.user = user;
    }

    public ProprietaireDTO user(AdminUserDTO user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProprietaireDTO)) {
            return false;
        }
        return id != null && id.equals(((ProprietaireDTO) o).id);
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Proprietaire{" +
                "id=" + getId() +
                ", cin='" + getCin() + "'" +
                ", rib='" + getRib() + "'" +
                ", numTel='" + getNumTel() + "'" +
                "}";
    }
}
