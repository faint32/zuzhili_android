package com.zuzhili.model.menu;

/**
 * Created by liutao on 14-2-21.
 */
public class SubMenu extends SanweiMenu {
    /** 子菜单名称 */
    private String name;

    /** 与fragment绑定的tag */
    private String tag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
