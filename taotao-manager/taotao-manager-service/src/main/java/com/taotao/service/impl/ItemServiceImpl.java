package com.taotao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.EasyuiPage;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public TbItem getItemById(long itemId) {
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        return tbItem;
    }

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
}
