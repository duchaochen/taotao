package com.taotao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyuiPage;
import com.taotao.common.pojo.IDUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper tbItemMapper;

	@Autowired
	private TbItemDescMapper itemDescMapper;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Resource(name="itemAddTopic")
	private Destination destination;

	@Autowired
	private JedisClient jedisClient;

	@Value("${ITEM_INFO}")
	private String ITEM_INFO;

	@Value("${TIEM_EXPIRE}")
	private Integer TIEM_EXPIRE;

	@Override
	public EasyuiPage getItemList(Integer page, Integer rows) {
		//设置分页控件的起始页码和每页查询多少行
		PageHelper.startPage(page,rows);
		List<TbItem> tbItems = tbItemMapper.selectByExample(new TbItemExample());
		PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(tbItems);
		EasyuiPage easyuiPage = new EasyuiPage();
		easyuiPage.setRows(pageInfo.getList());
		easyuiPage.setTotal(pageInfo.getTotal());
		return easyuiPage;
	}

	@Override
	public TaotaoResult addItem(TbItem item, String desc) {

		//生成商品id
		final long itemId = IDUtils.genItemId();
		//补全item的属性
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		//向商品表插入数据
		tbItemMapper.insert(item);
		//创建一个商品描述表对应的pojo
		TbItemDesc itemDesc = new TbItemDesc();
		//补全pojo的属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(new Date());
		itemDesc.setCreated(new Date());
		//向商品描述表插入数据
		itemDescMapper.insert(itemDesc);

		//发送消息队列
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				//发送消息
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		//返回结果
		return TaotaoResult.ok();
	}

    /**
     * 查询商品
     * @param itemId
     * @return
     */
    @Override
    public TbItem getItemById(long itemId) {
        //1.查询缓存是否存在，如果存在直接返回
        try {
			String item = jedisClient.get(ITEM_INFO + ":" + itemId + ":BASE");
			if (StringUtils.isNotBlank(item)) {
				TbItem tbItem = JsonUtils.jsonToPojo(item, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
        //2.如果不存在，则查询数据库，在加入缓存
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);

		try {
			//添加缓存
			jedisClient.set(ITEM_INFO + ":" + itemId + ":BASE", JsonUtils.objectToJson(tbItem));
			//设置缓存失效时间
			jedisClient.expire(ITEM_INFO + ":" + itemId + ":BASE",TIEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return tbItem;
    }

    /**
     * 查询商品详细
     * @param itemId
     * @return
     */
    @Override
    public TbItemDesc getItemDescById(long itemId) {
		try {
			//获取缓存，如果缓存有就直接返回
			String s = jedisClient.get(ITEM_INFO + ":" + itemId + ":DESC");
			if (StringUtils.isNotBlank(s)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(s, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        //缓存中没有查询数据库
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		try {
			//添加到缓存
			jedisClient.set(ITEM_INFO + ":" + itemId + ":DESC",JsonUtils.objectToJson(itemDesc));
			//设置缓存失效
			jedisClient.expire(ITEM_INFO + ":" + itemId + ":DESC",TIEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return itemDesc;
    }
}