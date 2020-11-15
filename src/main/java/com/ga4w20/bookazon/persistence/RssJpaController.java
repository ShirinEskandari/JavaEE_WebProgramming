/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.beans.RSSItem;
import com.ga4w20.bookazon.entities.Rss;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Alex
 */
@Named
@SessionScoped
public class RssJpaController implements Serializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(RssJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public void create(Rss rss) {
        try {
            utx.begin();
            em.persist(rss);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RssJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Rss rss) {
        try {
            utx.begin();
            rss = em.merge(rss);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RssJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            utx.begin();
            Rss rss;
            try {
                rss = em.getReference(Rss.class, id);
                rss.getRssID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rss with id " + id + " no longer exists.", enfe);
            }
            em.remove(rss);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RssJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Rss> findRssEntities() {
        return findRssEntities(true, -1, -1);
    }

    public List<Rss> findRssEntities(int maxResults, int firstResult) {
        return findRssEntities(false, maxResults, firstResult);
    }

    private List<Rss> findRssEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Rss.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Rss findRss(Integer id) {
        return em.find(Rss.class, id);
    }

    public int getRssCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Rss> rt = cq.from(Rss.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * This method find the one rss link that is activated.
     *
     * @return String
     * @author Alex Bellerive
     */
    public String getRSSLink() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Rss> rss = cq.from(Rss.class);

        cq.where(cb.equal(rss.get("active"), 1));

        TypedQuery<Rss> query = em.createQuery(cq);
        cq.select(rss);
        Rss rssFromDb = query.getSingleResult();

        return rssFromDb.getLink();
    }

    /**
     * This method returns a list of stories to display on the index
     *
     * @return List of RSSItem objects
     * @author Alex Bellerive
     */
    public List<RSSItem> getStories() {
        List<RSSItem> stories = new ArrayList<>();
        int limit = 3;

        String urlFromDb = getRSSLink();

        try {
            URL url = new URL(urlFromDb);
            stories = readUrl(url, limit);
            return stories;
        } catch (MalformedURLException ex) {
            LOG.error("An error occured while making the url " + ex.getMessage());
        }
        RSSItem t = new RSSItem("NoLink", "No Title", "Error getting news story");
        stories.add(t);
        return stories;
    }

    /**
     * This method parses the rss feed from the link and creates and object that
     * contains all that is needed to display the rss on index
     *
     * @param url
     * @param limit
     * @return List<RSSItem>
     * @author Alex Bellerive
     */
    private List<RSSItem> readUrl(URL url, int limit) {
        ArrayList<RSSItem> stories = new ArrayList<>();

        String title = "";
        String link = "";
        String desc = "";
        int current = 0;

        try ( BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (current == limit) {
                    break;
                }
                //These 'contain' if statements check the tags of the current line 
                // depending on what the line contains it will get that information
                // and save it into a variable by substring-ing it 
                if (line.contains("<title>")) {
                    int titleStart = line.indexOf("<title>") + 7;
                    int titleEnd = line.indexOf("</title>");
                    title = line.substring(titleStart, titleEnd);
                } else if (line.contains("<link>")) {
                    int linkStart = line.indexOf("<link>") + 6;
                    int linkEnd = line.indexOf("</link>");
                    link = line.substring(linkStart, linkEnd);
                } else if (line.contains("<description>")) {
                    int desStart = line.indexOf("<description>") + 13;
                    int desEnd = line.indexOf("</description>");
                    desc = line.substring(desStart, desEnd);
                } //End of the <item> tag so its the end of that story
                else if (line.contains("</item>")) {
                    RSSItem story = new RSSItem(link, title, desc);
                    stories.add(story);
                    current++;
                }
            }
        } catch (IOException ex) {
            LOG.error("An IOException occured while parsing the RSS feed" + ex.getMessage());
        }
        return stories;
    }

    /**
     * This method makes sure only one rss is activated at a time
     *
     * @param rssId
     * @author Alex Bellerive
     */
    public void activateLink(Integer rssId) {
        List<Rss> allStories = findRssEntities();

        for (Rss story : allStories) {
            if (Objects.equals(story.getRssID(), rssId)) {
                story.setActive(1);
            } else if (!Objects.equals(story.getRssID(), rssId)) {
                story.setActive(0);
            }
            edit(story);
        }
    }

    /**
     * This mehtod creates a new rss into the database
     * @param newLink
     * @author Alex Bellerive
     */
    public void addNewLink(String newLink) {
        Rss newRss = new Rss();
        newRss.setLink(newLink);
        newRss.setActive(0);

        List<Rss> allLinks = findRssEntities();
        Integer newId = allLinks.get(allLinks.size() - 1).getRssID() + 1;

        newRss.setRssID(newId);
        create(newRss);
    }

}
