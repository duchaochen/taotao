package com.taotao.solrj;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;

public class TestSolrj {

    @Test
    public void testSolrj() throws IOException, SolrServerException {
        //创建一个SolrServer对象，创建一个HttpSolrServer对象
        SolrServer solrServer = new HttpSolrServer("http://118.25.193.43:8080/solr/collection1");
        //创建一个文档对象
        SolrInputDocument solrInputDocument = new SolrInputDocument();

        //像文档中添加一个域
        solrInputDocument.addField("id","test001");
        solrInputDocument.addField("item_title","测试商品1");
        solrInputDocument.addField("item_price", 1000);

        solrServer.add(solrInputDocument);
        solrServer.commit();
    }
}
