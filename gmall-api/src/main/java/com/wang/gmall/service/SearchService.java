package com.wang.gmall.service;

import com.wang.gmall.bean.PmsSearchParam;
import com.wang.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

/**
 * @author 微笑
 * @date 2019/11/25 21:29
 */
public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
