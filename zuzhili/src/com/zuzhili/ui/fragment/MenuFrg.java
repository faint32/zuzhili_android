package com.zuzhili.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.model.menu.Menu;
import com.zuzhili.model.menu.SubMenu;
import com.zuzhili.ui.views.GroupItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liutao on 14-2-19.
 */
public class MenuFrg extends FixedOnActivityResultBugFragment {

    @ViewInject(R.id.listView)
    private ExpandableListView expandableLV;

    /** 主菜单列表 */
    private Map<Integer, Menu> menus = new HashMap<Integer, Menu>();

    /** 二级子菜单 */
    private Map<Menu, Map<Integer, SubMenu>> subMenus = new HashMap<Menu, Map<Integer, SubMenu>>();

    private List<String> spaceNames;

    private final float MENU_CHILD_HEIGHT = 38;

    public static MenuFrg newInstance() {
        MenuFrg f = new MenuFrg();
        return f;
    }

    public MenuFrg() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_expandable_layout, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO:设置adapter
        ExpandableAdapter expandableAdapter = new ExpandableAdapter();
        expandableLV.setAdapter(expandableAdapter);
        expandableLV.setGroupIndicator(null);
    }

    private void initData() {
        int[] imageResIds = new int[] {
                R.drawable.menu_home,
                R.drawable.menu_member,
                R.drawable.menu_approval,
                R.drawable.menu_todo,
                R.drawable.menu_public_space,
                R.drawable.menu_relationship,
                R.drawable.menu_project,
                R.drawable.menu_notification
        };
        String[] titles = new String[] {"首页", "成员", "内容审批", "任务", "公共空间","关系管理", "项目管理", "消息"};
        boolean[] flags = new boolean[] {false, false, false, false, true, false, false, false};
        int[] rightRegionViewStates = new int[] {0, 0, 0, 0, 2, 0, 0, 0};
        for (int i = 0; i < titles.length; i++) {
            // 初始化一级菜单列表
            Menu menu = new Menu();
            menu.setPosition(i);
            menu.setIconResId(imageResIds[i]);
            menu.setTitle(titles[i]);
            menu.setExpandable(flags[i]);
            menu.setRightRegionViewState(rightRegionViewStates[i]);
            menus.put(i, menu);

            Map<Integer, SubMenu> children = new HashMap<Integer, SubMenu>();
            if(menu.isExpandable()) {
                for (int j = 0; j < spaceNames.size(); j++) {
                    // 初始化二级菜单列表
                    SubMenu subMenu = new SubMenu();
//                    subMenu.setPublicSpaceName(spaceNames.get(j));
                    children.put(j, subMenu);
                    subMenus.put(menu, children);
                }
            }
        }

    }


    class ExpandableAdapter extends BaseExpandableListAdapter {

        public ExpandableAdapter() {
            super();
        }

        public Object getChild(int groupPosition, int childPosition) {
            return subMenus.get(menus.get(groupPosition)).get(childPosition).toString();
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return subMenus.get(menus.get(groupPosition)).size();
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = convertView;
            final int childPos = childPosition;
            if (view == null) {

                view = mLayoutInflater.inflate(R.layout.view_menu_child_item, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(activity, MENU_CHILD_HEIGHT));
                view.setLayoutParams(params);
                view.setBackgroundResource(R.drawable.child_menu_item_selector);
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO:点击跳转进入公共空间
                    }
                });
            }
            final TextView title = (TextView) view.findViewById(R.id.txt_child);
            return view;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupItemView view = (GroupItemView) convertView;
            if (view == null) {
                View container = mLayoutInflater.inflate(R.layout.view_menu_group_item, null);
                view = (GroupItemView) container.findViewById(R.id.group_item);
            }

            return view;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public Object getGroup(int groupPosition) {
            return menus.get(groupPosition);
        }

        public int getGroupCount() {
            return menus.size();
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
