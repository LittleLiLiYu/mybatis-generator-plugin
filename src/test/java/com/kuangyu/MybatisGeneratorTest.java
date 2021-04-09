package com.kuangyu;

import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MybatisGeneratorTest {

    private static final Logger logger = LoggerFactory.getLogger(MybatisGeneratorTest.class);

    @Test
    public void generate() {
        try {

            System.out.println("------Begin------");
            List<String> warnings = new ArrayList<String>();
            boolean overwrite = true;
            String configPath = "F:\\mybatis-generator-plugin\\src\\test\\resources\\generatorConfig.xml";
            File configFile = new File(configPath);
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
            System.out.println("------End------");
        } catch (Exception e) {
            System.out.println(e.toString());
            logger.error(e.getMessage(), e);
        }
    }


}
