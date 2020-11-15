package com.ga4w20.arquillian.test.parameterized;

import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Publisher;
import com.ga4w20.bookazon.persistence.BookJpaController;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import com.ga4w20.bookazon.persistence.exceptions.RollbackFailureException;
import com.kfstandard.business.test.parameterized.ParameterRule;
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parameterized Test class used for books.
 * @author Grant
 */
@RunWith(Arquillian.class)
public class BookTest {

    private final static Logger LOG = LoggerFactory.getLogger(BookTest.class);

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
                .addPackage(NonexistentEntityException.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addClass(ParameterRule.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/payara-resources.xml"), "payara-resources.xml")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource(new File("src/test/resources/log4j2.xml"), "log4j2.xml")
                .addAsResource("createBookStoreTables.sql")
                .addAsResource("insertAll.sql")
                .addAsLibraries(dependencies);

        return webArchive;
    }

    /**
     * The ParameterRule class does not support an array of values that are
     * assigned by the test class constructor as regular parameterized are done.
     *
     * Rule for Recently Added Book Test
     */
    @Rule
    public ParameterRule rule = new ParameterRule("dynamic",
            //We know the database will be seeded with 100 books, so the ID's start at 101
            new Book(101, "151-1748591029", "Test Book Recently Added", "555", "Cooking", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(1)),
            new Book(102, "141-1111111111", "What is this book called?", "555", "Cooking", "Random Synopsis to enter when creating books for arquilian testing.Will Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 characters", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(5)),
            new Book(103, "211-2222222222", "You Name it", "555", "Cooking", "Random Synopsis Will Copy and paste to get more than 100 characters TESTTESTESTESTto enter when creating books for arquilian testing. Needs to be more than 100 characters so i'm going to copy and paste this another time.Needs to be more than 100 characters so i'm going to copy and paste this another time.", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(17)),
            new Book(104, "814-5132124311", "Test Book drM", "415", "Cooking", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbsssssssssssss", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(14)),
            new Book(105, "711-1010101010", "TitleTielTielt", "15", "Cooking", "Random Synopsis to enter when creating bookWill Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 characters", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(7)),
            new Book(106, "922-4150982756", "DawsonCollege Book", "556", "Cooking", "Random Synopsis to enter when creaWill Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 charactersor arquilian testing.", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(21)),
            new Book(107, "111-1231231231", "Title1", "15", "Cooking", "Random Synopsis to enter when creating books for arquilian testing.Will Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 characters", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(8)),
            new Book(108, "222-2211445511", "Random test for rule 4", "556", "Cooking", "Random Synopsis to enter when creating bWill Copy and paste to get more than 100 charactersooks for arquilian testing.", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(4)),
            new Book(109, "111-4999999999", "Title1", "15", "Cooking", "Random Synopsis to enterWill Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 characters when creating books for arquilian testing.", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(1)),
            new Book(110, "222-0000000000", "Random test for rule 4", "556", "Cooking", "Random Synopsis to enter when creating Will Copy and paste to get more than 100 charactersWill Copy and paste to get more than 100 charactersbooks for arquilian testing.", new Date(), 10.41, 5.55, 5.43, new Date(), (Character) '1', (Character) '0', "random.jpg", new Publisher(2))
    );

    private Book dynamic;

    @Inject
    private BookJpaController bookController;

    @Inject
    private Book book;

    @Resource(lookup = "java:app/jdbc/bookstore")
    private DataSource ds;

    /**
     * This routine is courtesy of Bartosz Majsak who also solved my Arquillian
     * remote server problem
     */
    @Before
    public void seedDatabase() {
        LOG.debug("Seeding database...");
        final String createDBTables = loadAsString("createBookStoreTables.sql");
        final String seedDataScript = loadAsString("insertAll.sql");

        try (Connection connection = ds.getConnection()) {
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
     * Method that adds three new books, with the dateEntered set to a year
     * ahead of time, and checks if the query returns those 3 exact same books.
     */
    @Test
    public void should_get_books_recently_added() {
        LOG.debug("should_get_books_recently_added");
        //Create three books of today's date (should be the three newly added)
        book = dynamic;
        Calendar c = Calendar.getInstance();
        //Today's date
        c.setTime(new Date());
        //Adds a year
        c.add(Calendar.DATE, 365);
        book.setDateEntered(c.getTime());
        bookController.create(book);

        List<Book> books = bookController.getBooksRecentlyAdded();
        assertThat(books).contains(book);
    }

    /**
     * Method that tests and checks if the proper string was appended to a
     * synopsis of a book if its length > 100 characters.
     */
    @Test
    public void testDisplayReadMore() {
        LOG.debug("testDisplayReadMore");

        book = dynamic;
        String newSynopsis = bookController.displayReadMore(book.getSynopsis());

        assertThat(newSynopsis).contains(" [View Book To Read More!]");
    }


    /**
     * Method that tests if the ISBN matches given a search query on isbn.
     */
    @Test
    public void testSearchBarBooksByISBN() {
        LOG.debug("testSearchBarBooksByISBN");

        book = dynamic;
        bookController.create(book);
        List<Book> newBooks = bookController.searchBarBooks("isbn", book.getIsbn());

        assertThat(newBooks.get(0).getIsbn()).isEqualTo(book.getIsbn());
    }
    
     /**
     * Method that tests if the Title matches given a search query on title.
     */
    @Test
    public void testSearchBarBooksByTitle(){
        LOG.debug("testSearchBarBooksByTitle");
        
        book  = dynamic;
        bookController.create(book);
        List<Book> newBooks = bookController.searchBarBooks("title", book.getTitle());
        
        assertThat(newBooks.get(0).getTitle()).isEqualTo(book.getTitle());
    }
    
    /**
     * Method that tests search bar by publisher.
     */
    @Test
    public void testSearchBarBooksByPublisher(){
        LOG.debug("testSearchBarBooksByPublisher");
        
        book  = dynamic;
        bookController.create(book);
        List<Book> newBooks = bookController.searchBarBooks("publisher", book.getPublisherID().getName());
        
        assertThat(newBooks.get(0).getPublisherID().getName()).isEqualTo(book.getPublisherID().getName());
    }

    /**
     * The following methods support the seedDatabse method
     */
    private String loadAsString(final String path) {
        try (InputStream inputStream = Thread.currentThread()
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
