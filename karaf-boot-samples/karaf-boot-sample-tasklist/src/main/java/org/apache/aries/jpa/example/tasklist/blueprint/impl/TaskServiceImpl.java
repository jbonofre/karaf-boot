/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIESOR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.jpa.example.tasklist.blueprint.impl;

import java.util.Collection;

import javax.persistence.criteria.CriteriaQuery;

import org.apache.aries.jpa.example.tasklist.model.Task;
import org.apache.aries.jpa.example.tasklist.model.TaskService;
import org.apache.aries.jpa.template.JpaTemplate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class TaskServiceImpl implements TaskService {

    @Reference(target = "(osgi.unit.name=tasklist)")
    JpaTemplate jpa;

    @Override
    public Task getTask(Integer id) {
        return jpa.txExpr(em -> em.find(Task.class, id));
    }

    @Override
    public void addTask(Task task) {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Id property must be set");
        }
        System.err.println("Adding task " + task.getId());
        jpa.tx(em -> {
            em.persist(task);
            em.flush();
        });
    }

    @Override
    public Collection<Task> getTasks() {
        return jpa.txExpr(em -> {
            CriteriaQuery<Task> query = em.getCriteriaBuilder().createQuery(Task.class);
            return em.createQuery(query.select(query.from(Task.class))).getResultList();
        });
    }

    @Override
    public void updateTask(Task task) {
        jpa.tx(em -> em.merge(task));
    }

    @Override
    public void deleteTask(Integer id) {
        jpa.tx(em -> em.remove(getTask(id)));
    }

}
