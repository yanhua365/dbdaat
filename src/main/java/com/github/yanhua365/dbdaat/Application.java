package com.github.yanhua365.dbdaat;


import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application implements CommandLineRunner {

    @Value("${db.url}")
    private String url;
    @Value("${db.driver}")
    private String driver;
    @Value("${db.user}")
    private String user;
    @Value("${db.password}")
    private String password;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Connection conn = null;
        try{
            conn = getConnection(driver,url,user,password);
            IDatabaseConnection connection = new DatabaseConnection(conn);

            if(args.length == 0){
                System.out.println("请输入以下参数:\n");
                System.out.println("import-data [file] \n 导入文件中的数据到数据库，file用于指定导入的数据文件，默认是当前命令下的import-data.xml\n");
                System.out.println("export-data [file] \n 导出数据库中的数据到文件，file用于指定导出的数据文件，默认是当前命令下的export-data.xml\n");
                System.out.println("list-table \n 列出数据库中表\n");
                return;
            }

            System.out.println("========================================操作开始======================================");

            String command = args[0];

            if(command.equals("import-data")){
                String file = "import_data.xml";
                if(args.length == 2){
                    file = args[1];
                }
                IDataSet dataSet = new FlatXmlDataSet(new FileInputStream(file));
                DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
            }else if(command.equals("export-data")){
                String file = "export_data.xml";
                if(args.length == 2){
                    file = args[1];
                }
                IDataSet dataSet = connection.createDataSet();
                FlatXmlDataSet.write(dataSet, new FileOutputStream(file));
            }else if(command.equals("list-table")){
                System.out.println("TODO list tables .....................................");
            }


            System.out.println("========================================操作完成======================================");

        }finally {
            if(conn != null){
                try{conn.close();}catch (Exception e){};
            }
        }

    }



    public Connection getConnection(String driverName,String connectionUrl, String username,
                                    String password) {
        Connection con = null;
        try {
            // registers the specified driver class into memory
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // returns a connection objcet by selecting an appropriate driver
            // for specified connection URL
            con = DriverManager
                    .getConnection(connectionUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
