package org.eventhub.main.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.eventhub.main.dto.*;
import org.eventhub.main.exception.AccessIsDeniedException;
import org.eventhub.main.exception.NotValidDateException;
import org.eventhub.main.exception.NullDtoReferenceException;
import org.eventhub.main.mapper.EventMapper;
import org.eventhub.main.model.Embedding;
import org.eventhub.main.model.Event;
import org.eventhub.main.model.Photo;
import org.eventhub.main.repository.EventRepository;
import org.eventhub.main.service.EventService;

import org.eventhub.main.service.PhotoService;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final EmbeddingClient embeddingClient;

    private void checkDate( LocalDateTime currentTime, LocalDateTime startAt, LocalDateTime expireAt) {


        //Exceptions
        if (!startAt.isBefore(expireAt) || !startAt.isAfter(currentTime)) {
            throw new NotValidDateException("Choose correct date");
        }
    }

    private Embedding setVector(EventRequest eventRequest) {
        Embedding embedding = new Embedding();
        embedding.setEmbedding(embeddingClient.embed(String.format("%s. %s. %s. %s", eventRequest.getTitle(), eventRequest.getDescription(), eventRequest.getLocation(), getCategoriesFromList(eventRequest.getCategoryRequests()))));
        return embedding;
    }

    private String getCategoriesFromList(List<CategoryRequest> requests) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < requests.size() - 1; i++) {
            stringBuilder.append(requests.get(i).getName()).append(", ");
        }
        stringBuilder.append(requests.get(requests.size() - 1).getName());
        return stringBuilder.toString();
    }


    @Autowired
    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, EmbeddingClient embeddingClient) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.embeddingClient = embeddingClient;
    }
    @Override
    public EventResponse create(EventRequest eventRequest) {
        LocalDateTime currentTime = LocalDateTime.now();
        Event event = new Event();

        event.setCreatedAt(currentTime);

        checkDate(currentTime, eventRequest.getStartAt(), eventRequest.getExpireAt());

        event.setParticipants(new ArrayList<>());

        Embedding embedding = setVector(eventRequest);

        event.setEmbedding(embedding);


        Event eventToSave = eventMapper.requestToEntity(eventRequest, event);

        if (event.getParticipantCount() >= eventToSave.getMaxParticipants()) {
            throw new AccessIsDeniedException("Event " + eventToSave.getTitle() + " is full.");
        }

        return eventMapper.entityToResponse(eventRepository.save(eventToSave));
    }
    @Override
    public EventResponse readById(UUID id) {
        Event event = eventRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("Non existing id: " + id));
        return eventMapper.entityToResponse(event);
    }

    @Override
    public Event readByIdEntity(UUID id) {
        return eventRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("Non existing id: " + id));
    }

    @Override
    public Event readByTitle(String title) {
        Event event = eventRepository.findByTitle(title);
        if (event != null) {
            return event;
        }
        throw new EntityNotFoundException("Event with title " + title + " was not found");
    }

    @Override
    @Transactional
    public EventResponse update(EventRequest eventRequest) {
        if(eventRequest != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            Event event = readByTitle(eventRequest.getTitle());

            checkDate(currentTime, eventRequest.getStartAt(), eventRequest.getExpireAt());

            Embedding embedding = setVector(eventRequest);

            event.setEmbedding(embedding);

            Event eventToUpdate = eventMapper.requestToEntity(eventRequest, event);

            return eventMapper.entityToResponse(eventRepository.save(eventToUpdate));
        }
        throw new NullDtoReferenceException("Cannot update null event");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        eventRepository.delete(readByIdEntity(id));
    }

    @Override
    public List<EventResponse> getAll() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::entityToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public void addImage(UUID eventId, Photo image) {
        Event event = this.readByIdEntity(eventId);
        event.getPhotos().add(image);
    }

    @Override
    public void deleteImage(UUID eventId, Photo image){
        Event event = this.readByIdEntity(eventId);
        event.getPhotos().remove(image);
    }
}