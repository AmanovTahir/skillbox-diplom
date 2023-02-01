package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/*
lemma — леммы, встречающиеся в текстах (см. справочно: лемматизация).

id INT NOT NULL AUTO_INCREMENT;
site_id INT NOT NULL — ID веб-сайта из таблицы site;
lemma VARCHAR(255) NOT NULL — нормальная форма слова (лемма);
frequency INT NOT NULL — количество страниц, на которых
слово встречается хотя бы один раз. Максимальное значение
не может превышать общее количество слов на сайте.

 */
@Entity
@Getter
@Setter
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private Site site;

    @Column(name = "lemma", nullable = false)
    private String lemma;

    @Column(name = "frequency", nullable = false)
    private Integer frequency;

    @OneToMany(mappedBy = "lemma", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private Set<Index> indices;
}
