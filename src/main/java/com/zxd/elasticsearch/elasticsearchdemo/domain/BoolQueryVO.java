package com.zxd.elasticsearch.elasticsearchdemo.domain;

import lombok.Data;

@Data
public class BoolQueryVO {
    private String author;
    private String title;
    private Integer gtWordCount;
    private Integer ltWordCount;
}
