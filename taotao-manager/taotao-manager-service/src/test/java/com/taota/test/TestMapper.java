package com.taota.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.List;

public class TestMapper {

    @Test
    public void testPageHelper() {
        /**
         * 1.加载spring容器
         * 2.获取mapper对象
         * 3.获取数据
         * 4.封装分页对象
         */
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
        TbItemMapper bean = context.getBean(TbItemMapper.class);

        //第一页取10条
        PageHelper.startPage(1,10);

        TbItemExample tbItemExample = new TbItemExample();

        List<TbItem> tbItems = bean.selectByExample(tbItemExample);

        PageInfo<TbItem> pageInfo = new PageInfo<>(tbItems);
        System.out.println("总页码"+pageInfo.getPages());
        System.out.println("总计路数"+pageInfo.getTotal());
        System.out.println("当前记录数"+pageInfo.getList().size());
    }
}
