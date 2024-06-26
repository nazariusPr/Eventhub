package org.eventhub.main.repository;

import org.eventhub.main.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FilterRepository extends JpaRepository<Event, UUID> {
    @Query("SELECT e FROM Event e WHERE e.owner.id = :userId")
    List<Event> findMyEvents(@Param("userId") UUID userId);

    @Query("SELECT p.event FROM Participant p WHERE p.user.id = :userId AND p.isApproved = true")
    List<Event> findJoinedEvents(@Param("userId") UUID userId);

    @Query("SELECT p.event FROM Participant p WHERE p.user.id = :userId AND p.isApproved = false")
    List<Event> findPendingEvents(@Param("userId") UUID userId);

    @Query("SELECT e FROM Event e WHERE e.owner.id = :userId AND e.expireAt <= :currentDateTime")
    List<Event> findArchiveEvents(@Param("userId") UUID userId, @Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT e FROM Event e WHERE e.startAt <= :currentDateTime AND e.expireAt >= :currentDateTime")
    List<Event> findAllLiveEvents(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT e FROM Event e WHERE e.startAt >= :currentDateTime")
    List<Event> findAllUpcomingEvents(@Param("currentDateTime") LocalDateTime currentDateTime);
}
