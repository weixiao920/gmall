package com.wang.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.nacos.client.utils.StringUtils;
import com.wang.gmall.bean.PmsSearchParam;
import com.wang.gmall.bean.PmsSearchSkuInfo;
import com.wang.gmall.bean.PmsSkuAttrValue;
import com.wang.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 微笑
 * @date 2019/11/25 21:30
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {
        return getSearch(pmsSearchParam);
    }

    List<PmsSearchSkuInfo> getSearch(PmsSearchParam pmsSearchParam) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueId = pmsSearchParam.getValueId();

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        /**
         * jest的dsl工具
         */
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * Query
         *  bool
         *      filter
         *          term
         *          terms
         *      must
         *          match
         * from
         * size
         * highlight
         */
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        /**
         * filter 过滤 term/terms 过滤条件
         * 三级分类不为空，则查询
         */
        if (StringUtils.isNotBlank(catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        /**
         * filter 过滤 term/terms 过滤条件
         * sku属性值不为空，则查询
         */
        if (valueId != null) {
            for (String pmsSkuAttrValue : valueId) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue);
                boolQueryBuilder.filter(termQueryBuilder);
            }

        }
        /**
         * must  - match 查询条件
         * 如果关键字不为空，用关键字查询
         */
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //query dsl查询语句
        searchSourceBuilder.query(boolQueryBuilder);
        /**
         * 查询范围
         */
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        /**
         * 根据id排序
         * asc:升序
         * desc:降序
         */
        searchSourceBuilder.sort("id", SortOrder.DESC);
        /**
         * highlight
         * 关键字不为空则关键字高亮显示
         */
        if(StringUtils.isNotBlank(keyword)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red;'>");
            highlightBuilder.field("skuName");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        String dslStr = searchSourceBuilder.toString();

        Search search = new Search.Builder(dslStr).addIndex("gmall0105").addType("PmsSkuInfo").build();

        SearchResult execute = null;
        try {
            execute = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * 返回List<PmsSearchSkuInfo>集合
         */
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            /**
             * 解析高亮显示
             */
            Map<String, List<String>> highlight = hit.highlight;
            if(highlight!=null) {
                String skuName = highlight.get("skuName").get(0);
                source.setSkuName(skuName);
            }
            pmsSearchSkuInfos.add(source);
        }

        return pmsSearchSkuInfos;
    }
}
