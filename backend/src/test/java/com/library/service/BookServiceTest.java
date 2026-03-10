package com.library.service;

import com.library.dto.BookQueryRequest;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setIsbn("978-7-111-42036-8");
        testBook.setTitle("Java编程入门");
        testBook.setAuthor("詹姆斯·高斯林");
        testBook.setCategory("编程");
        testBook.setPrice(new BigDecimal("89.00"));
        testBook.setStock(10);
        testBook.setStatus(1);
    }

    @Test
    void getById_Success() {
        when(bookMapper.findById(1L)).thenReturn(testBook);
        Book result = bookService.getById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java编程入门", result.getTitle());
    }

    @Test
    void getById_NotFound() {
        when(bookMapper.findById(999L)).thenReturn(null);
        Book result = bookService.getById(999L);
        assertNull(result);
    }

    @Test
    void addBook_Success() {
        when(bookMapper.insert(any(Book.class))).thenReturn(1);
        Book result = bookService.addBook(testBook);
        assertNotNull(result);
        verify(bookMapper).insert(testBook);
    }

    @Test
    void updateBook_Success() {
        when(bookMapper.update(any(Book.class))).thenReturn(1);
        when(bookMapper.findById(1L)).thenReturn(testBook);
        Book result = bookService.updateBook(testBook);
        assertNotNull(result);
        verify(bookMapper).update(testBook);
    }

    @Test
    void deleteBook_Success() {
        when(borrowRecordMapper.countActiveBorrowByBookId(1L)).thenReturn(0);
        when(bookMapper.deleteById(1L)).thenReturn(1);
        bookService.deleteBook(1L);
        verify(bookMapper).deleteById(1L);
    }

    @Test
    void deleteBook_HasActiveBorrows() {
        when(borrowRecordMapper.countActiveBorrowByBookId(1L)).thenReturn(2);
        assertThrows(RuntimeException.class, () -> bookService.deleteBook(1L));
        verify(bookMapper, never()).deleteById(anyLong());
    }

    @Test
    void getAllCategories_Success() {
        List<String> categories = Arrays.asList("编程", "数据库", "前端", "算法");
        when(bookMapper.findAllCategories()).thenReturn(categories);
        List<String> result = bookService.getAllCategories();
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains("编程"));
    }
}
