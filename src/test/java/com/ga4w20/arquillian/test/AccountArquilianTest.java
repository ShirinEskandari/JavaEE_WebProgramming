package com.ga4w20.arquillian.test;

import com.ga4w20.bookazon.entities.Account;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.persistence.AccountJpaController;
import com.ga4w20.bookazon.persistence.BookJpaController;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import com.ga4w20.bookazon.persistence.exceptions.RollbackFailureException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;
import static org.assertj.core.api.Assertions.assertThat;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Shirin
 */
@RunWith(Arquillian.class)
public class AccountArquilianTest {

    private final static Logger LOG = LoggerFactory.getLogger(AccountArquilianTest.class);

    @Deployment
    public static WebArchive deploy() {

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
                .addAsResource("ValidationMessages.properties")
                .addAsLibraries(dependencies);

        return webArchive;
    }

    @Inject
    private AccountJpaController accountController;

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
     * create an Account object
     *
     * @author Shirin
     * @return
     */
    public Account createAccount() {
        Account account = new Account();
        account.setAddress("234cotedes neiges");
        account.setCellphone("5146643243");
        account.setCity("Montreal");
        account.setCompanyName("dd");
        account.setEmail("shirin5@gmail.com");
        account.setFName("shirin7");
        account.setLName("eskandari");
        account.setManagerStatus('0');
        account.setPostalCode("h3v2g3");
        account.setProvince("qc");
        account.setStreet("bates");
        account.setTelephone("5147766514");
        account.setHashedPassword("12345");
        account.setSalt("salt");

        accountController.create(account);
        return account;
    }

    /**
     * Method that tests the provided JPA method to create an account record and
     * persist to the database.
     *
     * @author Shirin
     */
    @Test
    public void testCreateAccount() {
        LOG.debug("testCreateaccount");

        int accountCount = accountController.getAccountCount();

        Account account;
        account = createAccount();

        assertThat(account.getAccountID()).isEqualTo(accountCount + 1);
    }

    /**
     * Method used to delete and persist an account record from the database.
     *
     * @author Shirin
     */
    @Test
    public void testDestroyAccount() {
        LOG.debug("testDestroyAccount");
        try {
            int firstAccountCount = accountController.getAccountCount();
            accountController.destroy(firstAccountCount - 1);
            int secondAccountCount = accountController.getAccountCount();
            assertThat(firstAccountCount).isEqualTo(secondAccountCount + 1);
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            LOG.debug(ex.getMessage());
        }
    }

    /**
     * Method that tests the find account with the email address
     *
     * @author Shirin
     */
    @Test
    public void testFindAccountByEmail() {
        Account account;
        account = createAccount();
        Account accountAfter = new Account();
        accountAfter = accountController.findAccountByEmail(account.getEmail());
        assertThat(account).isEqualTo(accountAfter);
    }

    /**
     * Method that checks that the bookstore database has the correct amount of
     * records (13), given a seedDatabase script.
     *
     * @author Shirin
     */
    @Test
    public void accountFindEnteties() {
        LOG.info("All account enteties");
        List<Account> accounts = accountController.findAccountEntities();
        assertThat(accounts).hasSize(13);
    }

    /**
     * Method that throws IllegalOrphanException test.
     *
     * @throws IllegalOrphanException
     * @throws NonexistentEntityException
     * @throws Exception
     * @author Shirin
     */
    @Test(expected = IllegalOrphanException.class)
    public void accountDestroyOrphanException() throws IllegalOrphanException, NonexistentEntityException, Exception {
        LOG.info("testThrowIOException");
        accountController.destroy(1);
    }

    /**
     * Method that tests EntityNotFoundException thrown.
     *
     * @throws IllegalOrphanException
     * @throws NonexistentEntityException
     * @author Shirin
     */
    @Test(expected = EntityNotFoundException.class)
    public void accountDestroyException() throws IllegalOrphanException, NonexistentEntityException {
        LOG.info("testThrowENFException");
        accountController.destroy(-1);
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
