package searchengine.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.Status;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SiteRepository extends CrudRepository<Site, Integer> {
    @Transactional
    @Modifying
    @Query("delete from Site s where upper(s.url) = upper(?1)")
    void deleteByUrlIgnoreCaseAllIgnoreCase(String url);

    @Transactional
    @Modifying
    @Query("update Site s set s.status = ?1 where s.name = ?2")
    void updateStatusByName(Status status, String name);

    Optional<Site> findByName(String name);

    @Transactional
    @Modifying
    @Query("update Site s set s.statusTime = ?1 where s.url = ?2")
    void updateStatusTimeByUrl(LocalDateTime statusTime, String url);

    Optional<Site> findByUrl(@NonNull String url);

    @Transactional
    @Modifying
    @Query("update Site s set s.pages = ?1 where s.url = ?2")
    int updatePagesByUrl(Page pages, String url);

    void deleteByUrl(String url);
}
