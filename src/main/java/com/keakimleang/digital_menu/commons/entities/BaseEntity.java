package com.keakimleang.digital_menu.commons.entities;

import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.annotation.*;

@Getter
@Setter
public abstract class BaseEntity implements Serializable {
    @Id
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BaseEntity {" +
                "id = " + id +
                "}";
    }
}