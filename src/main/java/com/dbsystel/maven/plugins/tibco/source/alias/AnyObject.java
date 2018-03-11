package com.dbsystel.maven.plugins.tibco.source.alias;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;

/**
 * <p>
 * Classe Java pour anyObject complex type.
 * Class is not in use. @see dev-platform-descriptors
 * </p>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "anyObject", propOrder = { "any" })
@XmlSeeAlso({ Designer.class })
public class AnyObject extends Object {

    @XmlAnyElement
    protected List<Element> any;

    /**
     * Gets the value of the any property.
     * 
     * 
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the any property.
     * 
     * 
     * For example, to add a new item, do as follows:
     * 
     * 
     * getAny().add(newItem);
     * 
     * 
     * 
     * 
     * Objects of the following type(s) are allowed in the list {@link Element }
     * 
     * 
     */
    public List<Element> getAny() {
        if (any == null) {
            any = new ArrayList<Element>();
        }
        return this.any;
    }

}
