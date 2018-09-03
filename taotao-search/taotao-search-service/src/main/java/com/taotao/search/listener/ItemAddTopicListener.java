package com.taotao.search.listener;

import com.taotao.common.pojo.SearchItem;
import com.taotao.search.mapper.SearchItemMapper;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 添加商品之后更新索引库
 */
public class ItemAddTopicListener implements MessageListener {

    @Autowired
    private SearchItemMapper searchItemMapper;

    @Autowired
    private HttpSolrServer httpSolrServer;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            long itemid = Long.parseLong(text);
            //延时1秒
            Thread.sleep(1000);
            //查询商品信息
            SearchItem searchItem = searchItemMapper.getItemById(itemid);

            SolrInputDocument document = new SolrInputDocument();
            //向文档中添加域
            document.addField("id", searchItem.getId());
            document.addField("item_title", searchItem.getTitle());
            document.addField("item_sell_point", searchItem.getSell_point());
            document.addField("item_price", searchItem.getPrice());
            document.addField("item_image", searchItem.getImage());

            System.out.println(searchItem.getImage());

            document.addField("item_category_name", searchItem.getCategory_name());
            httpSolrServer.add(document);
            //提交
            httpSolrServer.commit();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
