package com.taotao.content.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.content.service.ContentService;
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

    @Override
    public TaotaoResult addContent(TbContent tbContent) {
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        int insert = contentMapper.insert(tbContent);
        return TaotaoResult.ok();
    }

    @Override
    public List<TbContent> getContentByCid(long cid) {
//        //先查询缓存
//        //添加缓存不能影响正常业务逻辑
//        try {
//            //查询缓存
//            String json = jedisClient.hget(INDEX_CONTENT, cid + "");
//            //查询到结果，把json转换成List返回
//            if (StringUtils.isNotBlank(json)) {
//                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
//                return list;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //缓存中没有命中，需要查询数据库
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        //设置查询条件
        criteria.andCategoryIdEqualTo(cid);
        //执行查询
        List<TbContent> list = contentMapper.selectByExample(example);
//        //把结果添加到缓存
//        try {
//            jedisClient.hset(INDEX_CONTENT, cid + "", JsonUtils.objectToJson(list));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //返回结果
        return list;
    }
}
