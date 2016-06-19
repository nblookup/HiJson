/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hi.chyl.json;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author CangYan
 */
public class Kit {
    public final static String split = " : ";
    
    public final static String sign = "-" ;
    
    public final static String cNull = "k";
    public final static String cNum  = "n";
    public final static String cObj  = "o";
    public final static String cArr  = "a";
    public final static String cStr  = "v";
    public final static String cBool = "b";

    public final static String sNull = cNull + sign;
    public final static String sNum  = cNum + sign;
    public final static String sObj  = cObj + sign;
    public final static String sArr  = cArr + sign;
    public final static String sStr  = cStr + sign;
    public final static String sBool = cBool + sign;
   
    public final static String array   = "Array";
    public final static String object  = "Object";

    public static DefaultMutableTreeNode nullNode(String key){
        return treeNode(sNull + key + split +  "<null>");
    }
    public static DefaultMutableTreeNode nullNode(int index){
        return nullNode(fkey(index));
    }

    public static DefaultMutableTreeNode numNode(String key,String val){
        return treeNode(sNum + key + split + val);
    }
    public static DefaultMutableTreeNode numNode(int index,String val){
        return numNode(fkey(index),val);
    }

    public static DefaultMutableTreeNode boolNode(String key,Boolean val){
        String sVal = "false";
         if (val){
             sVal = "true";
         }
        return  treeNode(sBool + key + split + sVal);
    }

    public static DefaultMutableTreeNode boolNode(int index,Boolean val){
       return  boolNode(fkey(index),val);
    }

    public static DefaultMutableTreeNode strNode(String key,String val){
        return  treeNode(sStr + key + split +"\"" + val + "\"");
    }
    public static DefaultMutableTreeNode strNode(int index,String val){
        return  strNode(fkey(index),val);
    }

    public static DefaultMutableTreeNode objNode(String key){
        return treeNode(sObj+key);
    }
    
    public static DefaultMutableTreeNode objNode(int index){
        return objNode(fkey(index));
    }

     public static DefaultMutableTreeNode arrNode(String key){
        return treeNode(sArr+key);
    }

    public static DefaultMutableTreeNode arrNode(int index){
        return arrNode(fkey(index));
    }


    public static DefaultMutableTreeNode treeNode(String str){
        return new DefaultMutableTreeNode(str);
    }

    public static DefaultMutableTreeNode treeNode(String type,int index,String val){
        return treeNode(type +"[" + index + "]");
    }

    public static String fkey(int index){
        return "[" + index + "]";
    }
    
    //"a-[" + i + "]"
    public static String fArrKey(int index){
        return sArr + fkey(index);
    }


    public static int getIndex(String str){
        int index = -1;
        if(str==null||str.length()==0) return index;
        index = str.lastIndexOf("[");
        if(index>=0){
            try{
                index = Integer.parseInt(str.substring(index+1,str.length()-1));
            }catch(Exception ex){
                index = -1;
            }
        }
        return index;
    }
    public static String getKey(String str){
        int index = -1;
        if(str==null||str.length()==0) return str;
        index = str.lastIndexOf("[");
        if(index>=0){
            return str.substring(0,index);
        }
        StringBuffer sb = null;
        return str;

    }

    public static String[] pstr(String str){
        String arr[] = new String[3];//类型,key,value
        arr[0] = str.substring(0,1);
        int i = str.indexOf(Kit.split);
//        if(i<0) return arr;
        if(Kit.cArr.equals(arr[0])){
            arr[1] = str.substring(2);
            arr[2] = Kit.array;
        }else if(Kit.cObj.equals(arr[0])){
            arr[1] = str.substring(2);
            arr[2] = Kit.object;
        }else if(Kit.cStr.equals(arr[0])){
            arr[1] = str.substring(2,i);
            arr[2] = str.substring(i+4,str.length()-1);
        }else{
            arr[1] = str.substring(2,i);
            arr[2] = str.substring(i+3,str.length());
        }
        return arr;
    }

//    public static void main(String[] args){
//        System.out.println(getIndex("[5]"));
//    }

}
