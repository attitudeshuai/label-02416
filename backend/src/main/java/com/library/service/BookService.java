package com.library.service;

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

    /**
     * 根据ID获取图书
     * @param id 图书ID
     * @return 图书信息
     */
    public Book getById(Long id) {
        return bookMapper.findById(id);
    }

    /**
     * 添加新图书
     * @param book 图书信息
     * @return 保存后的图书
     */
    public Book addBook(Book book) {
        book.setStatus(1); // 1-上架状态
        bookMapper.insert(book);
        return book;
    }

    /**
     * 更新图书信息
     * @param book 图书信息
     * @return 更新后的图书
     */
    public Book updateBook(Book book) {
        // 如果 status 为空，保留原有状态
        if (book.getStatus() == null) {
            Book existing = bookMapper.findById(book.getId());
            if (existing != null) {
                book.setStatus(existing.getStatus());
            } else {
                book.setStatus(1); // 默认上架
            }
        }
        bookMapper.update(book);
        return bookMapper.findById(book.getId());
    }

    /**
     * 删除图书
     * @param id 图书ID
     */
    public void deleteBook(Long id) {
        // 检查是否有未归还的借阅记录
        int activeBorrows = borrowRecordMapper.countActiveBorrowByBookId(id);
        if (activeBorrows > 0) {
            throw new RuntimeException("该图书有未归还的借阅记录，无法删除");
        }
        bookMapper.deleteById(id);
    }

    /**
     * 分页查询图书
     * @param request 查询条件
     * @return 分页结果
     */
    public PageResult<Book> queryBooks(BookQueryRequest request) {
        // 计算分页偏移量
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        
        // 查询图书列表
        List<Book> books = bookMapper.findByCondition(
                request.getTitle(),
                request.getAuthor(),
                request.getCategory(),
                request.getIsbn(),
                request.getKeyword(),
                offset,
                request.getPageSize()
        );
        
        // 查询总数
        Long total = bookMapper.countByCondition(
                request.getTitle(),
                request.getAuthor(),
                request.getCategory(),
                request.getIsbn(),
                request.getKeyword()
        );
        
        return PageResult.of(books, total, request.getPageNum(), request.getPageSize());
    }

    /**
     * 获取所有图书分类
     * @return 分类列表
     */
    public List<String> getAllCategories() {
        return bookMapper.findAllCategories();
    }

    /**
     * 获取最新上架图书
     * @return 最新图书列表
     */
    public List<Book> getLatestBooks() {
        return bookMapper.findLatest();
    }

    /**
     * 获取图书总数
     * @return 图书总数
     */
    public Long getTotalCount() {
        return bookMapper.count();
    }
}
