package org.eventhub.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.eventhub.main.dto.*;
import org.eventhub.main.event.EmailEventPublisher;
import org.eventhub.main.exception.ResponseStatusException;
import org.eventhub.main.model.Event;
import org.eventhub.main.model.User;
import org.eventhub.main.service.EventService;
import org.eventhub.main.service.ParticipantService;
import org.eventhub.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/users")
public class EventController {
    private final UserService userService;
    private final EventService eventService;
    private final EmailEventPublisher emailEventPublisher;
    private final ParticipantService participantService;

    @Autowired
    public EventController(UserService userService,EventService eventService, ParticipantService participantService, EmailEventPublisher emailEventPublisher){
        this.userService = userService;
        this.eventService = eventService;
        this.participantService = participantService;
        this.emailEventPublisher = emailEventPublisher;
    }

    @PostMapping("/{user_id}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventFullInfoResponse> create(@RequestHeader(name="Authorization") String token, @PathVariable("user_id") UUID userId, @Validated @RequestBody EventRequest request,
                                                        BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ResponseStatusException("Invalid Input");
        }
        EventFullInfoResponse response = eventService.create(request);

        if(request.isWithOwner()) {
            ParticipantResponse participantResponse = participantService.create(new ParticipantRequest(response.getId(), userId));
            participantService.addParticipant(participantResponse.getId(), response.getId(), token);
            response.setParticipantCount(response.getParticipantCount() + 1);
        }

        log.info("**/created event(id) = " + response.getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/events/info")
    public ResponseEntity<List<EventFullInfoResponse>> getAllFullInfo(){
        log.info("**/get all events");
        return new ResponseEntity<>(eventService.getAllFullInfo(), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponseXY>> getAll(){
        log.info("**/get all events");
        return new ResponseEntity<>(eventService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/events/{event_id}")
    public ResponseEntity<EventFullInfoResponse> getById(@PathVariable("event_id") UUID eventId){
        EventFullInfoResponse response = eventService.readById(eventId);
        log.info("**/get by id event(id) = " + response.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/events/{event_id}")
    public ResponseEntity<EventFullInfoResponse> update(@RequestHeader (name="Authorization") String token, @PathVariable("event_id") UUID eventId,
                                                        @Validated @RequestBody EventRequest request, BindingResult result) {


        if(result.hasErrors()){
            throw new ResponseStatusException(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        EventFullInfoResponse response = eventService.update(eventId, request, token);
        log.info("**/updated event(id) = " + response.getId());

        this.emailEventPublisher.publishEventUpdate(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/events/{event_id}")
    public ResponseEntity<OperationResponse> delete(@RequestHeader (name="Authorization") String token, @PathVariable("event_id") UUID eventId) {
        String title = eventService.readById(eventId).getTitle();
        List<User> users =  userService.findApprovedUsersByEventId(eventId);

        eventService.delete(eventId, token);
        log.info("**/deleted event(id) = " + eventId);

        this.emailEventPublisher.publishEventDelete(title,users);
        return new ResponseEntity<>(new OperationResponse("Event with title '"+title+"' deleted successfully"), HttpStatus.OK);
    }

    @GetMapping("/{user_id}/events")
    public ResponseEntity<List<EventSearchResponse>> getUserEvents(@PathVariable("user_id") UUID userId) {
        List<EventSearchResponse> userEvents = eventService.getUserEvents(userId);
        return new ResponseEntity<>(userEvents, HttpStatus.OK);
    }
}

