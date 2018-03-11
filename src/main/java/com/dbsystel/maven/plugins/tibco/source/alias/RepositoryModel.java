package com.dbsystel.maven.plugins.tibco.source.alias;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 
 * This class will
 * <ul>
 * <li>unmarshall XML file of the schema with "http://www.tibco.com/xmlns/repo/types/2002" namespace to JAXB objects.</li>
 * <li>
 * marshall the {@link Repository} object back to XML file with the same schema.</li>
 * </ul>
 * 
 *
 */
public class RepositoryModel {
    //  private static final String REPOSITORY_NAMESPACE = "http://www.tibco.com/xmlns/repo/types/2002";

    private Repository repository;

    public Repository getRepository() {
        return repository;
    }

    private JAXBContext jaxbContext;

    private File xmlFile;

    public RepositoryModel(File xmlFile) throws JAXBException {
        this.xmlFile = xmlFile;
        initRepositoryModel();
    }

    /**
     * <p>
     * This will initialize the {@link Repository} object which is a JAXB representation of the "Repository"
     * root-element of TIBCO ".aliaslib" files using the schema with "http://www.tibco.com/xmlns/repo/types/2002"
     * namespace.
     * </p>
     *
     * @throws JAXBException
     */
    private void initRepositoryModel() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        repository = (Repository) jaxbUnmarshaller.unmarshal(xmlFile);
        //repository = (Repository) o;
    }

    /**
     * <p>
     * This will marshall the object back to the XML file.
     * </p>
     * 
     * @throws JAXBException
     */
    public void save() throws JAXBException {
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(repository, xmlFile);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(xmlFile.getAbsolutePath());
        buf.append(", Repository:" + getRepository().toString());
        return buf.toString();
    }
}
