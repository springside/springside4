package org.springside.examples.bootapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springside.examples.bootapi.domain.Message;

public interface MessageDao extends CrudRepository<Message, Long> {

}
