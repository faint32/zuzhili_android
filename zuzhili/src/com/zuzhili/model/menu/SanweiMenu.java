package com.zuzhili.model.menu;

/**
 * Created by liutao on 1/2/15.
 */
public class SanweiMenu implements Comparable<SanweiMenu> {
    public String lname;    // 栏目名称
    public int llevel;   // 栏目级别
    public int id;       // 栏目id
    public int lparentid;    // 父栏目id
    public String searchurl;    // 搜索链接（有的栏目有，有的栏目没有）
    public String licon;    // 栏目图标链接
    public String lurl;     // 单击栏目，进入HTML5页面的链接

    @Override
    public int compareTo(SanweiMenu another) {
        if (this.llevel == another.llevel) {
            return this.id - another.id;
        }
        return this.llevel - another.llevel;
    }
}
