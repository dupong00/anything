package com.example.anything.group.internal.repository;

import com.example.anything.group.internal.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group,Long> {
}
