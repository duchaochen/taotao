package com.taotao.search.service;

import com.taotao.common.pojo.TaotaoResult;

import java.io.IOException;

public interface SearchItemService {

    TaotaoResult importItemsToIndex() throws IOException;
}
