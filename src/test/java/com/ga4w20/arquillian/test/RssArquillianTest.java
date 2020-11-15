package com.ga4w20.arquillian.test;

import com.ga4w20.bookazon.beans.RSSItem;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Rss;
import com.ga4w20.bookazon.persistence.AuthorJpaController;

import com.ga4w20.bookazon.persistence.BookJpaController;
import com.ga4w20.bookazon.persistence.RssJpaController;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import com.ga4w20.bookazon.persistence.exceptions.RollbackFailureException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alex Bellerive
 */
@RunWith(Arquillian.class)
public class RssArquillianTest {

    private final static Logger LOG = LoggerFactory.getLogger(RssArquillianTest.class);

    @Deployment
    public static WebArchive deploy() {

        // Use an alternative to the JUnit assert library called AssertJ
        // Need to reference MySQL driver as it is not part of either
        // embedded or remote
        final File[] dependencies = Maven
                .resolver()
                .loadPomFromFile("pom.xml")
                .resolve("mysql:mysql-connector-java",
                        "org.assertj:assertj-core",
                        "org.slf4j:slf4j-api",
                        "org.apache.logging.log4j:log4j-slf4j-impl",
                        "org.apache.logging.log4j:log4j-web"
                ).withTransitivity()
                .asFile();

        // The webArchive is the special packaging of your project
        // so that only the test cases run on the server or embedded
        // container
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addPackage(BookJpaController.class.getPackage())
                .addPackage(Book.class.getPackage())
                .addPackage(RSSItem.class.getPackage())
                .addPackage(NonexistentEntityException.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/payara-resources.xml"), "payara-resources.xml")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource(new File("src/test/resources/log4j2.xml"), "log4j2.xml")
                .addAsResource("createBookStoreTables.sql")
                .addAsResource("insertAll.sql")
                .addAsLibraries(dependencies);

        return webArchive;
    }
    
    @Inject
    private RssJpaController rssController;
    
    @Resource(lookup = "java:app/jdbc/bookstore")
    private DataSource ds;

    /**
     * This routine is courtesy of Bartosz Majsak who also solved my Arquillian
     * remote server problem
     */
    @Before
    public void seedDatabase() {
        final String createDBTables = loadAsString("createBookStoreTables.sql");
        final String seedDataScript = loadAsString("insertAll.sql");

        try ( Connection connection = ds.getConnection()) {
            //connection.prepareStatement(createDBTables).execute();
            for (String statement : splitStatements(new StringReader(
                    createDBTables), ";")) {
                connection.prepareStatement(statement).execute();
            }

            for (String statement : splitStatements(new StringReader(
                    seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
    }

    /**
     * This method tests if it returns the proper link that has the active status
     */
    @Test
    public void testGetRssLink() {
        String link = this.rssController.getRSSLink();
        assertEquals(link, "https://www.nasa.gov/rss/dyn/breaking_news.rss");
    }
    
    /**
     * This method tests getStories() and reassures that this method 
     * returns an object that is set
     */
    @Test
    public void testGetStories(){
        List<RSSItem> stories = this.rssController.getStories();
        //This list shouldnt be empty 
        assertEquals(stories.isEmpty(),false);
    }
    
    /**
     * This method tests that a specific non active rss story becomes the 
     * new active rss story
     */
    @Test
    public void testActivateLink(){
        List<Rss> allLinks = this.rssController.findRssEntities();
        Rss nonActiveLink = allLinks.get(2);
        this.rssController.activateLink(nonActiveLink.getRssID());
        
        String activatedLink = this.rssController.getRSSLink();
        
        assertEquals(nonActiveLink.getLink(), activatedLink);
    }
    
    /**
     * This method tests the addNewLink(String newLink) method
     */
    @Test
    public void testAddNewLink(){
        String newLink = "https://www.techrepublic.com/rssfeeds/topic/apple/";
        List<Rss> allOldLinks = this.rssController.findRssEntities();
        
        this.rssController.addNewLink(newLink);
        List<Rss> allNewLinks = this.rssController.findRssEntities();
        
        assertThat(allOldLinks).isNotEqualTo(allNewLinks);
    }

    /**
     * The following methods support the seedDatabse method
     */
    private String loadAsString(final String path) {
        try ( InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(path)) {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    /**
     * Private method provided by Ken used to split a provided script into a
     * list of strings, essentially a list of commands to execute.
     *
     * @param reader
     * @param statementDelimiter
     * @return
     */
    private List<String> splitStatements(Reader reader,
            String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
            return statements;
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing sql", e);
        }
    }

    /**
     * Private method provided by Ken that checks if a line is a comment inside
     * of an sql script.
     *
     * @param line
     * @return
     */
    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//")
                || line.startsWith("/*");
    }
}
