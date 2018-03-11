/**
 * (C) Copyright 2011-2015 FastConnect SAS
 * (http://www.fastconnect.fr/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.09.10 à 11:00:34 AM CEST 
//

package com.dbsystel.maven.plugins.tibco.source.alias;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Classe Java pour anonymous complex type.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "globalVariable" })
@XmlRootElement(name = "globalVariables")
public class GlobalVariables {

    protected List<GlobalVariables.GlobalVariable> globalVariable;

    /**
     * Gets the value of the globalVariable property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the globalVariable property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getGlobalVariable().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link GlobalVariables.GlobalVariable }
     * 
     * 
     */
    public List<GlobalVariables.GlobalVariable> getGlobalVariable() {
        if (globalVariable == null) {
            globalVariable = new ArrayList<GlobalVariables.GlobalVariable>();
        }
        return this.globalVariable;
    }

    /**
     * <p>
     * Classe Java pour anonymous complex type.
     * 
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class GlobalVariable {

        @XmlElement(required = true)
        protected String name;

        @XmlElement(required = true)
        protected String value;

        protected String description;

        protected Boolean deploymentSettable;

        protected Boolean serviceSettable;

        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String type;

        protected String constraint;

        protected Long modTime;

        /**
         * 
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getName() {
            return name;
        }

        /**
         *
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * 
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getValue() {
            return value;
        }

        /**
         * 
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         *
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getDescription() {
            return description;
        }

        /**
         *
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * 
         * 
         * @return possible object is {@link Boolean }
         * 
         */
        public Boolean isDeploymentSettable() {
            return deploymentSettable;
        }

        /**
         * 
         * 
         * @param value
         *            allowed object is {@link Boolean }
         * 
         */
        public void setDeploymentSettable(Boolean value) {
            this.deploymentSettable = value;
        }

        /**
         * 
         * 
         * @return possible object is {@link Boolean }
         * 
         */
        public Boolean isServiceSettable() {
            return serviceSettable;
        }

        /**
         * 
         * 
         * @param value
         *            allowed object is {@link Boolean }
         * 
         */
        public void setServiceSettable(Boolean value) {
            this.serviceSettable = value;
        }

        /**
         * Obtient la valeur de la propriété type.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getType() {
            return type;
        }

        /**
         * Définit la valeur de la propriété type.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * 
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getConstraint() {
            return constraint;
        }

        /**
         *
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setConstraint(String value) {
            this.constraint = value;
        }

        /**
         *
         * 
         * @return possible object is {@link Long }
         * 
         */
        public Long getModTime() {
            return modTime;
        }

        /**
         *
         * 
         * @param value
         *            allowed object is {@link Long }
         * 
         */
        public void setModTime(Long value) {
            this.modTime = value;
        }

    }

}
