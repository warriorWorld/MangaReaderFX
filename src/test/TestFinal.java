package test;

import bean.MangaBean;

public class TestFinal {
    private static void test(final  int i, final String s, final MangaBean mangaBean){
        System.out.println(i+"\n"+s+"\n"+mangaBean.getName());
    }
    public static void main(String[] args) {
        MangaBean item=new MangaBean();
        item.setName("111");
        test(1,"111",item);
        MangaBean item1=new MangaBean();
        item1.setName("222");
        test(2,"222",item1);
    }
}
