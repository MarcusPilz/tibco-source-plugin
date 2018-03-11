package com.dbsystel.maven.plugins.tibco.source.alias;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Name {

    @XmlAttribute
    protected String name;

    @XmlElement(name = "FILE_ALIASES_LIST")
    protected String filealiaseslist;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getFILEALIASESLIST() {
        return filealiaseslist;
    }

    public void setFILEALIASESLIST(String value) {
        this.filealiaseslist = value;
    }

}