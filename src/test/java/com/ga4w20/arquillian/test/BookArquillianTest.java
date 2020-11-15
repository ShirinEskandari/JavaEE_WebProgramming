package com.ga4w20.arquillian.test;

import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Publisher;
import com.ga4w20.bookazon.persistence.BookJpaController;
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Grant & Alex
 */
@RunWith(Arquillian.class)
public class BookArquillianTest {

    private final static Logger LOG = LoggerFactory.getLogger(BookArquillianTest.class);

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
    private BookJpaController bookController;

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
     * Method that tests the JPA provided method to create and persist a book to
     * the database.
     */
    @Test
    public void testCreateBook() {
        LOG.info("testCreateBook");
        Book book = createBookObj();

        bookController.create(book);

        Book book1 = bookController.findBook(book.getBookID());

        LOG.debug("ID " + book.getBookID());

        assertEquals("Not same the same", book1, book);
    }

    /**
     * Method that tests the JPA provided method to delete and persists a book
     * record in the database.
     */
    @Test
    public void testDeleteBook() {
        LOG.info("testDeleteBook");
        try {
            //Find the last book in the database (There's 100 books)
            Book book = bookController.findBook(100);
            bookController.destroy(book.getBookID());
            List<Book> books = bookController.findBookEntities();
            assertThat(books).hasSize(99);
        } catch (NonexistentEntityException | IllegalOrphanException ex) {
            LOG.debug(ex.getMessage());
        }
    }

    /**
     * Method that checks that the bookstore database has the correct amount of
     * records (100), given a seedDatabase script.
     */
    @Test
    public void should_find_all_books() {
        LOG.info("should_find_all_books");
        List<Book> books = bookController.findBookEntities();
        assertThat(books).hasSize(100);
    }

    /**
     * Private method used to create a book object, used in methods that need to
     * create and persist a new book record for the sake of that test case.
     *
     * @return
     */
    private Book createBookObj() {
        Book book = new Book();
        //All books are identified by their bookID, the ISBN being the same for all added books won't matter in this case
        book.setIsbn("111-1111111111");
        book.setTitle("Test Book Creation");
        book.setPages("555");
        book.setGenre("Cooking");
        book.setSynopsis("Random Synopsis to enter when creating books for arquilian testing.");
        book.setPublisherID(new Publisher(1));
        book.setPubDate(new Date());
        book.setWholeSalePrice(66.01);
        book.setListPrice(62.51);
        book.setSalePrice(41.15);
        book.setDateEntered(new Date());
        book.setEBook('1');
        book.setRemovalStatus('0');
        book.setImagePath("TestBook.jpg");

        return book;
    }


    /**
     * Method that tests if the ISBN matches given a search query on isbn.
     */
    @Test
    public void testSearchBarBooksByISBN(){
        LOG.info("testSearchBarBooksByISBN");
        //The Pizza Bible book with id 100
        Book book = bookController.findBook(100);
        //List of 1 book only, because ISBN's are supposedly unique, the provided ISBN is the ISBN of the book called "The Pizza Bible"
        List<Book> newBook = bookController.searchBarBooks("isbn", "978-1607746058");
        assertThat(newBook.get(0).getIsbn()).isEqualTo(book.getIsbn());
    }

    /**
     * Method that tests if the author's first name matches the book author's
     * first name, given a search query.
     */
    @Test
    public void testSearchBarBooksByAuthor_FirstName(){
        LOG.info("testSearchBarBooksByAuthor_FirstName");
        //The Pizza Bible book with id 100
        Book book = bookController.findBook(100);
        List<Book> newBook = bookController.searchBarBooks("author", "Tony");
        //Checks if the titles match after finding a book with the author first name "Tony
        assertThat(newBook.get(0).getTitle()).isEqualTo(book.getTitle());
    }

    /**
     * Method that tests if the author's last name matches the book author's
     * last name, given a search query.
     */
    @Test
    public void testSearchBarBooksByAuthor_LastName(){
        LOG.info("testSearchBarBooksByAuthor_LastName");
        //The Pizza Bible book with id 100
        Book book = bookController.findBook(100);
        List<Book> newBook = bookController.searchBarBooks("author", "Gemignani");
        //Checks if the titles match after finding a book with the author last name "Gemignani"
        assertThat(newBook.get(0).getTitle()).isEqualTo(book.getTitle());
    }

    /**
     * Method that tests if the author's full name matches the book author's
     * full name, given a search query.
     */
    @Test
    public void testSearchBarBooksByAuthor_FullName(){
        LOG.info("testSearchBarBooksByAuthor_FullName");
        //The Pizza Bible book with id 100
        Book book = bookController.findBook(100);
        List<Book> newBook = bookController.searchBarBooks("author", "Tony Gemignani");
        //Checks if the titles match after finding a book with the author full name "Tony Gemignani"
        assertThat(newBook.get(0).getTitle()).isEqualTo(book.getTitle());
    }

    /**
     * Method that tests if the authorID's match from a query execution given a
     * specific bookID.
     */
    @Test
    public void testAuthorIDFromBook(){
        LOG.info("testAuthorIDFromBook");
        //Gets The Pizza Bible book (id 100)
        Book book = bookController.findBook(100);
        //Gets author 64 (Tony Gemignani, the author of The Pizza Bible, is the 64th record)
        int authorID = bookController.getAuthorIDFromBook(book.getIsbn());

        assertThat(authorID).isEqualTo(64);
    }

    /**
     * Method that gets 20 books given a specified genre.
     */
    @Test
    public void testGetBooksByGenre(){
        LOG.info("testGetBooksByGenre");
        String genre = "Cooking";
        List<Book> books = bookController.getBooksByGenre(genre);
        //There's 20 books per genre, 19 being the last index
        assertThat(books.get(19).getGenre()).isEqualTo("Cooking");
    }

    /**
     * Method that checks if the 3rd book of the list (There's max 3 results),
     * has an author ID that matches the specified author ID.
     */
    @Test
    public void should_get_books_from_same_author() {
        LOG.info("should_get_books_from_same_author");
        //Rick Riordan, author ID 22, has 5 books in the DB, but query will only return 3
        List<Book> books = bookController.getBooksFromSameAuthor("Rick Riordan", 0);
        assertThat(books.get(2).getBookAuthorList().get(0).getAuthorID().getAuthorID()).isEqualTo(22);
    }

    /**
     * Method that gets three books from the same genre and checks if the
     * genre's indeed match.
     */
    @Test
    public void testGetThreeBooksFromGenre(){
        LOG.info("testGetThreeBooksFromGenre");
        //For say, based on the user's last click on a book who's genre was cooking
        String genre = "Cooking";

        List<Book> books = bookController.getThreeBooksByGenre(genre);
        assertThat(books.get(2).getGenre()).isEqualTo(genre);
    }
    
    /**
     * Method that tests bought books.
     */
    @Test
    public void testGetBoughtBooks(){
        LOG.info("testGetBoughtBooks");
        //The database seed inserts 2 books for account 2
        List<Book> books = bookController.getBoughtBooks(2);
        assertThat(books.size()).isEqualTo(2);
    }
  
    /**
     * Method that tests NoResultException thrown.
     */
    @Test(expected = NoResultException.class)
    public void shouldThrowNRException(){
        LOG.info("testThrowNRException");
        bookController.getAuthorIDFromBook("11111111111111");
    }
    
    /**
     * Method that tests EntityNotFoundException thrown.
     * @throws IllegalOrphanException
     * @throws NonexistentEntityException 
     */
    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowENFException() throws IllegalOrphanException, NonexistentEntityException{
        LOG.info("testThrowENFException");
        bookController.destroy(-1);
    }
    
    /**
     * Method that throws IllegalOrphanException test.
     * @throws IllegalOrphanException
     * @throws NonexistentEntityException
     * @throws Exception 
     */
    @Test(expected = IllegalOrphanException.class)
    public void shouldThrowIOException() throws IllegalOrphanException, NonexistentEntityException, Exception{
        LOG.info("testThrowIOException");
        bookController.destroy(1);
    }
    
    /*
    @Test
    public void shouldThrowNEEException(){
        assertThrows(NonexistentEntityException.class, () -> {
            bookController.edit(bookController.findBook(7));
        });
    }*/
    
    /**
     * This method tests if the method findAllBooks(char status) returns the
     * right amount of books depending on the status
     * @author Alex Bellerive
     */
    @Test
    public void testFindAllActiveBooks() {
        LOG.debug("testFindAllActiveBooks");
        Character active = '0';
        List<Book> allActiveBooks = this.bookController.findAllBooks(active);

        //All books currently in the database are active
        List<Book> allBooks = this.bookController.findBookEntities();

        assertEquals("Not the same ammount of books returned", allActiveBooks.size(), allBooks.size());
    }

    /**
     * This method tests if the method findAllBooks(char status) returns the
     * right amount of books depending on the status
     * @author Alex Bellerive
     */
    @Test
    public void testFindAllDeactivatedBooks() {
        LOG.debug("testFindAllActiveBooks");
        Character deactivated = '1';

        Book book = createBookObj();
        book.setRemovalStatus(deactivated);
        bookController.create(book);

        List<Book> allDeactivatedBooks = this.bookController.findAllBooks(deactivated);

        assertEquals("Only one deactivated book was created, should return 1", allDeactivatedBooks.size(), 1);
    }

    /**
     * This method toggleBook(String isbn, char status) changed the status of
     * the book to whatever specified, Active or Deactivated
     * @throws com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException
     * @author Alex Bellerive
     */
    @Test
    public void testToggleBook() throws IllegalOrphanException {
        LOG.debug("testToggleBook");

        List<Book> allActiveBooks = this.bookController.findAllBooks('0');
        this.bookController.toggleBook(allActiveBooks.get(0).getIsbn(), '1');
        this.bookController.toggleBook(allActiveBooks.get(1).getIsbn(), '1');
        this.bookController.toggleBook(allActiveBooks.get(2).getIsbn(), '1');
        this.bookController.toggleBook(allActiveBooks.get(3).getIsbn(), '1');
        this.bookController.toggleBook(allActiveBooks.get(4).getIsbn(), '1');
        
        List<Book> allActiveBooksPostToggle = this.bookController.findAllBooks('0');

        assertEquals("There was 5 books deactivated, the total deactivated should be 5 less", allActiveBooks.size(), allActiveBooksPostToggle.size());

    }

    /**
     * Testing the book from findBookIsbnis the same book in the database
     * @throws com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException
     */
    @Test
    public void testFindBookByIsbn() throws IllegalOrphanException {
        LOG.debug("testFindBookByIsbn");
        Book book = bookController.findBook(1);
        Book bookForTest = bookController.findBookByIsbn("978-0756404741");
        
        assertEquals(bookForTest.getTitle(),book.getTitle());
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
