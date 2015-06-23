package com.zxl.niubixilitysafe;

import java.util.ArrayList;
import java.util.List;

import com.zxl.niubixilitysafe.engine.TaskInfoProvider;
import com.zxl.niubixilitysafe.ui.MyTextView;
import com.zxl.niubixilitysafe.util.MemInfoUtil;
import com.zxl.niubixilitysafe.vo.TaskInfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity implements OnClickListener {
	private TextView tv_task_count;
	private TextView tv_mem;
	private TextView tv_show_user;
	private TextView tv_show_system;
	private ListView lv_user;
	private ListView lv_system;
	private LinearLayout ll_loading;
	private List<TaskInfo> usertaskinfos;
	private List<TaskInfo> systemtaskinfos;

	private ActivityManager am;
	private TaskListAdapter usertaskAdapter;
	private TaskListAdapter systemtaskAdapter;
	private int processcount = 0;
	private long availmem = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_manager);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		tv_task_count = (TextView) findViewById(R.id.tv_taskmanager_count);
		tv_mem = (TextView) findViewById(R.id.tv_taskmanager_mem);
		tv_show_user = (TextView) findViewById(R.id.tv_show_usertask);
		tv_show_system = (TextView) findViewById(R.id.tv_show_systemtask);
		lv_user = (ListView) findViewById(R.id.lv_usertask);
		lv_system = (ListView) findViewById(R.id.lv_systemtask);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_show_user.setOnClickListener(this);
		tv_show_system.setOnClickListener(this);
		processcount = MemInfoUtil.getRunningProcessCount(this);
		availmem = MemInfoUtil.getAvailMem(this);
		initText();

		// 获取正在运行的进程信息.
		fillData();

		lv_system.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskinfo = (TaskInfo) lv_system.getItemAtPosition(position);
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
				if(taskinfo.isIschecked()){
					taskinfo.setIschecked(false);
					cb.setChecked(false);
				}else{
					taskinfo.setIschecked(true);
					cb.setChecked(true);
				}
				
				
			}
		});
		lv_user.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskinfo = (TaskInfo) lv_user.getItemAtPosition(position);
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
				if(taskinfo.isIschecked()){
					taskinfo.setIschecked(false);
					cb.setChecked(false);
				}else{
					taskinfo.setIschecked(true);
					cb.setChecked(true);
				}
			}
		});
	}

	private void fillData() {
		new AsyncTask<Void, Void, List<TaskInfo>>() {

			@Override
			protected void onPreExecute() {
				ll_loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<TaskInfo> result) {
				super.onPostExecute(result);

				// 遍历集合 区分出来用户进程 和系统进程
				usertaskinfos = new ArrayList<TaskInfo>();
				systemtaskinfos = new ArrayList<TaskInfo>();
				for (TaskInfo info : result) {
					if (info.isIsusertask()) {
						usertaskinfos.add(info);
					} else {
						systemtaskinfos.add(info);
					}
				}
				// 数据准备完毕了.
				ll_loading.setVisibility(View.INVISIBLE);
				// 设置listview的数据适配器了.
				usertaskAdapter = new TaskListAdapter(usertaskinfos);
				systemtaskAdapter = new TaskListAdapter(systemtaskinfos);

				lv_system.setAdapter(systemtaskAdapter);
				lv_user.setAdapter(usertaskAdapter);

			}

			@Override
			protected List<TaskInfo> doInBackground(Void... params) {
				return TaskInfoProvider.getTaskInfos(getApplicationContext());
			}
		}.execute();

	}

	private void initText() {
	
		tv_task_count.setText("运行进程数目:"
				+ processcount + "个");
		
		tv_mem.setText("可用/总内存:"
				+ Formatter.formatFileSize(this, availmem)
				+ Formatter.formatFileSize(this, MemInfoUtil.getTotalMem()));
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_show_systemtask:
			tv_show_user.setBackgroundResource(R.color.gray);
			tv_show_system.setBackgroundResource(R.color.white);
			lv_system.setVisibility(View.VISIBLE);
			lv_user.setVisibility(View.INVISIBLE);

			break;

		case R.id.tv_show_usertask:
			tv_show_user.setBackgroundResource(R.color.white);
			tv_show_system.setBackgroundResource(R.color.gray);
			lv_system.setVisibility(View.INVISIBLE);
			lv_user.setVisibility(View.VISIBLE);
			break;
		}

	}

	private class TaskListAdapter extends BaseAdapter {
		private List<TaskInfo> taskinfos;

		public TaskListAdapter(List<TaskInfo> taskinfos) {
			this.taskinfos = taskinfos;
		}

		public void removeitem(TaskInfo taskinfo){
			this.taskinfos.remove(taskinfo);
		}
		
		public int getCount() {
			return taskinfos.size();
		}

		public Object getItem(int position) {
			return taskinfos.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.task_manager_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) view
						.findViewById(R.id.iv_taskmanger_icon);
				holder.name = (MyTextView) view
						.findViewById(R.id.tv_taskmanger_name);
				holder.mem = (TextView) view
						.findViewById(R.id.tv_taskmanger_itemmen);
				holder.cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
				view.setTag(holder);

			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			TaskInfo taskinfo = taskinfos.get(position);
			holder.icon.setImageDrawable(taskinfo.getIcon());
			holder.mem.setText(Formatter.formatFileSize(
					getApplicationContext(), taskinfo.getMemsize()));
			holder.name.setText(taskinfo.getAppname());
			holder.cb.setChecked(taskinfo.isIschecked());
			
			return view;
		}

	}

	static class ViewHolder {
		ImageView icon;
		MyTextView name;
		TextView mem;
		CheckBox cb;
	}
	//一键清理的点击事件.
	public void killTask(View view){
		//知道 当前是在系统进程 还是用户进程
		int count = 0;
		long  savedmem = 0;
		List<TaskInfo> killedTask = new ArrayList<TaskInfo>();
		
		if(lv_system.getVisibility()==View.VISIBLE){
			for(TaskInfo taskinfo : systemtaskinfos){
				if(taskinfo.isIschecked()){
					//杀死当前的进程
					am.killBackgroundProcesses(taskinfo.getPackname());
					count++;
					savedmem += taskinfo.getMemsize();
					killedTask.add(taskinfo);
				}
			}
			
		}else{
			for(TaskInfo taskinfo : usertaskinfos){
				if(taskinfo.isIschecked()){
					//杀死当前的进程
					am.killBackgroundProcesses(taskinfo.getPackname());
					count++;
					savedmem += taskinfo.getMemsize();
					killedTask.add(taskinfo);
				}
			}
			
			
		}
		for(TaskInfo task : killedTask){
			if(task.isIsusertask()){
				usertaskAdapter.removeitem(task);
			}else{
				systemtaskAdapter.removeitem(task);
			}
		}
		usertaskAdapter.notifyDataSetChanged();
		systemtaskAdapter.notifyDataSetChanged();
		Toast.makeText(this, "杀死了"+count+"个进程,释放了"+Formatter.formatFileSize(this, savedmem)+"的内存", 1).show();
		processcount-=count;
		availmem +=savedmem;
		initText();
	}
}
