package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.beans.ManagerOrderBackingBean;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ga4w20.bookazon.entities.Account;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Booksale;
import com.ga4w20.bookazon.entities.Sale;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.LoggerFactory;

/**
 * Controller used to manipulate sales data.
 *
 * @author Grant
 */
@Named
@SessionScoped
public class SaleJpaController implements Serializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SaleJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private BooksaleJpaController bsjc;

    public void create(Sale sale) {
        try {
            if (sale.getBooksaleList() == null) {
                sale.setBooksaleList(new ArrayList<Booksale>());
            }

            utx.begin();
            Account accountID = sale.getAccountID();
            if (accountID != null) {
                accountID = em.getReference(accountID.getClass(), accountID.getAccountID());
                sale.setAccountID(accountID);
            }
            List<Booksale> attachedBooksaleList = new ArrayList<Booksale>();
            for (Booksale booksaleListBooksaleToAttach : sale.getBooksaleList()) {
                booksaleListBooksaleToAttach = em.getReference(booksaleListBooksaleToAttach.getClass(), booksaleListBooksaleToAttach.getBookSaleID());
                attachedBooksaleList.add(booksaleListBooksaleToAttach);
            }
            sale.setBooksaleList(attachedBooksaleList);
            em.persist(sale);
            if (accountID != null) {
                accountID.getSaleList().add(sale);
                accountID = em.merge(accountID);
            }
            for (Booksale booksaleListBooksale : sale.getBooksaleList()) {
                Sale oldSaleIDOfBooksaleListBooksale = booksaleListBooksale.getSaleID();
                booksaleListBooksale.setSaleID(sale);
                booksaleListBooksale = em.merge(booksaleListBooksale);
                if (oldSaleIDOfBooksaleListBooksale != null) {
                    oldSaleIDOfBooksaleListBooksale.getBooksaleList().remove(booksaleListBooksale);
                    oldSaleIDOfBooksaleListBooksale = em.merge(oldSaleIDOfBooksaleListBooksale);
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(SaleJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Sale sale) throws IllegalOrphanException {
        try {
            utx.begin();
            Sale persistentSale = em.find(Sale.class, sale.getSaleID());
            Account accountIDOld = persistentSale.getAccountID();
            Account accountIDNew = sale.getAccountID();
            List<Booksale> booksaleListOld = persistentSale.getBooksaleList();
            List<Booksale> booksaleListNew = sale.getBooksaleList();
            List<String> illegalOrphanMessages = null;
            for (Booksale booksaleListOldBooksale : booksaleListOld) {
                if (!booksaleListNew.contains(booksaleListOldBooksale)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Booksale " + booksaleListOldBooksale + " since its saleID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (accountIDNew != null) {
                accountIDNew = em.getReference(accountIDNew.getClass(), accountIDNew.getAccountID());
                sale.setAccountID(accountIDNew);
            }
            List<Booksale> attachedBooksaleListNew = new ArrayList<Booksale>();
            for (Booksale booksaleListNewBooksaleToAttach : booksaleListNew) {
                booksaleListNewBooksaleToAttach = em.getReference(booksaleListNewBooksaleToAttach.getClass(), booksaleListNewBooksaleToAttach.getBookSaleID());
                attachedBooksaleListNew.add(booksaleListNewBooksaleToAttach);
            }
            booksaleListNew = attachedBooksaleListNew;
            sale.setBooksaleList(booksaleListNew);
            sale = em.merge(sale);
            if (accountIDOld != null && !accountIDOld.equals(accountIDNew)) {
                accountIDOld.getSaleList().remove(sale);
                accountIDOld = em.merge(accountIDOld);
            }
            if (accountIDNew != null && !accountIDNew.equals(accountIDOld)) {
                accountIDNew.getSaleList().add(sale);
                accountIDNew = em.merge(accountIDNew);
            }
            for (Booksale booksaleListNewBooksale : booksaleListNew) {
                if (!booksaleListOld.contains(booksaleListNewBooksale)) {
                    Sale oldSaleIDOfBooksaleListNewBooksale = booksaleListNewBooksale.getSaleID();
                    booksaleListNewBooksale.setSaleID(sale);
                    booksaleListNewBooksale = em.merge(booksaleListNewBooksale);
                    if (oldSaleIDOfBooksaleListNewBooksale != null && !oldSaleIDOfBooksaleListNewBooksale.equals(sale)) {
                        oldSaleIDOfBooksaleListNewBooksale.getBooksaleList().remove(booksaleListNewBooksale);
                        oldSaleIDOfBooksaleListNewBooksale = em.merge(oldSaleIDOfBooksaleListNewBooksale);
                    }
                }
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(SaleJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, IllegalOrphanException {
        try {
            utx.begin();
            Sale sale;
            try {
                sale = em.getReference(Sale.class, id);
                sale.getSaleID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sale with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Booksale> booksaleListOrphanCheck = sale.getBooksaleList();
            for (Booksale booksaleListOrphanCheckBooksale : booksaleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Sale (" + sale + ") cannot be destroyed since the Booksale " + booksaleListOrphanCheckBooksale + " in its booksaleList field has a non-nullable saleID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Account accountID = sale.getAccountID();
            if (accountID != null) {
                accountID.getSaleList().remove(sale);
                accountID = em.merge(accountID);
            }
            em.remove(sale);
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(SaleJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Sale> findSaleEntities() {
        return findSaleEntities(true, -1, -1);
    }

    public List<Sale> findSaleEntities(int maxResults, int firstResult) {
        return findSaleEntities(false, maxResults, firstResult);
    }

    private List<Sale> findSaleEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Sale.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Sale findSale(Integer id) {
        return em.find(Sale.class, id);
    }

    public int getSaleCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Sale> rt = cq.from(Sale.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Method that buys books and persists the record to the database (with
     * booksaleList too)
     *
     * @author Grant 
     * @param books
     * @param gst
     * @param hst
     * @param pst
     * @param account
     */
    public void buyBooks(List<Book> books, double gst, double hst, double pst, Account account) {
        //One big sale (receipt) on all books bought in one transaction;
        Sale sale = new Sale();
        sale.setAccountID(account);
        //Will get edited
        sale.setPrice(0);
        sale.setSaleDate(new Date());
        this.create(sale);

        double finalPrice = 0.0;
        List<Booksale> bookSales = new ArrayList<>();

        for (int i = 0; i < books.size(); i++) {
            //Only creates a sale if the user has not already purchased the book.
            Booksale booksale = this.findBookSale(books.get(i).getIsbn(), account.getAccountID());
            if (booksale == null) {
                booksale = new Booksale();
                //Sets the book OBJECT in the bridging table
                booksale.setIsbn(books.get(i));
                //The books were part of the same sale instance
                booksale.setSaleID(sale);
                //Create the booksale record in the bridge
                this.bsjc.create(booksale);
                //Populate the list of bookSales
                bookSales.add(booksale);
                finalPrice += books.get(i).getListPrice();
            }
        }

        finalPrice = (finalPrice
                + (Math.round((finalPrice * hst / 100.0) * 100.0) / 100.0)
                + (Math.round((finalPrice * gst / 100.0) * 100.0) / 100.0)
                + (Math.round((finalPrice * pst / 100.0) * 100.0) / 100.0));

        sale.setPrice(finalPrice);
        sale.setBooksaleList(bookSales);
        try {
            this.edit(sale);
        } catch (IllegalOrphanException ex) {
            LOG.error(ex.getMessage());
        }
    }

    /**
     * Method used to get total sales of an account
     *
     * @author Grant 
     * @param accountID
     * @return double
     */
    public double getClientSales(int accountID) {
        List<Sale> sales = getSales(accountID);
        double totalSales = 0;

        for (Sale sale : sales) {
            totalSales += sale.getPrice();
        }

        return totalSales;
    }

    /**
     * Method that gets the books of a sale
     *
     * @author Grant 
     * @param saleID
     * @return
     */
    public List<Book> getInvoiceBooks(int saleID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Sale.class);
        Root<Sale> sale = cq.from(Sale.class);
        Join booksales = sale.join("booksaleList");
        cq.where(cb.equal(sale.get("saleID"), saleID));
        cq.select(booksales.get("isbn"));

        TypedQuery<Book> booksInsideInvoice = em.createQuery(cq);
        return booksInsideInvoice.getResultList();
    }

    /**
     * Method that gets all invoices of a client
     *
     * @author Grant 
     * @param accountID
     * @return
     */
    public List<Sale> getAllInvoicesOfAccount(int accountID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Sale.class);
        Root<Sale> sale = cq.from(Sale.class);
        cq.where(cb.equal(sale.get("accountID").get("accountID"), accountID));
        cq.orderBy(cb.desc(sale.get("saleID")));

        TypedQuery<Sale> salesRelatedToAccount = em.createQuery(cq);
        return salesRelatedToAccount.getResultList();
    }

    /**
     * Private method used to query the database and get the sales related to an
     * account
     *
     * @author Grant 
     * @param accountID
     * @return
     */
    private List<Sale> getSales(int accountID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Sale.class);
        Root<Sale> sale = cq.from(Sale.class);
        cq.where(cb.equal(sale.get("accountID").get("accountID"), accountID));
        cq.select(sale);

        TypedQuery<Sale> listOfSales = em.createQuery(cq);
        List<Sale> salesRelatedToAccount = listOfSales.getResultList();
        return salesRelatedToAccount;
    }

    /**
     * Private method used to check if a user has already bought a book they're
     * trying to buy
     *
     * @author Grant 
     * @param isbn
     * @param accountID
     * @return
     */
    private Booksale findBookSale(String isbn, int accountID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Booksale.class);
        Root<Booksale> bs = cq.from(Booksale.class);
        Join sale = bs.join("saleID");
        cq.select(bs);
        cq.where(
                cb.equal(bs.get("isbn").get("isbn"), isbn),
                cb.equal(sale.get("accountID").get("accountID"), accountID)
        );

        TypedQuery<Booksale> query = em.createQuery(cq);
        //May return null if an account did not buy a book
        List<Booksale> booksale = query.getResultList();
        if (booksale == null || booksale.isEmpty()) {
            return null;
        } else {
            return booksale.get(0);
        }
    }

    public List<Sale> topSales(Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Sale.class);

        Root<Sale> sale = cq.from(Sale.class);
        if (start != null && end != null) {
            cq.where(
                    cb.between(sale.<Date>get("saleDate"), start, end)
            );
        }
        cq.orderBy(cb.desc(sale.get("price")));

        cq.select(sale.get("accountID")).distinct(true);

        TypedQuery<Sale> query = em.createQuery(cq);

        return query.getResultList();
    }

    /**
     * this method will use three tables data
     * Sale, Book, Booksale
     * creating data to new object called ManagerOrderBackingBean who saving 
     * saleID, ISBN, TITLE, accountID and bookSaleID
     * @param 
     * @return mob
     * @author Lin Yang
     */
    public List<ManagerOrderBackingBean> salesOrder() {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ManagerOrderBackingBean> cq = cb.createQuery(ManagerOrderBackingBean.class);

        Root<Booksale> bookSale = cq.from(Booksale.class);
        Join<Booksale, Sale> saleToBs = bookSale.join("saleID");
        Join<Booksale, Book> bsTob = bookSale.join("isbn");
        cq.where(cb.notEqual(saleToBs.get("price"), 0));
        cq.orderBy(cb.asc(saleToBs.get("saleID")));
        cq.select(cb.construct(ManagerOrderBackingBean.class, saleToBs.get("saleID"), bsTob.get("isbn"), bsTob.get("title"), saleToBs.get("accountID"),bookSale.get("bookSaleID")));
        TypedQuery<ManagerOrderBackingBean> query = em.createQuery(cq);
        List<ManagerOrderBackingBean> mob = query.getResultList();
        return mob;
    }

}
