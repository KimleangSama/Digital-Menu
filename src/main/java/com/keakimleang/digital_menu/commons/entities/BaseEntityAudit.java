package com.keakimleang.digital_menu.commons.entities;

import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
public abstract class BaseEntityAudit extends BaseEntity implements Serializable {
    private Long createdBy;
    private LocalDateTime createdAt;

    private Long updatedBy;
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntityAudit that)) return false;
        if (!super.equals(o)) return false;
        return createdBy.equals(that.createdBy) &&
                updatedBy.equals(that.updatedBy) &&
                createdAt.equals(that.createdAt) &&
                updatedAt.equals(that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                createdBy, updatedBy, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "BaseEntityAudit{" + "createdBy='" + createdBy + ", updatedBy='" +
                updatedBy + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                "}" + super.toString();
    }
}
