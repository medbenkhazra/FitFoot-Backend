package fit.foot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Reservation.
 */
@Table("reservation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("date")
    private LocalDate date;

    @Column("heure_debut")
    private ZonedDateTime heureDebut;

    @Column("heure_fin")
    private ZonedDateTime heureFin;

    @Transient
    @JsonIgnoreProperties(value = { "reservations", "annonce", "complexe" }, allowSetters = true)
    private Terrain terrain;

    @Column("terrain_id")
    private Long terrainId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reservation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Reservation date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ZonedDateTime getHeureDebut() {
        return this.heureDebut;
    }

    public Reservation heureDebut(ZonedDateTime heureDebut) {
        this.setHeureDebut(heureDebut);
        return this;
    }

    public void setHeureDebut(ZonedDateTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public ZonedDateTime getHeureFin() {
        return this.heureFin;
    }

    public Reservation heureFin(ZonedDateTime heureFin) {
        this.setHeureFin(heureFin);
        return this;
    }

    public void setHeureFin(ZonedDateTime heureFin) {
        this.heureFin = heureFin;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
        this.terrainId = terrain != null ? terrain.getId() : null;
    }

    public Reservation terrain(Terrain terrain) {
        this.setTerrain(terrain);
        return this;
    }

    public Long getTerrainId() {
        return this.terrainId;
    }

    public void setTerrainId(Long terrain) {
        this.terrainId = terrain;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation)) {
            return false;
        }
        return id != null && id.equals(((Reservation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", heureDebut='" + getHeureDebut() + "'" +
            ", heureFin='" + getHeureFin() + "'" +
            "}";
    }
}
