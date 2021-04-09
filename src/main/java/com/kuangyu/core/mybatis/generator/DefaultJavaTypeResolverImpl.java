package com.kuangyu.core.mybatis.generator;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 *
 * @author kuangyu
 */
public class DefaultJavaTypeResolverImpl extends JavaTypeResolverDefaultImpl {

    public DefaultJavaTypeResolverImpl() {
        super();
        typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT",
                                                           new FullyQualifiedJavaType(Integer.class.getName())));
    }
}
