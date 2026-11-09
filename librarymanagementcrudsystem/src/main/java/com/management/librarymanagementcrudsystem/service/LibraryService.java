package com.management.librarymanagementcrudsystem.service;

import com.management.librarymanagementcrudsystem.entity.Library;
import com.management.librarymanagementcrudsystem.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {
    private LibraryRepository libraryRepository;

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }
    //CREATE
    public Library saveBooks(Library library){
        library.setDeleted(false);
        return libraryRepository.save(library);
    }

    //READ
    public Library getBooksById(Long id){
       return
               libraryRepository.findByIdAndDeletedFalse(id).orElseThrow(
                       () -> new RuntimeException("Book Not found with this ID."));
    }
    public Library getBooksByISBN(int isbnNo){
        return
                libraryRepository.findByIsbnNoAndDeletedFalse(isbnNo).orElseThrow(
                        () -> new RuntimeException("No Books Found With this ISBN No."));
    }

    public List<Library> getAllBooksWithDeletedIsFalse(){
        return libraryRepository.findAllByDeletedFalse();
    }

    //UPDATE
    public Library updateLibrary(Long id, Library updateLibrary){
        Library existingLibrary = getBooksById(id);

        existingLibrary.setName(updateLibrary.getName());
        existingLibrary.setIsbnNo(updateLibrary.getIsbnNo());
        existingLibrary.setPrice(updateLibrary.getPrice());
        existingLibrary.setAvailableStock(updateLibrary.getAvailableStock());

        return libraryRepository.save(existingLibrary);
    }
    //SOFT DELETE
    public Library deleteBooksById(Long id){
        Library library = libraryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Book not found"));
        library.setDeleted(true);
        return libraryRepository.save(library);
    }
    //Get deleted Books
    public List<Library> getDeletedBooks(){
        return libraryRepository.findAllByDeletedTrue();
    }

    //Restore Deleted Books
    public Library restoreDeleteBooks(Long id){
        Library library = libraryRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found.."));
        library.setDeleted(false);
        return libraryRepository.save(library);
    }
}
