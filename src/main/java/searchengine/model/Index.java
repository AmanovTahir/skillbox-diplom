package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "`index`")
@Getter
@Setter
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "page_id", referencedColumnName = "id", nullable = false)
    private Page page;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "lemma_id", referencedColumnName = "id", nullable = false)
    private Lemma lemma;

    @Column(name = "`rank`", nullable = false)
    private Float rank;
}
