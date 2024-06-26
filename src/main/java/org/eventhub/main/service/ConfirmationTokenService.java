package org.eventhub.main.service;

import org.eventhub.main.model.ConfirmationToken;
import org.eventhub.main.model.PasswordResetToken;
import org.eventhub.main.model.User;

import java.util.UUID;

public interface ConfirmationTokenService {
    ConfirmationToken create(User user);
    ConfirmationToken findByToken(String token);
    ConfirmationToken read(UUID id);
    void delete(UUID id);
}
