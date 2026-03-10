package com.library.controller;

import com.library.common.ForbiddenException;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.dto.BookQueryRequest;
import com.library.dto.BookRequest;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 图书控制器
 * 提供图书的增删改查接口
 */
@Validated
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Long id) {
        Book book = bookService.getById(id);
        if (book == null) {
            return Result.error("图书不存在");
        }
        return Result.success(book);
    }

    /**
     * 添加新图书（仅管理员）
     */
    @PostMapping
    public Result<Book> addBook(@Valid @RequestBody BookRequest bookReq, HttpServletRequest httpReq) {
        checkAdmin(httpReq);
        Book book = new Book();
        BeanUtils.copyProperties(bookReq, book);
        Book saved = bookService.addBook(book);
        return Result.success(saved);
    }

    /**
     * 更新图书信息（仅管理员）
     */
    @PutMapping("/{id}")
    public Result<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest bookReq, HttpServletRequest httpReq) {
        checkAdmin(httpReq);
        Book book = new Book();
        BeanUtils.copyProperties(bookReq, book);
        book.setId(id);
        Book updated = bookService.updateBook(book);
        return Result.success(updated);
    }

    /**
     * 删除图书（仅管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id, HttpServletRequest httpReq) {
        checkAdmin(httpReq);
        bookService.deleteBook(id);
        return Result.success();
    }

    /**
     * 分页查询图书
     */
    @GetMapping
    public Result<PageResult<Book>> queryBooks(@Valid BookQueryRequest queryReq) {
        PageResult<Book> result = bookService.queryBooks(queryReq);
        return Result.success(result);
    }

    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        return Result.success(bookService.getAllCategories());
    }

    @GetMapping("/latest")
    public Result<List<Book>> getLatestBooks() {
        return Result.success(bookService.getLatestBooks());
    }

    private void checkAdmin(HttpServletRequest httpReq) {
        Integer role = (Integer) httpReq.getAttribute("role");
        if (role == null || role != 1) {
            throw new ForbiddenException("无权限操作");
        }
    }
}
