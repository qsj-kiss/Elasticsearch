package es;

import com.qs.repositories.ArticleRepositories;
import com.qs.domain.Article;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

/**
 * Create by qsj computer
 *
 * @author qsj
 * @date 2021/3/30 1:09
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:ApplicationContext.xml")
public class SpringDataEsTest {

    @Autowired
    private ArticleRepositories articleRepositories;
    @Autowired
    private ElasticsearchTemplate template;

    @Test
    public void createIndex() {
        //创建索引并配置映射关系
        boolean index = template.createIndex(Article.class);
        System.out.println(index);
    }

    @Test
    public void addDocument() {
        //添加文档
        Article article = new Article();
        article.setId(4);
        article.setTitle("这个是标题4");
        article.setContent("这个是正文4");
        Article save = articleRepositories.save(article);
        System.out.println(save);
    }

    @Test
    public void addDocuments() {
        //添加多条文档
        Article article = new Article();
        for (int i = 21; i <= 40; i++) {
            article.setId(i);
            article.setTitle("我是标题" + i);
            article.setContent("我不是标题" + i);
            articleRepositories.save(article);
        }
    }

    @Test
    public void delDocument() {
        //删除文档
//        articleRepositories.deleteById(1L);
        //删除所有文档
        articleRepositories.deleteAll();
    }

    @Test
    public void updateDocument() {
        //更新文档/和保存同一个方法
        Article article = new Article();
        article.setId(4);
        article.setTitle("这个是标题40");
        article.setContent("这个是正文40");
        Article save = articleRepositories.save(article);
        System.out.println(save);
    }

    @Test
    public void findDocument() {
        //查询所有文档
        Iterable<Article> all = articleRepositories.findAll();
        all.forEach(a -> System.out.println(a));
    }

    @Test
    public void findDocumentById() {
        //根据Id查询
        Optional<Article> article = articleRepositories.findById(1L);
        String string = article.get().toString();
        System.out.println(string);
    }

    @Test
    public void testFindByTitle() {
        //根据title查询文档
        List<Article> title = articleRepositories.findArticleByTitle("标题");
        title.forEach(article -> System.out.println(article));
    }

    @Test
    public void testFindArticleByTitleOrContent() {
        //根据title或者Content查询文档
        List<Article> title = articleRepositories.findArticleByTitleOrContent("标题", "21");
        title.forEach(article -> System.out.println(article));
    }

    @Test
    public void testFindArticleByTitleOrContent2() {
        //根据title或者Content分页查询文档
        Pageable request = PageRequest.of(0,15);
        List<Article> title = articleRepositories.findArticleByTitleOrContent("标题", "21", request);
        title.forEach(article -> System.out.println(article));
    }

    @Test
    public void testFindArticleByTitleAndContent() {
        //根据title和Content查询文档
        List<Article> title = articleRepositories.findArticleByTitleAndContent("标题", "21");
        title.forEach(article -> System.out.println(article));
    }

    @Test
    public void testNativeSearchQuery() {
        //原生的方法实现queryString方法
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery("标题是注释").defaultField("title");
        Pageable request = PageRequest.of(0,15);
        NativeSearchQuery query =  new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(request)
                .build();
        List<Article> list = template.queryForList(query, Article.class);
        list.forEach(article -> System.out.println(article));
    }

}