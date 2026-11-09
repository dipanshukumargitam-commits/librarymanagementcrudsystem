package com.management.librarymanagementcrudsystem.repository;

import com.management.librarymanagementcrudsystem.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library, Long> {

    Optional<Library> findByIdAndDeletedFalse(Long id);
    Optional<Library> findByIsbnNoAndDeletedFalse(int isbnNo);
    List<Library> findAllByDeletedTrue();
    List<Library> findAllByDeletedFalse();
}
