package com.keakimleang.digital_menu.commons.entities;

import java.time.*;

public interface EntityDeletable {
    Long getDeletedBy();

    LocalDateTime getDeletedAt();
}
