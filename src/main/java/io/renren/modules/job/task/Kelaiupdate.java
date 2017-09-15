package io.renren.modules.job.task;

import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


@Component("Kelaiupdate")
public class Kelaiupdate {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /*@Autowired
    private SysUserService sysUserService;*/

    private String[] skelai;  //存放配置文件中科莱项目名称
    private String[] shospital;  //存放配置文件中医院项目编号
    private String[] scbh;   //存放医院数据库中需要上传到科莱的编号
    private String[] xkelai;  //存放小项目对应表
    private String[] xhospital;  //存放单个编号的groupbh
    private String[] hospitalgroupbh;

    Map<String,String> map = new HashMap<>();
    Map<String,String> map2 ;
    Map<String,String> map3 = new HashMap<>();
    List<String> dyxm = new ArrayList<>();
    List<String> groupbh = new ArrayList<>();
    DBConnection db = new DBConnection();
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int maxid;
    int maxtransferid;
    int maxtestresultId;

    // 读取配置文件中的科莱项目名称和医院项目编码，并使用findbh方法查找需要上传的编号
    // 配置文件中应当写好写全需要到科莱检测的项目，格式为：科莱项目=医院编号
    public void readjson() {  //读取医院小项目明细（6位）和克莱小项目编码，需要插入testresult表中
        Properties prop = new Properties();
        List<String> kelaixm = new ArrayList<>();
        List<String> hospitalbh = new ArrayList<>();
        String path1 = "G:\\KelaiCommon.properties";

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(path1));
            prop.load(in);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            System.out.println("读取项目明细配置文件：");
            while(it.hasNext()){
                String key=it.next();
                kelaixm.add(key);
                hospitalbh.add(prop.getProperty(key));

                //System.out.println(key + "   " + prop.getProperty(key) );
            }

            in.close();

            skelai =  kelaixm.toArray(new String[kelaixm.size()]);
            shospital =  hospitalbh.toArray(new String[hospitalbh.size()]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //findbh();
    }


    public void readxmbh() {   //读取医院套餐组（4位）和克莱对应编码（需要插入克莱peopleinfo表中）
        Properties prop = new Properties();

        String path1 = "G:\\xm.properties";

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(path1));
            prop.load(in);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            System.out.println("读取项目组合配置文件：");
            while(it.hasNext()){
                String key=it.next();

                String key1 = prop.getProperty(key);

                //System.out.print(key1+ "   ");
                //System.out.println(key);
                map.put(key1,key);

            }

            in.close();



        } catch (IOException e) {
            e.printStackTrace();
        }
        //findgroupbh();

    }

    //连接本底hospital数据库，根据配置文件查找需要上传到克莱的编号（14位）

    public void findbh() {
        Connection cnn = db.getDBConnection("hospitalread");

        try {
            Statement preStmt1 = cnn.createStatement();
            List<String> scbh1 = new ArrayList<>();
            //ResultSet rs1  = preStmt1.executeQuery("select bh from tj_result where bh in (select bh from tj_dj where status < 25) and groupbh in (0627,0632,0634,0642) and tjrq > '"+dt.format(new Date())+"' group by bh");
            ResultSet rs1  = preStmt1.executeQuery("select bh from tj_result where bh in (select bh from tj_dj where status < 25) and tjrq > '"+getSpecifiedDayBefore(dt.format(new Date()))+"' and bh not in (select bh from transfer_kelai_record) group by bh");
            while(rs1.next()){
                String bh = rs1.getString(1);
                scbh1.add(bh);
            }
            scbh =  scbh1.toArray(new String[scbh1.size()]);
            //System.out.println(scbh1.size());
            cnn.close();
            /*for(String s : scbh) {
                System.out.println(s);
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //insertkelai();
    }

    public void findgroupbh() {  //查找需要上传的编号的某个人所需要克莱检验的套餐组编号（4位）
        Connection cnn = db.getDBConnection("hospitalread");

        try {
            Statement preStmt2 = cnn.createStatement();

            String mapvalue;
            //System.out.println(scbh.length);
            for(int i = 0;i<scbh.length;i++) {
                //

                map2 = new HashMap<>();
                List<String> scbh1 = new ArrayList<>();
                mapvalue = "";
                //ResultSet rs1  = preStmt1.executeQuery("select bh from tj_result where bh in (select bh from tj_dj where status < 25) and groupbh in (0627,0632,0634,0642) and tjrq > '"+dt.format(new Date())+"' group by bh");
                ResultSet rs1 = preStmt2.executeQuery("select groupbh from tj_package_mx where bh in (select packagebh from tj_dj where bh = "+scbh[i]+") group by groupbh");
                while (rs1.next()) {
                    String bh = rs1.getString(1);
                    scbh1.add(bh);
                    //System.out.println(bh);

                }
                hospitalgroupbh = scbh1.toArray(new String[scbh1.size()]);
                //System.out.println("length:" + hospitalgroupbh.length + "   " + scbh[i]);


                for(int j = 0;j < hospitalgroupbh.length;j++) {
                    if(map.containsKey(hospitalgroupbh[j])) {
                        map2.put(hospitalgroupbh[j],map.get(hospitalgroupbh[j]));

                        //System.out.print("mark:" + hospitalgroupbh[j]);
                        //System.out.println("    " + map.get(hospitalgroupbh[j]));
                    }
                }
                for(String value:map2.values()) {
                    mapvalue = mapvalue +","+ value;
                }
                mapvalue = mapvalue.substring(1);
                System.out.println("bh:" + scbh[i]);
                System.out.println("markvalue:" + mapvalue);
                map3.put(scbh[i],mapvalue);



            }

            /*for(String key:map3.keySet()) {
                System.out.println("map3 key:" + key);
            }
            for(String value:map3.values()) {
                System.out.println("map3 kvalue:" + value);
            }*/

            cnn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertkelai() {  //插入科莱数据库peopleinfo表和testresult表
        String xm = "";



        Connection cnn = db.getDBConnection("kelaisc");

        try {
            PreparedStatement preStmt = cnn.prepareStatement("insert into peopleinfo(id,idcard,name,flowno,items,flag,cjsj) values(?,?,?,?,?,?,?)");
            cnn.setAutoCommit(false);
            for(int i = 0;i < scbh.length; i++) {
                maxid++;
                xm = map3.get(scbh[i]);
                preStmt.setString(1, maxid+"");
                preStmt.setString(2, "");
                preStmt.setString(3, "");
                preStmt.setString(4, scbh[i]);
                preStmt.setString(5, xm.toString());
                preStmt.setString(6, "0");
                preStmt.setString(7, dt.format(new Date()));
                preStmt.addBatch();
                transferupdate(scbh[i]);
            }
            preStmt.executeBatch();
            if(scbh.length == 0 ){
                System.out.println("本次未发现需要上传的体检数据，请稍后重试！");
            }else {
                System.out.println("插入kelaitest_peopleinfo成功,共插入了"+scbh.length+"条数据");
            }


            cnn.commit();
            cnn.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getmaxId() {  //获取科莱peopleinfo表中最大ID
        Connection cnn = db.getDBConnection("kelaisc");
        try {
            Statement preStmt2 = cnn.createStatement();
            ResultSet rs1 = preStmt2.executeQuery("select max(peopleinfo.id) from peopleinfo");
            while (rs1.next()) {
                maxid = rs1.getInt(1);

            }
            cnn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getmaxtransferId() {  //获取transfer_kelai_record 表中最大id
        Connection cnn = db.getDBConnection("hospitalread");
        try {
            Statement preStmt2 = cnn.createStatement();
            ResultSet rs1 = preStmt2.executeQuery("select max(transfer_kelai_record.id) from transfer_kelai_record");
            while (rs1.next()) {
                maxtransferid = rs1.getInt(1);

            }
            cnn.close();
            //System.out.println(maxtransferid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getmaxtestresultId() {  //获取科莱testresult表中最大id
        Connection cnn = db.getDBConnection("kelaisc");
        try {
            Statement preStmt = cnn.createStatement();
            ResultSet rs1 = preStmt.executeQuery("select max(testresult.id) from testresult");
            while (rs1.next()) {
                maxtestresultId = rs1.getInt(1);

            }
            cnn.close();
            //System.out.println(maxtransferid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferupdate(String bh) {
        //getmaxtransferId();
        Connection cnn1 = db.getDBConnection("hospitalread");
        try {
            PreparedStatement preStmt1 = cnn1.prepareStatement("insert into transfer_kelai_record(id,bh,zt) value(?,?,?)");
            preStmt1.setString(1,""+ ++maxtransferid);
            preStmt1.setString(2,bh);
            preStmt1.setString(3,""+0);
            preStmt1.executeUpdate();
            //System.out.println("transfer 插入成功");
            cnn1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertkelaitestresult(boolean flag) {
        int temp=0;
        Connection cnn = db.getDBConnection("kelaisc");
        try{
            if(flag == true) {
                for(int i = 0;i< scbh.length;i++) {
                    for(int j = 0;j <skelai.length;j++) {
                        String groupbhfromxmbh = getgroupbhfromxmbh(scbh[i],shospital[j]);
                        if(groupbhfromxmbh == null) {continue;}

                        PreparedStatement preStmt1 = cnn.prepareStatement("insert into testresult3(id,idcard,name,flowno,testitem,testresult,groupbh,unitname,lowerlimit,upperlimit) value(?,?,?,?,?,?,?,?,?,?)");
                        preStmt1.setString(1, "" + ++maxtestresultId);
                        preStmt1.setString(2, "");
                        preStmt1.setString(3, "");
                        preStmt1.setString(4, scbh[i]);
                        preStmt1.setString(5, skelai[j]);
                        preStmt1.setString(6, null);
                        preStmt1.setString(7, getgroupbhfromxmbh(scbh[i],shospital[j]));
                        preStmt1.setString(8, null);
                        preStmt1.setString(9, null);
                        preStmt1.setString(10, null);
                        temp++;
                        preStmt1.executeUpdate();
                    }
                }
                System.out.println("插入kelaitest_testresult成功,共插入了"+temp+"条数据");
            }
            if(flag == false) {
                for(int i = 0;i< scbh.length;i++) {
                    for(int j = 0;j <skelai.length;j++) {
                        String groupbhfromxmbh = getgroupbhfromxmbh(scbh[i],shospital[j]);
                        if(groupbhfromxmbh == null) {continue;}

                        PreparedStatement preStmt1 = cnn.prepareStatement("insert into testresult(id,idcard,name,flowno,testitem,testresult,groupbh,unitname,lowerlimit,upperlimit) value(?,?,?,?,?,?,?,?,?,?)");
                        preStmt1.setString(1, "" + ++maxtestresultId);
                        preStmt1.setString(2, "");
                        preStmt1.setString(3, "");
                        preStmt1.setString(4, scbh[i]);
                        preStmt1.setString(5, shospital[j]);
                        preStmt1.setString(6, null);
                        preStmt1.setString(7, groupbhfromxmbh);
                        preStmt1.setString(8, null);
                        preStmt1.setString(9, null);
                        preStmt1.setString(10, null);
                        temp++;
                        preStmt1.executeUpdate();
                    }
                }
                System.out.println("插入kelaitest_testresult成功,共插入了"+temp+"条数据");
            }
            cnn.close();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public String getgroupbhfromxmbh(String bh,String xmbh) {
        String groupbh = null;

        Connection cnn = db.getDBConnection("hospitalread");
        try {
            Statement preStmt3 = cnn.createStatement();
            ResultSet rs3 = preStmt3.executeQuery("select groupbh from tj_result where xmbh = '"+ xmbh +"' and bh = '" + bh + "'");
            while (rs3.next()) {
                groupbh = rs3.getString(1);

                //System.out.println(groupbh);
            }
            cnn.close();
            //System.out.println(maxtransferid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupbh;
    }

    public static String getSpecifiedDayBefore(String specifiedDay){
//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day-31);

        String dayBefore=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    public void Application() {
        Runnable runnable = new Runnable() {
            public void run() {
                //while (true) {
                    // ------- code for task to run
                    System.out.println("start!");
                    System.out.println("从"+getSpecifiedDayBefore(dt.format(new Date()))+"开始");
                    Kelaiupdate r = new Kelaiupdate();
                    r.readjson();
                    r.readxmbh();
                    r.findbh();
                    r.findgroupbh();
                    r.getmaxtransferId();
                    r.getmaxId();
                    r.getmaxtestresultId();
                    r.insertkelai();
                    r.insertkelaitestresult(false);
                    System.out.println("end!");
                    // ------- ends here
                    /*try {
                        Thread.sleep(200000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                //}
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        /*SysUserEntity user = sysUserService.queryObject(1L);
        System.out.println(ToStringBuilder.reflectionToString(user));*/

    }



}
