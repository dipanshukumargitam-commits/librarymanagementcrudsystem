package com.management.librarymanagementcrudsystem.controller;


import com.management.librarymanagementcrudsystem.entity.Library;
import com.management.librarymanagementcrudsystem.service.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/library")
public class LibraryController {
    private LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/create")
    public Library createLibrary(@RequestBody Library library){
        return libraryService.saveBooks(library);
    }

    @GetMapping("/id/{id}")
    public Library findById(@PathVariable Long id){
        return libraryService.getBooksById(id);
    }
    @GetMapping("/isbn/{isbnNo}")
    public Library findByIsbnNo(@PathVariable int isbnNo){
        return libraryService.getBooksByISBN(isbnNo);
    }
    @GetMapping("/Books")
    public List<Library> findAllBooksWithDeletedFalse(Library library){
        return libraryService.getAllBooksWithDeletedIsFalse();
    }


    @PatchMapping("/update/{id}")
    public Library updateEntity(@PathVariable Long id,
                                @RequestBody Library library){
        return libraryService.updateLibrary(id, library);
    }

    @DeleteMapping("/delete/{id}")
    public Library getDeleteBooks(@PathVariable Long id){
        return libraryService.deleteBooksById(id);
    }


    @GetMapping("/deletedBooks")
    public List<Library> getDeletedBooks(){
        return libraryService.getDeletedBooks();
    }


    @PutMapping("/restore/{id}")
    public Library restoreBooks(@PathVariable Long id){
        return libraryService.restoreDeleteBooks(id);
    }
}
