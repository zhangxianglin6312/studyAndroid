package com.zxl.niubixilitysafe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxl.niubixilitysafe.dao.DB.CommonNumberDao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity {
	private ExpandableListView elv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_number);
		elv = (ExpandableListView) findViewById(R.id.elv);
	
		elv.setAdapter(new MyListAdapter());
		
		elv.setOnChildClickListener(new OnChildClickListener() {
			
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				TextView tv = (TextView) v;
				String text = tv.getText().toString();
			    String number =	text.split("\n")[1];
			    Intent intent = new Intent();
			    intent.setAction(Intent.ACTION_DIAL);
			    intent.setData(Uri.parse("tel:"+number));
			    startActivity(intent);
				return false;
			}
		});
		
	}
	
	private class MyListAdapter extends BaseExpandableListAdapter{
		private List<String>  groupItems;//缓存所有的分组信息
		
		private Map<Integer,List<String>> childrenCache; //缓存所有的孩子信息.
		
		
		
		
		public MyListAdapter() {
			childrenCache = new HashMap<Integer, List<String>>(); //初始化 孩子的集合
		}

		//返回有多少分组信息
		public int getGroupCount() {
			
			//return CommonNumberDao.getGroupCount();
			groupItems = CommonNumberDao.getGroupItems();
			return groupItems.size(); 
		}

		//返回每一个分组有多少个孩子
		public int getChildrenCount(int groupPosition) {
			
			//return CommonNumberDao.getChildCountByGroupId(groupPosition);
			
			if(childrenCache.containsKey(groupPosition)){ //判断缓存里面是否有信息
				List<String> childrenItems =	childrenCache.get(groupPosition);
				return childrenItems.size();
			}else{
				List<String> childrenItems =	CommonNumberDao.getChildItemsByGroupId(groupPosition);
				childrenCache.put(groupPosition, childrenItems);
				return childrenItems.size();
			}
			
			
		}
		//返回分组的对象
		public Object getGroup(int groupPosition) {
			return null;
		}
		//返回某一个分组里面的某个孩子对象
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public boolean hasStableIds() {
			return false;
		}

		//返回某一个分组要显示的view对象
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv = new TextView(getApplicationContext());
			tv.setTextSize(30);
			//tv.setText("    "+CommonNumberDao.getGroupName(groupPosition));
			tv.setText("    "+groupItems.get(groupPosition));
			tv.setTextColor(Color.BLACK);
			return tv;
		}

		//返回某一个分组的孩子的view对象
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv = new TextView(getApplicationContext());
			tv.setTextSize(16);
			//tv.setText(CommonNumberDao.getChildName(groupPosition, childPosition));
			tv.setText(childrenCache.get(groupPosition).get(childPosition));
			tv.setTextColor(Color.BLACK);
			return tv;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
}
