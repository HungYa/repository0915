package io.renren.modules.job.task;

public class Edit {
    public String[] make(String[] key,String[] value) {
        String result = "";
        String[][] keyvalue = new String[value.length][];
        String[] json = new String[key.length];
        System.out.println(key.length);
        for(int i = 0;i< key.length;i++) {
            //System.out.println("第"+i+"轮");
            keyvalue[i] = value[i].split(" ");
            String[] id = new String[keyvalue[i].length];
            for(int j = 0;j < keyvalue[i].length;j++){
                id[j] = j+1+ "";
            }
            result = key[i] + "=[";
            for(int j = 0;j < keyvalue[i].length;j++) {
                if(keyvalue[i].length < 10) {
                    keyvalue[i][j] = keyvalue[i][j].substring(1,keyvalue[i][j].length());
                } else if(keyvalue[i].length >9 ){
                    keyvalue[i][j] = keyvalue[i][j].substring(2,keyvalue[i][j].length());
                }
                result =  result+"{id\\:" + id[j]+",text\\:'" + string2Unicode(keyvalue[i][j])+ "'},";
                if(j == keyvalue[i].length-1) {
                    result = result.substring(0,result.length()-1) + "]";
                }
            }
            json[i] = result;
            //System.out.println(json[i]);
        }
        //System.out.println(result);
        return json;
    }

    /*public  String string2Unicode(String string) {

        char[] chars = string.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            String result = Integer.toString(chars[i], 16);
            if(result.length() < 4) {
                result = "00" + result;
            }
            returnStr += "\\u" + result;
            System.out.println(returnStr);
        }
        return returnStr;
    }*/

    public  String string2Unicode(String str) {
        String result = "";
        UnicodeUtil u =new UnicodeUtil();
        try {
            result = u.encode(str);
            //System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
