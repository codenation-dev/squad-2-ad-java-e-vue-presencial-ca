package br.com.codenation.logstackapi.service;

import br.com.codenation.logstackapi.dto.TriggerCreateDTO;
import br.com.codenation.logstackapi.model.entity.Trigger;

import java.util.List;
import java.util.UUID;

public interface TriggerService {

    Trigger save(TriggerCreateDTO dto);

    List<Trigger> findAll();

    Trigger findById(UUID id);

}
