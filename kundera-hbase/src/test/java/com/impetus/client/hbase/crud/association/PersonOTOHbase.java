/*******************************************************************************
 * * Copyright 2012 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.client.hbase.crud.association;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.impetus.client.twitter.entities.PersonalDetailHbase;

/**
 * The Class Person.
 */
@Entity
@Table(name = "PERSON", schema = "KunderaExamples@hbaseTest")
public class PersonOTOHbase
{

    /** The person id. */
    @Id
    @Column(name = "PERSON_ID")
    private String personId;

    /** The person name. */
    @Column(name = "PERSON_NAME")
    private String personName;

    /** The age. */
    @Column(name = "AGE")
    private Integer age;

    @Embedded
    private PersonalDetailHbase personalDetail;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressOTOHbase address;

    public PersonOTOHbase(String personId)
    {
        this.personId = personId;
    }

    public PersonOTOHbase()
    {

    }

    /**
     * Gets the person id.
     * 
     * @return the person id
     */
    public String getPersonId()
    {
        return personId;
    }

    /**
     * Gets the person name.
     * 
     * @return the person name
     */
    public String getPersonName()
    {
        return personName;
    }

    /**
     * Sets the person name.
     * 
     * @param personName
     *            the new person name
     */
    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    /**
     * Sets the person id.
     * 
     * @param personId
     *            the new person id
     */
    public void setPersonId(String personId)
    {
        this.personId = personId;
    }

    /**
     * @return the age
     */
    public Integer getAge()
    {
        return age;
    }

    /**
     * @param age
     *            the age to set
     */
    public void setAge(int age)
    {
        this.age = age;
    }

    public AddressOTOHbase getAddress()
    {
        return address;
    }

    public void setAddress(AddressOTOHbase address)
    {
        this.address = address;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    /**
     * @return the personalDetail
     */
    public PersonalDetailHbase getPersonalDetail()
    {
        return personalDetail;
    }

    /**
     * @param personalDetail
     *            the personalDetail to set
     */
    public void setPersonalDetail(PersonalDetailHbase personalDetail)
    {
        this.personalDetail = personalDetail;
    }

}
