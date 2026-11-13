package com.management.librarymanagementcrudsystem.service;

import com.management.librarymanagementcrudsystem.entity.Library;
import com.management.librarymanagementcrudsystem.exception.BookNotFoundException;
import com.management.librarymanagementcrudsystem.exception.DuplicateIsbnException;
import com.management.librarymanagementcrudsystem.libraryDTO.LibraryRequestDTO;
import com.management.librarymanagementcrudsystem.libraryDTO.LibraryResponseDTO;
import com.management.librarymanagementcrudsystem.mapper.LibraryMapper;
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
    public LibraryResponseDTO saveBooks(LibraryRequestDTO requestDTO){
        boolean isbnExist = libraryRepository.findByIsbnNoAndDeletedFalse(requestDTO.getIsbnNo()).isPresent();
        if(isbnExist){
            throw  new DuplicateIsbnException("A Book with ISBN "+requestDTO.getIsbnNo() +" Already Exist.");
        }
        Library library = LibraryMapper.toEntity(requestDTO);
        Library savedLibrary = libraryRepository.save(library);
        return LibraryMapper.toResponseDTO(savedLibrary);
    }



    //READ
    public LibraryResponseDTO getBooksById(Long id){
               Library library = libraryRepository.findByIdAndDeletedFalse(id).orElseThrow(
                       () -> new BookNotFoundException("Book Not found with ID:  "+id));
               return LibraryMapper.toResponseDTO(library);
    }
    public LibraryResponseDTO getBooksByISBN(int isbnNo){
        Library library =libraryRepository.findByIsbnNoAndDeletedFalse(isbnNo).orElseThrow(
                        () -> new RuntimeException("No Books Found With this ISBN No."));
        return LibraryMapper.toResponseDTO(library);
    }

    public List<LibraryResponseDTO> getAllBooksWithDeletedIsFalse(){
         List<Library> libraries = libraryRepository.findAllByDeletedFalse();

         return libraries.stream().map(LibraryMapper::toResponseDTO).toList();
    }



    //UPDATE
    public LibraryResponseDTO updateLibrary(Long id, LibraryRequestDTO requestDTO){

        Library existingLibrary = libraryRepository.findByIdAndDeletedFalse(id).orElseThrow(
                () -> new BookNotFoundException("Book Not found with this ID."));

        libraryRepository.findByIsbnNoAndDeletedFalse(requestDTO.getIsbnNo()).ifPresent(book -> {
            if(!book.getId().equals(id)) {
                throw new DuplicateIsbnException(
                        "A Book With ISBN "+requestDTO.getIsbnNo()+ " Already Exists."
                );
            }
        });

        existingLibrary.setName(requestDTO.getName());
        existingLibrary.setIsbnNo(requestDTO.getIsbnNo());
        existingLibrary.setPrice(requestDTO.getPrice());
        existingLibrary.setAvailableStock(requestDTO.getAvailableStock());
        Library updateLibrary = libraryRepository.save(existingLibrary);
        return LibraryMapper.toResponseDTO(updateLibrary);
    }



    //SOFT DELETE
    public LibraryResponseDTO deleteBooksById(Long id){
        Library library = libraryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Book not found"));
        library.setDeleted(true);
        Library savedLibrary = libraryRepository.save(library);
        return LibraryMapper.toResponseDTO(savedLibrary);
    }



    //Get deleted Books
    public List<LibraryResponseDTO> getDeletedBooks(){

        List<Library> deletedBoooks = libraryRepository.findAllByDeletedTrue();
        return deletedBoooks.stream().map(LibraryMapper::toResponseDTO).toList();

    }



    //Restore Deleted Books
    public LibraryResponseDTO restoreDeleteBooks(Long id){
        Library library = libraryRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found.."));
        library.setDeleted(false);
        Library savedRestoredBooks = libraryRepository.save(library);
        return LibraryMapper.toResponseDTO(savedRestoredBooks);
    }

}
