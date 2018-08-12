package com.taotao.service;

import com.taotao.common.pojo.EasyuiPage;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;

public interface ItemService {

    TbItem getItemById(long itemId);

    EasyuiPage getItemList(Integer page, Integer rows);

    TaotaoResult addItem(TbItem item, String desc);
}
