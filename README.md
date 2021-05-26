# mybatis-generator-plugin

本插件对mybatis-generator-maven-plugin进行扩展, 依赖一个BaseMapper, 所有生成的XXMapper.java继承BaseMapper.java, 生成两个.xml, XXMapper.xml对应BaseMapper中方法的实现（重新生成时会覆盖）, XXExtMapper.xml和XXMapper.java 在重新生成时不会覆盖
