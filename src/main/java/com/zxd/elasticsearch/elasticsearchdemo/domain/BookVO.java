package com.zxd.elasticsearch.elasticsearchdemo.domain;

import lombok.Data;

@Data
public class BookVO {
    private String id;
    private String type;
    private Integer wordCount;
    private String author;
    private String title;
    private String publishDate;
}
