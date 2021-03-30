package com.qs.repositories;

import com.qs.domain.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
    * @return
    * @作者 秦世交
    * @创建时间 2021/3/30 23:33
*/
public interface ArticleRepositories extends ElasticsearchRepository<Article,Long> {

    /** 根据title查询
     * @param title
     * @return
     */
    List<Article> findArticleByTitle(String title);

    /** 根据title或content查询
     * @param title
     * @param content
     * @return
     */
    List<Article> findArticleByTitleOrContent(String title,String content);

    /** 根据title或content进行分页查询
     * @param title
     * @param content
     * @param pageable
     * @return
     */
    List<Article> findArticleByTitleOrContent(String title, String content, Pageable pageable);

    /** 根据title和content查询
     * @param title
     * @param content
     * @return
     */
    List<Article> findArticleByTitleAndContent(String title,String content);
}
