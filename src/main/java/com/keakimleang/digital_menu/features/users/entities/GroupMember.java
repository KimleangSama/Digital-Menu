package com.keakimleang.digital_menu.features.users.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Table(name = "groups_members")
public class GroupMember implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long groupId;
    private Long userId;
}