package com.keakimleang.digital_menu.features.users.repos;

import com.keakimleang.digital_menu.features.users.entities.*;
import java.util.*;
import org.springframework.data.r2dbc.repository.*;
import org.springframework.data.repository.reactive.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Repository
public interface GroupRepository extends ReactiveCrudRepository<Group, Long> {
}
