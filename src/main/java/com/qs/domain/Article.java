package com.qs.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Create by qsj computer
 *
 * @author qsj
 * @date 2021/3/11 15:40
 */
@Document(indexName = "index_blog",type = "article")
public class Article {
    @Id
    @Field(type = FieldType.Long ,index = false ,store = true )
    private long id;
    @Field(type = FieldType.text ,index = true ,store = true ,analyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.text ,index = true ,store = true ,analyzer = "ik_smart")
    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

}
