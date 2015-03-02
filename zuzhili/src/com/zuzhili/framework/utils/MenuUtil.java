package com.zuzhili.framework.utils;

import com.zuzhili.R;
import com.zuzhili.model.menu.Menu;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by zuosl on 14-2-27.
 */
public class MenuUtil {
//    private static String[] titles = new String[]{"首页", "成员", "内容审批", "任务", "公共空间", "关系管理", "项目管理", "消息", "更多......"};
//    private static String[] titles = new String[]{"首页", "成员", "投票", "更多......"};
    private static String[] titles = new String[]{"沟通", "协作", "系统"};

    public static List getMenuList() {
        List<Menu> menuBeanList = new ArrayList<Menu>();
        for (String title : titles) {
            Menu menu = new Menu();
//            menu.setTag(getTagByTitle(title));
//            if (!"更多......".equals(title)) {
//                menu.setIconResId(getImageByTitle(title));
//            }
            menu.lname = title;
            menu.setExpandable(true);
//            menu.setHaveRes(getBackRes(title));
//            if (title.equals("公共空间")) {
//                menu.setExpandable(true);
//                menu.setRightRegionViewState(Menu.RIGHT_REGION_ARROW);
//            } else {
//                menu.setExpandable(false);
//                menu.setRightRegionViewState(Menu.RIGHT_REGION_NONE);
//            }
            menuBeanList.add(menu);
        }
        return menuBeanList;
    }

    private static boolean getBackRes(String title) {
//        if ("更多......".equals(title)) {
//            return false;
//        } else {
//            return true;
//        }
        return true;
    }

    private static int getImageByTitle(String title) {
        if ("首页".equals(title)) {
            return R.drawable.menu_home;
        } else if ("成员".equals(title)) {
            return R.drawable.menu_member;
        } else if ("内容审批".equals(title)) {
            return R.drawable.menu_approval;
        } else if ("任务".equals(title)) {
            return R.drawable.menu_todo;
        } else if ("公共空间".equals(title)) {
            return R.drawable.menu_public_space;
        } else if ("关系管理".equals(title)) {
            return R.drawable.menu_relationship;
        } else if ("项目管理".equals(title)) {
            return R.drawable.menu_project;
        } else if ("消息".equals(title)) {
            return R.drawable.menu_notification;
        } else if ("投票".equals(title)) {
            return R.drawable.menu_vote;
        } else if ("更多......".equals(title)) {
            return R.drawable.menu_more;
        }
        return R.drawable.menu_home;
    }

    //标签
    private static String getTagByTitle(String title) {
        if ("首页".equals(title)) {
            return "tag.feed";
        } else if ("成员".equals(title)) {
            return "tag.member";
        } else if ("内容审批".equals(title)) {
            return "tag.approval";
        } else if ("任务".equals(title)) {
            return "tag.todo";
        } else if ("公共空间".equals(title)) {
            return "tag.public.space";
        } else if ("关系管理".equals(title)) {
            return "tag.relationship";
        } else if ("项目管理".equals(title)) {
            return "tag.project";
        } else if ("消息".equals(title)) {
            return "tag.notification";
        } else if ("更多......".equals(title)) {
            return "tag.more";
        } else if ("投票".equals(title)) {
            return "tag.vote";
        }
        return "";
    }
}
