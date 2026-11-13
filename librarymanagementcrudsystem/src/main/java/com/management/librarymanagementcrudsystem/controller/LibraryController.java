package com.management.librarymanagementcrudsystem.controller;


import com.management.librarymanagementcrudsystem.libraryDTO.LibraryRequestDTO;
import com.management.librarymanagementcrudsystem.libraryDTO.LibraryResponseDTO;
import com.management.librarymanagementcrudsystem.service.LibraryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/library")
@Validated
public class LibraryController {

    private LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/create")
    public LibraryResponseDTO createLibrary(@Valid @RequestBody LibraryRequestDTO requestDTO){
        return libraryService.saveBooks(requestDTO);
    }

    @GetMapping("/id/{id}")
    public LibraryResponseDTO findById(@PathVariable @Positive(message = "Book ID must be Positive.") Long id){
        return libraryService.getBooksById(id);
    }
    @GetMapping("/isbn/{isbnNo}")
    public LibraryResponseDTO findByIsbnNo(@PathVariable @Positive(message = "Book ISBN No. must be Positive.") int isbnNo){
        return libraryService.getBooksByISBN(isbnNo);
    }


    @GetMapping("/Books")
    public List<LibraryResponseDTO> findAllBooksWithDeletedFalse(){
        return libraryService.getAllBooksWithDeletedIsFalse();
    }


    @PatchMapping("/update/{id}")
    public LibraryResponseDTO updateEntity(@PathVariable Long id,
                                @RequestBody LibraryRequestDTO library){
        return libraryService.updateLibrary(id, library);
    }

    @DeleteMapping("/delete/{id}")
    public LibraryResponseDTO getDeleteBooks(@PathVariable Long id){
        return libraryService.deleteBooksById(id);
    }


    @GetMapping("/deletedBooks")
    public List<LibraryResponseDTO> getDeletedBooks(){
        return libraryService.getDeletedBooks();
    }


    @PutMapping("/restore/{id}")
    public LibraryResponseDTO restoreBooks(@PathVariable Long id){
        return libraryService.restoreDeleteBooks(id);
    }
}
