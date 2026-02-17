package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.dto.BookQueryRequest;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书控制器
 * 提供图书的增删改查接口
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 根据ID获取图书详情
     * @param id 图书ID
     * @return 图书信息
     */
    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Long id) {
        Book book = bookService.getById(id);
        if (book == null) {
            return Result.error("图书不存在");
        }
        return Result.success(book);
    }

    /**
     * 添加新图书
     * @param book 图书信息
     * @return 保存后的图书信息
     */
    @PostMapping
    public Result<Book> addBook(@RequestBody Book book) {
        try {
            Book saved = bookService.addBook(book);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新图书信息
     * @param id 图书ID
     * @param book 图书信息
     * @return 更新后的图书信息
     */
    @PutMapping("/{id}")
    public Result<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        try {
            book.setId(id);
            Book updated = bookService.updateBook(book);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除图书
     * @param id 图书ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询图书列表
     * @param request 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<Book>> queryBooks(BookQueryRequest request) {
        PageResult<Book> result = bookService.queryBooks(request);
        return Result.success(result);
    }

    /**
     * 获取所有图书分类
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        return Result.success(bookService.getAllCategories());
    }

    /**
     * 获取最新上架图书
     * @return 最新图书列表
     */
    @GetMapping("/latest")
    public Result<List<Book>> getLatestBooks() {
        return Result.success(bookService.getLatestBooks());
    }
}
