package com.wang.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wang.gmall.bean.PmsSearchSkuInfo;
import com.wang.gmall.bean.PmsSkuInfo;
import com.wang.gmall.service.PmsSkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GmallSearchServiceApplicationTests {

    @Reference
    PmsSkuInfoService pmsSkuInfoService;

    @Autowired
    JestClient jestClient;

    @Test
    void contextLoads() throws Exception {
       // getSearch();
        putSearch();
    }

    void getSearch() throws  Exception{
        List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();
        /**
         * jest的dsl工具
         */
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        /**
         * Query
         *  bool
         *      filter
         *          term
         *          terms
         *      must
         *          match
         */
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        //filter
        TermQueryBuilder termQueryBuilder=new TermQueryBuilder("skuAttrValueList.valueId","40");
        boolQueryBuilder.filter(termQueryBuilder);
        //must
        MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName","小米");
        boolQueryBuilder.must(matchQueryBuilder);
        //query
        searchSourceBuilder.query(boolQueryBuilder);
        /**
         * 查询范围
         */
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        String dslStr = searchSourceBuilder.toString();
        System.err.println(dslStr);
        Search search=new Search.Builder(dslStr).addIndex("gmall0105").addType("PmsSkuInfo").build();

        SearchResult execute = jestClient.execute(search);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;

            pmsSearchSkuInfos.add(source);
        }
        System.out.println(pmsSearchSkuInfos.size());
    }

    /**
     * 添加search数据
     * @throws IOException
     */
    void putSearch() throws IOException {
        /**
         *  查询mySql数据
         */
        List<PmsSkuInfo> pmsSkuInfos=new ArrayList<>();
        pmsSkuInfos = pmsSkuInfoService.getSkuAll();

        /**
         * 转换为es数据
         */

        List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();

        for(PmsSkuInfo pmsSkuInfo:pmsSkuInfos){
            PmsSearchSkuInfo pmsSearchSkuInfo=new PmsSearchSkuInfo();

            BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);

            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));

            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        /**
         * 导入es
         */

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            System.out.println(pmsSearchSkuInfo.getId());
            Index put = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();

            jestClient.execute(put);

        }
    }

}
