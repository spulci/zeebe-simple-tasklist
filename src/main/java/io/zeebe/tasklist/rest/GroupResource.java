/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
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
package io.zeebe.tasklist.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.zeebe.tasklist.entity.GroupEntity;
import io.zeebe.tasklist.repository.GroupRepository;
import io.zeebe.tasklist.repository.TaskRepository;

@RestController
@RequestMapping("/api/groups")
public class GroupResource {

  @Autowired private GroupRepository groupRepository;
  @Autowired private TaskRepository taskRepository;

  @RequestMapping(path = "/", method = RequestMethod.POST)
  public void createGroup(@RequestBody String name) {

    if (groupRepository.existsById(name)) {
      throw new RuntimeException(String.format("Group with name '%s' already exists.", name));
    }

    final GroupEntity group = new GroupEntity();
    group.setName(name);

    groupRepository.save(group);
  }

  @RequestMapping(path = "/{name}", method = RequestMethod.DELETE)
  public void deleteGroup(@PathVariable("name") String name) {

    if (!groupRepository.existsById(name)) {
      throw new RuntimeException(String.format("Group with name '%s' doesn't exist.", name));
    }
    //unclaim all group task. 
    //1) Unclaim all task owned by this group
    taskRepository
        .findAllByGroup(name, Pageable.unpaged())
        .forEach(
            task -> {
              task.setAssignee(null);
              task.setCandidateGroup(null);
              taskRepository.save(task);
            });
    //2) delete group
    groupRepository.deleteById(name);
  }
}
