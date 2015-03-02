package com.zuzhili.model.menu;

/**
 * Created by liutao on 14-2-21.
 */
public class Menu extends SanweiMenu {
    /** 右侧无图标 */
    public static int RIGHT_REGION_NONE = 0;

    /** 右侧有一个提示消息数目的badgeView*/
    public static int RIGHT_REGION_BADGEVIEW = 1;

    /** 右侧有一个提示menu可展开的arrow */
    public static int RIGHT_REGION_ARROW = 2;

    /** 抽屉菜单图片资源id */
    private int iconResId;

    /** 菜单项标题 */
    private String title;

    /** 菜单项在列表中的位置 */
    private int position;

    /** 菜单项是否可展开为二级子菜单 */
    private boolean isExpandable;

    /** 右侧显示状态（右侧有三种显示状态，"0"none; "1",badgeView; "2", arrow）*/
    private int rightRegionViewState;

    /** 与fragment绑定的tag */
    private String tag;

    /** 是否含有资源文件 */
    private boolean isHaveRes;

    public boolean isHaveRes() {
        return isHaveRes;
    }

    public void setHaveRes(boolean isHaveRes) {
        this.isHaveRes = isHaveRes;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean isExpandable) {
        this.isExpandable = isExpandable;
    }

    public int getRightRegionViewState() {
        return rightRegionViewState;
    }

    public void setRightRegionViewState(int rightRegionViewState) {
        this.rightRegionViewState = rightRegionViewState;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
