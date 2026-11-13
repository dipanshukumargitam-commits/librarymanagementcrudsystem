package com.management.librarymanagementcrudsystem.mapper;

import com.management.librarymanagementcrudsystem.entity.Library;
import com.management.librarymanagementcrudsystem.libraryDTO.LibraryRequestDTO;
import com.management.librarymanagementcrudsystem.libraryDTO.LibraryResponseDTO;

public class LibraryMapper {
    public static Library toEntity(LibraryRequestDTO requestDTO){
        Library library = new Library();
        library.setName(requestDTO.getName());
        library.setIsbnNo(requestDTO.getIsbnNo());
        library.setPrice(requestDTO.getPrice());
        library.setAvailableStock(requestDTO.getAvailableStock());
        library.setDeleted(false);
        return library;

    }
    public static LibraryResponseDTO toResponseDTO(Library library){
    LibraryResponseDTO libraryResponseDTO = new LibraryResponseDTO();
    libraryResponseDTO.setId(library.getId());
    libraryResponseDTO.setName(library.getName());
    libraryResponseDTO.setIsbnNo(library.getIsbnNo());
    libraryResponseDTO.setPrice(library.getPrice());
    libraryResponseDTO.setAvailableStock(library.getAvailableStock());
    libraryResponseDTO.setDeleted(library.IsDeleted());
    return libraryResponseDTO;
    }
}
