package br.com.codenation.centralerrosapi.service;

import br.com.codenation.centralerrosapi.model.Log;
import br.com.codenation.centralerrosapi.repository.LogRepository;
import br.com.codenation.centralerrosapi.service.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class LogService {

    private LogRepository repository;

    public List<Log> findAll() {
        return repository.findAll();
    }

    public Log findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log not found with the specified id"));
    }

    public List<Log> findByTitleIgnoreCaseContaining(String title) {
        return repository.findByTitleIgnoreCaseContaining(title);
    }

    public List<Log> findByIpContaining(String ip) {
        return repository.findByServerIpContaining(ip);
    }

    public List<Log> findByApplicationIgnoreCaseContaining(String application) {
        return repository.findByServerApplicationIgnoreCaseContaining(application);
    }

    public List<Log> findByEnvironmentIgnoreCaseContaining(String environment) {
        return repository.findByEnvironmentIgnoreCaseContaining(environment);
    }

    public Log unarchive(UUID id) {
        Log error = findById(id);
        error.setArchived(false);
        return repository.save(error);
    }

    public Log archive(UUID id) {
        Log error = findById(id);
        error.setArchived(true);
        return repository.save(error);
    }
}
