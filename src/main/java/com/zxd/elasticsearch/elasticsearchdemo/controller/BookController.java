package com.zxd.elasticsearch.elasticsearchdemo.controller;

import com.zxd.elasticsearch.elasticsearchdemo.domain.BookVO;
import com.zxd.elasticsearch.elasticsearchdemo.domain.BoolQueryVO;
import com.zxd.elasticsearch.elasticsearchdemo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public String add(@RequestBody BookVO vo) {
        try {
            return bookService.addBook(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/getid")
    public String get(@RequestParam(value = "id", required = true) String id) {
        System.out.println("id :" + id);
        return bookService.findBookById(id);
    }

    @PostMapping("/getidasync")
    public String getasync(@RequestParam(value = "id", required = true) String id) {
        System.out.println("id :" + id);
        return bookService.findBookByIdAsync(id);
    }
    @PostMapping("/search")
    public String search(@RequestBody BoolQueryVO vo){
        return bookService.boolQuery(vo);
    }
}
