package searchengine.model;


import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Page implements Comparable<Page> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private Site site;
    @Column(columnDefinition = "TEXT NOT NULL, UNIQUE KEY path_idx(path(512), site_id)")
    private String path;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @OneToMany(mappedBy = "page", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private Set<Index> indices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(path, page.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public int compareTo(Page o) {
        return this.getPath().compareTo(o.getPath());
    }
}
