//package com.kuangyu;
//
//import com.alibaba.fastjson.JSON;
//import com.viewshine.mall.entity.LavaI18n;
//import com.viewshine.mall.entity.LavaI18nExample;
//import com.viewshine.mall.mapper.LavaI18nMapper;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Date;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class MybatisTest {
//
//    private static final Logger logger = LoggerFactory.getLogger(MybatisTest.class);
//
//    @Autowired
//    LavaI18nMapper lavaI18nMapper;
//
//    @Test
//    public void testInsertSelective() {
//        LavaI18n lavaI18n = new LavaI18n();
//        lavaI18n.setI18nLang("zh");
//        lavaI18n.setI18nKey("testKey");
//        lavaI18n.setI18nMessage("testMessage");
//        lavaI18n.setModule("testModule");
//        lavaI18n.setGmtCreate(new Date());
//        lavaI18nMapper.insertSelective(lavaI18n);
//
//        System.out.println(lavaI18n);
//        System.out.println(lavaI18n.getId());
//        logger.debug("ID: {}", lavaI18n.getId());
//    }
//
//    @Test
//    public void testUpdateByIdSelective() {
//
//        LavaI18n lavaI18n = new LavaI18n();
//        lavaI18n.setI18nLang("zh");
//        lavaI18n.setI18nKey("testKey");
//        lavaI18n.setI18nMessage("testMessage");
//        lavaI18n.setModule("testModule");
//        lavaI18n.setId(1);
//        lavaI18n.setGmtModified(new Date());
//
//        lavaI18nMapper.updateByPrimaryKeySelective(lavaI18n);
//
//    }
//
//    @Test
//    public void testDeleteById() {
//        LavaI18n lavaI18n = new LavaI18n();
//        lavaI18n.setId(1);
//
//        lavaI18nMapper.deleteByPrimaryKey(lavaI18n);
//    }
//
//    @Test
//    public void testSelectById() {
//        LavaI18n lavaI18n = new LavaI18n();
//        lavaI18n.setId(2);
//
//        lavaI18n = lavaI18nMapper.selectByPrimaryKey(lavaI18n);
//        System.out.println(JSON.toJSONString(lavaI18n));
//        logger.debug("LavaI18n: {}", JSON.toJSONString(lavaI18n));
//    }
//
//    @Test
//    public void testUpdateByExampleSelective() {
//        LavaI18n lavaI18n = new LavaI18n();
//        lavaI18n.setI18nLang("en");
//        lavaI18n.setI18nMessage("testMessage2");
//        lavaI18n.setId(2);
//        lavaI18n.setGmtModified(new Date());
//
//        LavaI18nExample example = new LavaI18nExample();
//        example.createCriteria().andI18nKeyEqualTo("testKey");
//
//        lavaI18nMapper.updateByExampleSelective(lavaI18n, example);
//
//    }
//
//
//}
