package es;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qs.domain.Article;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Create by qsj computer
 *  Elasticsearch使用增删改操作
 * @author qsj
 * @date 2021/3/9 9:21
 */

public class EsTest1  {
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

    @Test
    //新增索引库的方法
    public void createIndex(){
        //使用client对象创建一个索引库
        client.admin().indices().prepareCreate("blog")
                //执行方法
                .get();
        //关闭client对象
        client.close();
    }

    @Test
    //删除索引库的方法
    public void delIndex(){
        //使用client对象创建一个索引库
        client.admin().indices().prepareDelete("blog").get();
        client.close();
    }

    @Test
    //设置mapping的方法
    public void setMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("Article")
                        .startObject("properties")
                            .startObject("id")
                                .field("type","long")
                                .field("store",true)
                            .endObject()
                            .startObject("title")
                                .field("type","text")
                                .field("store",true)
                                .field("analyzer","ik_smart")
                            .endObject()
                            .startObject("content")
                                .field("type","text")
                                .field("store",true)
                                .field("analyzer","ik_smart")
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();
                //使用client把Mapping信息设置到索引库
        client.admin().indices()
                //设置要做映射的索引
                .preparePutMapping("blog")
                //设置要做映射的Type
                .setType("Article")
                //设置mapping信息
                .setSource(builder)
                .get();
        client.close();
    }

    @Test
    //第一钟添加数据的方法
    public void setContent() throws IOException{
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .field("id",1)
                    .field("title","湖南人民好")
                    .field("content","是真的好")
                .endObject();
        client.prepareIndex()
                .setIndex("blog")
                .setType("Article")
                .setSource(builder)
                .get();
    }

    @Test
    //第二钟添加数据的方法Json字符串
    public void setContent2() throws IOException{
        Article article = new Article();
        article.setId(7);
        article.setTitle("这个是一个安静的晚上1");
        article.setContent("我独自徘徊在乡间的小路上1");
        ObjectMapper objectMapper = new ObjectMapper();
        String value = objectMapper.writeValueAsString(article);
        client.prepareIndex()
                .setIndex("blog")
                .setType("Article")
                .setSource(value, XContentType.JSON)
                .get();
        client.close();
    }

    @Test
    //删除数据的方法1
    public void delContent() {
        DeleteResponse response = client.prepareDelete("blog", "Article", "AXiD8piT_PLet4MPmaol").get();
        System.out.println(response.status().getStatus());
        client.close();
    }

    @Test
    //删除数据的方法2
    //这个ID不是设置的Id而是默认的那个_id
    public void delContent2() {
        DeleteResponse response = client.prepareDelete()
                .setIndex("blog")
                .setType("Article")
                .setId("5")
                .get();
        System.out.println(response.status().getStatus());
        client.close();
    }

    @Test
    //测试插入多条数据
    public void insertMore() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Article article = new Article();
        for (int i = 1; i < 20; i++) {
            article.setId(i);
            article.setTitle("这个是一个安静的晚上"+i);
            article.setContent("我独自徘徊在乡间的小路上"+i);
            String value = objectMapper.writeValueAsString(article);
            client.prepareIndex()
                    .setIndex("blog")
                    .setType("Article")
                    .setSource(value, XContentType.JSON)
                    .get();
        }
        client.close();
    }
}
