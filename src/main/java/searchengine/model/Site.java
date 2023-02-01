package searchengine.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(columnDefinition = "enum('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "status_time", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "site", cascade = {CascadeType.REMOVE, CascadeType.ALL}, orphanRemoval = true)
    private Set<Page> pages;

    @OneToMany(mappedBy = "site", cascade = {CascadeType.REMOVE, CascadeType.ALL})
    private Set<Lemma> lemmas;
}
