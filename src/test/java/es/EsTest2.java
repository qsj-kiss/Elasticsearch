package es;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Create by qsj computer
 *  Elasticsearch使用查操作
 * @author qsj
 * @date 2021/3/9 9:50
 */

public class EsTest2 {
    public static TransportClient client = null;
    @Before
    public void create() throws UnknownHostException {
        InputStream resource = EsTest2.class.getClassLoader().getResourceAsStream("elasticSearch.properties");
        Properties properties = new Properties();
        try {
            properties.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //创建一个settings对象
        Settings settings = Settings.builder()
                .put("cluster.name",properties.getProperty("cluster.name")).build();
        //创建一个客户端client对象
        client = new PreBuiltTransportClient(settings);
        client.addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(properties.getProperty("ip.node1")),Integer.valueOf(properties.getProperty("port.node1"))));
        client.addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(properties.getProperty("ip.node2")),Integer.valueOf(properties.getProperty("port.node2"))));
        client.addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(properties.getProperty("ip.node3")),Integer.valueOf(properties.getProperty("port.node3"))));
    }

    /** 一个公用的输出方法
     * @param queryBuilder
     */
    public void show(QueryBuilder queryBuilder,int number,int count){
        //创建一个查询对象
        SearchResponse response = client.prepareSearch("blog").setTypes("Article")
                .setQuery(queryBuilder)
                //从哪个开始
                .setFrom(number)
                //每页显示的数量
                .setSize(count)
                .get();
        //获取查询结果
        SearchHits hits = response.getHits();
        //打印数据
        System.out.println("总条数："+hits.getTotalHits()+"条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit hit = iterator.next();
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }

    /** 一个公用的输出方法
     * @param queryBuilder
     */
    public void show(QueryBuilder queryBuilder,int number,int count,String highLight){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(highLight);
        highlightBuilder.preTags("<red>");
        highlightBuilder.preTags("</red>");
        //创建一个查询对象
        SearchResponse response = client.prepareSearch("blog").setTypes("Article")
                .setQuery(queryBuilder)
                //从哪个开始
                .setFrom(number)
                //每页显示的数量
                .setSize(count)
                //需要高亮处理
                .highlighter(highlightBuilder)
                .get();
        //获取查询结果
        SearchHits hits = response.getHits();
        //打印数据
        System.out.println("总条数："+hits.getTotalHits()+"条");
        Iterator<SearchHit> iterator = hits.iterator();

        while (iterator.hasNext()){
            SearchHit hit = iterator.next();
            System.out.println("----未高亮显示的数据----");
            System.out.println(hit.getSource().get(highLight));
            System.out.println("----高亮显示的数据----");
            Map<String, HighlightField> fields = hit.getHighlightFields();
            HighlightField field = fields.get(highLight);
            String string = field.getFragments()[0].toString();
            System.out.println(string);
        }
        client.close();
    }

    @Test
    //根据Id查询数据
    public void findById(){
    //创建一个查询对象
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("AXggNCGuU2xq7QNvcW9m", "AXggP_KVpulrt-5k9AsT");
        show(queryBuilder,0,5);
    }

    @Test
    //根据term查询数据
    public void findByTerm(){
        //创建一个查询对象
        //参数一要搜索的字段
        //参数二要搜索的关键词
        QueryBuilder queryBuilder = QueryBuilders.termQuery("title","好");
        show(queryBuilder,0,5);
    }

    @Test
    //根据StringQuery查询数据
    public void findQueryStringQuery(){
        //创建一个查询对象
        //参数一要搜索的字段
        //参数二要搜索的关键词
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("安静的晚上").defaultField("title");
        show(queryBuilder,0,20);
    }

    @Test
    //高亮显示查询
    public void findHighLight(){
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("安静").defaultField("title");
        show(queryBuilder,0,5,"title");
    }


}
