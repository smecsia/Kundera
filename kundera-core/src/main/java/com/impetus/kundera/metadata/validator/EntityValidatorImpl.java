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
package com.impetus.kundera.metadata.validator;

import com.impetus.kundera.client.ClientResolver;
import com.impetus.kundera.configure.schema.api.SchemaManager;
import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.model.EntityMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.impetus.kundera.utils.ReflectUtils.collectFieldsInClassHierarchy;

/**
 * Validates entity for JPA rules.
 * 
 * @author animesh.kumar
 */
public class EntityValidatorImpl implements EntityValidator
{

    /** The Constant log. */
    private static final Log log = LogFactory.getLog(EntityValidatorImpl.class);

    /** cache for validated classes. */
    private List<Class<?>> classes = new ArrayList<Class<?>>();

    /**
     * Checks the validity of a class for Cassandra entity.
     * 
     * @param clazz
     *            validates this class
     * 
     * @return returns 'true' if valid
     */
    @Override
    // TODO: reduce Cyclomatic complexity
    public final void validate(final Class<?> clazz)
    {

        if (classes.contains(clazz))
        {
            return;
        }

        log.debug("Validating " + clazz.getName());

        // Is Entity?
        if (!clazz.isAnnotationPresent(Entity.class))
        {
            throw new InvalidEntityDefinitionException(clazz.getName() + " is not annotated with @Entity");
        }

        // Must be annotated with @Table
        if (!clazz.isAnnotationPresent(Table.class))
        {
            throw new InvalidEntityDefinitionException(clazz.getName() + " must be annotated with @Table");
        }

        // must have a default no-argument constructor
        try
        {
            clazz.getConstructor();
        }
        catch (NoSuchMethodException nsme)
        {
            throw new InvalidEntityDefinitionException(clazz.getName()
                    + " must have a default no-argument constructor.");
        }

        // Check for @Key and ensure that there is just 1 @Key field of String
        // type.
        List<Field> keys = new ArrayList<Field>();
        for (Field field : collectFieldsInClassHierarchy(clazz, MappedSuperclass.class, Inheritance.class))
        {
            if(field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(EmbeddedId.class))
            {
                throw new InvalidEntityDefinitionException(clazz.getName() + " must have either @Id field or @EmbeddedId field");
            }
            
            if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class))
            {
                keys.add(field);
            }
        }

        if (keys.size() == 0)
        {
            throw new InvalidEntityDefinitionException(clazz.getName() + " must have an @Id field.");
        }
        else if (keys.size() > 1)
        {
            throw new InvalidEntityDefinitionException(clazz.getName() + " can only have 1 @Id field.");
        }

        // if (!keys.get(0).getType().equals(String.class))
        // {
        // throw new PersistenceException(clazz.getName() +
        // " @Id must be of String type.");
        // }

        // save in cache

        classes.add(clazz);
    }

    @Override
    public void validateEntity(Class<?> clazz)
    {
        EntityMetadata metadata = KunderaMetadataManager.getEntityMetadata(clazz);
        if (metadata != null)
        {
            SchemaManager schemaManager = ClientResolver.getClientFactory(metadata.getPersistenceUnit())
                    .getSchemaManager();
            if (!schemaManager.validateEntity(clazz))
            {
                log.warn("Validation for : " + clazz + " failed , any operation on this class will result in fail.");
            }
        }
    }
}
