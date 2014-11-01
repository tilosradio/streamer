package hu.tilos.radio.backend.episode;


import hu.radio.tilos.model.Bookmark;
import hu.radio.tilos.model.Episode;
import hu.radio.tilos.model.Show;
import hu.radio.tilos.model.TextContent;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.types.EpisodeData;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.TextData;


import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.modelmapper.ModelMapper;


/**
 * Returns with the persisted episode records.
 */
public class PersistentEpisodeProvider {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ModelMapper modelMapper;

    public List<EpisodeData> listEpisode(int showId, Date from, Date to) {

        String query = "SELECT e from Episode e WHERE e.plannedFrom < :end AND e.plannedTo > :start";


        if (showId > 0) {
            query += " AND e.show.id = :showId";
        } else {
            query += " AND e.show.status = 1";

        }
        TypedQuery<Episode> q = entityManager.createQuery(query, Episode.class);
        q.setParameter("start", from);
        q.setParameter("end", to);
        if (showId > 0) {
            q.setParameter("showId", showId);
        }

        List<EpisodeData> result = new ArrayList<>();
        for (Episode e : q.getResultList()) {
            EpisodeData d = modelMapper.map(e, EpisodeData.class);
            d.setPersistent(true);

            if (e.getPlannedTo() == e.getRealTo() || e.getText() == null) {
                Query bookmarkQuery = entityManager.createQuery("SELECT b FROM Bookmark b WHERE b.episode.id = :id").setParameter("id", d.getId());
                List<Bookmark> bookmarks = bookmarkQuery.getResultList();
                Bookmark bookmark = chooseTheBestBookmark(bookmarks);
                useBookmarkForEpisodeText(d, bookmark);
            }
            if (d.getPlannedTo() == d.getRealTo()) {
                //todo
                Date nd = new Date();
                nd.setTime(d.getPlannedTo().getTime() + 30 * 60 * 1000);
                d.setRealTo(nd);
            }
            result.add(d);
        }

        return result;

    }

    private void useBookmarkForEpisodeText(EpisodeData episodeData, Bookmark bookmark) {
        if (bookmark != null) {
            TextData textData = new TextData();
            textData.setTitle(bookmark.getTitle());
            episodeData.setText(textData);
        }
    }

    private Bookmark chooseTheBestBookmark(List<Bookmark> bookmarks) {
        if (bookmarks.size() == 0) {
            return null;
        }
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.isSelected()) {
                return bookmark;
            }
        }
        return null;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
