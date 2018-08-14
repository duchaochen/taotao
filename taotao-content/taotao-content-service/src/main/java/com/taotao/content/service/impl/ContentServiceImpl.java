package com.taotao.content.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClientPool;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private JedisClientPool jedisClientPool;

    @Override
    public TaotaoResult addContent(TbContent tbContent) {

        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        int insert = contentMapper.insert(tbContent);
        //添加之后需要删除缓存，让前台查询的时候重新加载缓存就可以显示新的数据了
        jedisClientPool.hdel("INDEX_CONTENT",tbContent.getCategoryId().toString());
        return TaotaoResult.ok();
    }

    @Override
    public List<TbContent> getContentByCid(long cid) {

        //首先在缓存中获取数据，如果已经有了对应的数据直接返回，没有在往后执行
        try {
            String index_content = jedisClientPool.hget("INDEX_CONTENT", cid + "");
            //如果缓存中取出的值不等于空就证明
            if (StringUtils.isNotBlank(index_content)) {
                return JsonUtils.jsonToList(index_content,TbContent.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //缓存中没有命中，需要查询数据库
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        //设置查询条件
        criteria.andCategoryIdEqualTo(cid);
        //执行查询
        List<TbContent> list = contentMapper.selectByExample(example);

        try {
            //将取出来的值在更新到缓存中
            jedisClientPool.hset("INDEX_CONTENT", cid + "",JsonUtils.objectToJson(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
