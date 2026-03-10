package com.library.service;

import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.dto.BookQueryRequest;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图书服务
 * 处理图书的增删改查业务逻辑
 */
@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    public Book getById(Long id) {
        return bookMapper.findById(id);
    }

    public Book addBook(Book book) {
        book.setStatus(1);
        bookMapper.insert(book);
        return book;
    }

    public Book updateBook(Book book) {
        if (book.getStatus() == null) {
            Book existing = bookMapper.findById(book.getId());
            if (existing != null) {
                book.setStatus(existing.getStatus());
            } else {
                book.setStatus(1);
            }
        }
        bookMapper.update(book);
        return bookMapper.findById(book.getId());
    }

    public void deleteBook(Long id) {
        int activeBorrows = borrowRecordMapper.countActiveBorrowByBookId(id);
        if (activeBorrows > 0) {
            throw new BusinessException("该图书有未归还的借阅记录，无法删除");
        }
        bookMapper.deleteById(id);
    }

    public PageResult<Book> queryBooks(BookQueryRequest request) {
        int pageNum = (request.getPageNum() != null && request.getPageNum() >= 1) ? request.getPageNum() : 1;
        int pageSize = (request.getPageSize() != null && request.getPageSize() >= 1) ? request.getPageSize() : 10;
        if (pageSize > 100) pageSize = 100;

        int offset = (pageNum - 1) * pageSize;

        List<Book> books = bookMapper.findByCondition(
                request.getTitle(), request.getAuthor(), request.getCategory(),
                request.getIsbn(), request.getKeyword(), offset, pageSize);

        Long total = bookMapper.countByCondition(
                request.getTitle(), request.getAuthor(), request.getCategory(),
                request.getIsbn(), request.getKeyword());

        return PageResult.of(books, total, pageNum, pageSize);
    }

    public List<String> getAllCategories() {
        return bookMapper.findAllCategories();
    }

    public List<Book> getLatestBooks() {
        return bookMapper.findLatest();
    }

    public Long getTotalCount() {
        return bookMapper.count();
    }
}
