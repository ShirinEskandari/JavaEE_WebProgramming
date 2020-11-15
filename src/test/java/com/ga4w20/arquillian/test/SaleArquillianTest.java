package com.ga4w20.arquillian.test;

import com.ga4w20.bookazon.beans.ManagerOrderBackingBean;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Sale;
import com.ga4w20.bookazon.persistence.AccountJpaController;
import com.ga4w20.bookazon.persistence.BookJpaController;
import com.ga4w20.bookazon.persistence.BooksaleJpaController;
import com.ga4w20.bookazon.persistence.SaleJpaController;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Arquillian testing for sales.
 * @author Grant
 */
@RunWith(Arquillian.class)
public class SaleArquillianTest {

    private final static Logger LOG = LoggerFactory.getLogger(SaleArquillianTest.class);

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
    private SaleJpaController saleController;
    
    @Inject
    private BookJpaController bookController;
    
    @Inject
    private AccountJpaController accountController;
    
    @Inject
    private BooksaleJpaController bookSaleController;

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
     * Method that tests buying books.
     */
    @Test
    public void testBuyBooks(){
        LOG.info("testBuyBooks");
        //Get the first book in the database
        Book book = bookController.findBook(1);
        //Have the first account in the database buy the book
        List<Book> books = new ArrayList<>();
        books.add(book);
        int salesBefore = bookSaleController.getBooksaleCount();
        saleController.buyBooks(books, 15, 0.0, 0.0, accountController.findAccount(1));
        int salesAfter = bookSaleController.getBooksaleCount();
        assertThat(salesBefore).isEqualTo(salesAfter - 1);
    }

    /**
     * Test method that tests client sales.
     */
    @Test
    public void testGetClientSales(){
        LOG.info("testGetClientSales");
        //The database is seeded with saleID #2, linked with account#10 with a sale value of 10.99
        //This getClientSales() method gets the sales of the account linked to ID 10 and should return 10.99;
        double salesPrice = saleController.getClientSales(10);
        assertThat(salesPrice).isEqualTo(10.99);
    }
    
    /**
     * Test method that tests if the method gets the books part of the invoice.
     */
    @Test
    public void testGetInvoiceBooks(){
        LOG.info("testGetInvoiceBooks");
        //The database is seeded with saleID #4, with account #4. The booksaleList should contain one single book
        List<Book> invoiceBooks = saleController.getInvoiceBooks(4);
        assertThat(invoiceBooks.get(0).getIsbn()).isEqualTo("978-1848312555");
    }
    
    /**
     * Test method that tests if the method gets all the invoices related to an account.
     */
    @Test
    public void testGetAllInvoicesOfAccount(){
        LOG.info("testGetAllInvoicesOfAccount");
        //The database is seeded with saleIDs #3 and #8, both linked to accountID #6
        List<Sale> invoices = saleController.getAllInvoicesOfAccount(6);
        assertThat(invoices.size()).isEqualTo(2);
    }
    
    /**
     * Test method that checks if the method gets all the sales from the database seed (which are essentially the top sales).
     */
    @Test
    public void testTopSales(){
        LOG.info("testTopSales");
        //The database seed adds a sale of the current date. The query should range between the start and end
        //meaning that it will look for sales between the range of null and null, in which there is a check that if the values are null
        //The query will just return all sales
        List<Sale> topSales = saleController.topSales(null, null);
        
        //The database is seeded with 10 sales, but 3 of them are related to other accounts. We only want distinct ones
        assertThat(topSales.size()).isEqualTo(7);
    }
    
    /**
     * Test method that checks if the method gets all the sales in order from the database.
     */
    @Test
    public void testSalesOrder(){
        LOG.info("testSalesOrder");
        List<ManagerOrderBackingBean> newList = saleController.salesOrder();
        //The database have 11 sales that price over $0. 
        assertThat(newList.size()).isEqualTo(11);
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
