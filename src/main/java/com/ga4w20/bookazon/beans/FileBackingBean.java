package com.ga4w20.bookazon.beans;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.LoggerFactory;

/**
 * Backing Bean for Files in the client.xhtml when a client wants to download the books he bought
 * @author Grant
 */
@Named
@RequestScoped
public class FileBackingBean {
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(FileBackingBean.class);
    private StreamedContent file;

    public FileBackingBean() throws IOException {
        try {
            LOG.info("Inside file backing bean!");
            InputStream response = new URL("https://ia800700.us.archive.org/35/items/toilersofsea01hugouoft/toilersofsea01hugouoft.pdf").openStream();
            file = new DefaultStreamedContent(response, "application/pdf", "WebProject.pdf");
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
    }

    public StreamedContent getFile() {
        return this.file;
    }
}
