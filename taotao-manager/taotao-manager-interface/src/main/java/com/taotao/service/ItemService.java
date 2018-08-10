package com.taotao.service;

import com.taotao.common.EasyuiPage;
import com.taotao.pojo.TbItem;

public interface ItemService {

    TbItem getItemById(long itemId);

    EasyuiPage getItemList(Integer page, Integer rows);
}
