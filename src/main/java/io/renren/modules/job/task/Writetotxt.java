package io.renren.modules.job.task;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
public class Writetotxt {
    public void write(String[] str) throws Exception{
        //在g盘上创建一个名为testfile的文本文件
        File f = new File("G:"+File.separator+"testfile.txt");
        //用FileOutputSteam包装文件，并设置文件可追加
        OutputStream out = new FileOutputStream(f,false);
        //字符数组
        for(int i =0; i<str.length; i++){
            out.write(str[i].getBytes()); //向文件中写入数据
            System.out.println(str[i]);
            out.write('\r'); // \r\n表示换行
            out.write('\n');
        }
        out.close();	//关闭输出流
        System.out.println("写入成功！");
    }
}