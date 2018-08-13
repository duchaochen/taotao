package com.taotao.content.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

import java.util.List;

public interface ContentService {

    TaotaoResult addContent(TbContent tbContent);
    List<TbContent> getContentByCid(long cid);
}
