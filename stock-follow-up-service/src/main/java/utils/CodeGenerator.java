package utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.io.File;
import java.io.IOException;


public class CodeGenerator {
    //RYAgentDB.Agent_SystemStatusInfo
    private static final String GLOBAL_PARENT = "";
    private static final String DB_NAEM = "stock_follow_up";
    private static final boolean ALL_TABLE = true;
    private static final String GLOBAL_OUTPUT_DIR = "/src/main/java";

    public static void main(String[] args) throws IOException {
        AutoGenerator gen = new AutoGenerator();
        //数据源配置
        gen.setDataSource(getDataSourceConfig());
        //全局配置
        gen.setGlobalConfig(getGlobalConfig());
        //策略配置 tables为null时生成库里面所有的表
        if(ALL_TABLE){
            gen.setStrategy(getStrategyConfig(null));
        } else {
            String[] tables = {"tutor_info"};
            gen.setStrategy(getStrategyConfig(tables));
        }
        //包配置
        gen.setPackageInfo(getPackageConfig());
        //设置Freemarker模板引擎
        gen.setTemplateEngine(new VelocityTemplateEngine());

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        //控制 不生成 controller
        templateConfig.setController("");
  
        gen.setTemplate(templateConfig);

        //执行
        gen.execute();
    }



    private static PackageConfig getPackageConfig() {
        return new PackageConfig()
                .setParent(GLOBAL_PARENT)
                .setService("service")
                .setServiceImpl("service.impl")
                .setEntity("entity")
                .setMapper("mapper")
                .setXml("mapper");
    }

    private static StrategyConfig getStrategyConfig(String[] tables) {
        StrategyConfig strategyConfig = new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                //使用lombok
                .setEntityLombokModel(true)
                // 驼峰转连字符
                .setControllerMappingHyphenStyle(true);

        if(tables != null){
            strategyConfig.setInclude(tables);
        }
        return strategyConfig;

    }

    private static GlobalConfig getGlobalConfig() throws IOException {
        //String projectPath = System.getProperty("user.dir");
        //获取项目路径
        String canonicalPath = new File("").getCanonicalPath();
        return new GlobalConfig()
                .setOutputDir(canonicalPath + GLOBAL_OUTPUT_DIR)//输出目录
                .setFileOverride(false)//是否覆盖文件
                .setActiveRecord(true)//activeRecord 实体类继承Model类
                .setEnableCache(false)// XML 二级缓存
                .setBaseResultMap(false)//XML ResultMap
                .setBaseColumnList(false)//XML columList
                .setSwagger2(true)
                .setOpen(false)//生成后打开文件夹
                .setAuthor("")
                // 自定义文件命名，注意 %s 会自动填充表实体属性！
                .setMapperName("%sMapper")
                .setXmlName("%sMapper")
                .setServiceName("%sService")
                .setServiceImplName("%sServiceImpl")
                .setEntityName("%s")
                .setControllerName("")
                ;
    }

    private static DataSourceConfig getDataSourceConfig(){
        return new DataSourceConfig()
                .setDbType(DbType.MYSQL)
                .setDriverName("com.mysql.cj.jdbc.Driver")
                .setUrl("jdbc:mysql://18.140.183.99:3306/" + DB_NAEM + "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true")
                .setUsername("stock-test")
                .setPassword("lg112200")
                //将datetime装换为localdatetime的类型换成date类型
//                .setTypeConvert((globalConfig, fieldType) -> {
//                    System.out.println("转换类型：" + fieldType);
//                     if (fieldType.toLowerCase().contains( "datetime" ) ) {
//                        return DbColumnType.DATE;
//                     }
//                    return new MySqlTypeConvert().processTypeConvert(globalConfig, fieldType);
//                })
                .setTypeConvert((globalConfig, fieldType) -> {
                    System.out.println("转换类型：" + fieldType);
                    if (fieldType.toLowerCase().contains( "decimal" ) ) {
                        return DbColumnType.BIG_DECIMAL;
                    }
                    if (fieldType.toLowerCase().contains( "datetime" ) ) {
                        return DbColumnType.DATE;
                    }
                    if (fieldType.toLowerCase().contains( "date" ) ) {
                        return DbColumnType.DATE;
                    }
                    if (fieldType.toLowerCase().contains( "int" ) ) {
                        return DbColumnType.INTEGER;
                    }
                    if (fieldType.toLowerCase().contains( "tinyint" ) ) {
                        return DbColumnType.BOOLEAN;
                    }
                    if (fieldType.toLowerCase().indexOf( "bigint" ) > -1 ) {
                        return DbColumnType.LONG;
                    }
                    if (fieldType.toLowerCase().contains( "varchar" ) ) {
                        return DbColumnType.STRING;
                    }
                    if (fieldType.toLowerCase().contains( "bit" ) ) {
                        return DbColumnType.BOOLEAN;
                    }
                    if (fieldType.toLowerCase().contains( "smallint" ) ) {
                        return DbColumnType.INTEGER;
                    }
                    if (fieldType.toLowerCase().indexOf ("blob") > -1) {
                        return DbColumnType.STRING;
                    }
                    if (fieldType.toLowerCase().contains( "json" ) ) {
                        return DbColumnType.STRING;
                    }
                    return new MySqlTypeConvert().processTypeConvert(globalConfig, fieldType);
                })
                ;
    }

}
