package io.renren.modules.job.task;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@Component("Readpropertise")
public class Readpropertise {
    List<String> resultvalue = new ArrayList<String>();
    List<String> resultkey = new ArrayList<String>();
    String[] value;
    String[] key;
    public void Read() {
        Properties prop = new Properties();
        try{
            //读取属性文件a.properties
            InputStream in = new BufferedInputStream (new FileInputStream("D:\\repository\\editpropertise\\pro0904.properties"));
            prop.load(in);     ///加载属性列表
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key1=it.next();
                resultkey.add(key1);
                String propertiesFileEncode = "utf-8";
                String newValue = new String(prop.getProperty(key1).getBytes("ISO-8859-1"),propertiesFileEncode);
                resultvalue.add(newValue);
                //System.out.println(key+" : "+newValue);
            }
            in.close();

            ///保存属性到b.properties文件
            /*FileOutputStream oFile = new FileOutputStream("pro0904.properties", true);//true表示追加打开
            prop.setProperty("phone", "10086");
            prop.store(oFile, "The New properties file");
            oFile.close();*/
        }
        catch(Exception e){
            System.out.println(e);
        }

        value = resultvalue.toArray(new String[resultvalue.size()]);
        key = resultkey.toArray(new String[resultkey.size()]);


    }


    public void Application() {
        Readpropertise r = new Readpropertise();
        r.Read();
        Writetotxt w = new Writetotxt();
        Edit e = new Edit();
        try {
            w.write(e.make(r.key,r.value));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}