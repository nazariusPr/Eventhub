package org.eventhub.main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.eventhub.main.dto.ParticipantRequest;
import org.eventhub.main.dto.ParticipantResponse;
import org.eventhub.main.exception.NoSuchParticipantException;
import org.eventhub.main.mapper.ParticipantMapper;
import org.eventhub.main.model.Participant;
import org.eventhub.main.repository.ParticipantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    public ParticipantResponse createParticipant(ParticipantRequest participantRequest) {
        Participant participant = participantMapper.mapToParticipant(participantRequest);
        return participantMapper.mapToParticipantResponse(participantRepository.save(participant));
    }

    public ParticipantResponse getParticipantById(Long id) {
        Participant participant = participantRepository.findById(id).orElseThrow();
        return participantMapper.mapToParticipantResponse(participant);
    }

    @Transactional
    public Page<ParticipantResponse> getAllParticipants(Predicate filters, Pageable pageable) {
        return participantRepository.findAll((com.querydsl.core.types.Predicate) filters, pageable)
                .map(participantMapper::mapToParticipantResponse);
    }

    public void deleteParticipant(Long id) {
        participantRepository.deleteById(id);
    }
}
