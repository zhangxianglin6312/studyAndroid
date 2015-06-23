package com.zxl.niubixilitysafe;

import java.util.List;

import com.zxl.niubixilitysafe.dao.DB.BlackNumberDao;
import com.zxl.niubixilitysafe.vo.BlackNumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity implements OnClickListener {
	private static final String TAG = "CallSmsSafeActivity";
	private ListView lv_call_sms_safe;
	private ImageView iv_call_logo_add;
	private ImageView iv_callsms_hint;
	private BlackNumberDao dao;
	private AlertDialog dialog;
	private EditText et_add_black_number;
	private CheckBox cb_block_phone, cb_block_sms;
	private Button bt_add_ok, bt_add_cancle;
	private Button bt_change_ok, bt_change_cancle;
	private EditText et_change_black_number;

	private List<BlackNumber> blacknumbers;

	private CallSmsAdapter adapter;
	private String number ;
	
	private int changedpositon;
	private int startindex = 0;//开始获取数据的位置
	private int max = 20;     //一次最多获取多个条的数据
	private int totalcount;  //数据库里面一共有多少个条目

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_sms_safe);
		lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
		iv_call_logo_add = (ImageView) findViewById(R.id.iv_call_logo_add);
		iv_callsms_hint = (ImageView) findViewById(R.id.iv_callsms_hint);
		
		// 判断 用户是否已经添加过黑名单号码 如果没有添加过 就显示提示的图片
		dao = new BlackNumberDao(this);
		blacknumbers = dao.findAll(startindex, max);
		
		totalcount = dao.getTotalCount();
		
		if (blacknumbers.size() == 0) {
			iv_callsms_hint.setVisibility(View.VISIBLE);
		} else {
			iv_callsms_hint.setVisibility(View.INVISIBLE);
		}
		adapter = new CallSmsAdapter();
		lv_call_sms_safe.setAdapter(adapter);

		//判断用户是否把listview拖动到了界面的最下方.
		lv_call_sms_safe.setOnScrollListener(new OnScrollListener() {
			
			/**
			 * 当listview的滚动状态发生改变的时候 调用
			 */
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					//判断 , listview里面最后一个用户可见的条目 
					//是否是 listview数据适配器里面最后一个条目
					int position = lv_call_sms_safe.getLastVisiblePosition();// 位置是从0开始的
					// 数据适配器里面有多个条目  1开始的
					int  maxitem = blacknumbers.size();
					if((position+1)==maxitem){
						Log.i(TAG,"用户把界面拖动到了最下面");
						Toast.makeText(getApplicationContext(), "加载更多数据", 0).show();
						fillMoreData();
					}
					break;
				}
				
			}

			/**
			 * 当listview在滚动的时候 调用的方法.
			 */
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		
		
		
		iv_call_logo_add.setOnClickListener(this);

		// 1 .注册一个上下文菜单
		registerForContextMenu(lv_call_sms_safe);


		Intent intent = getIntent();
		number = intent.getStringExtra("number");
		if(!TextUtils.isEmpty(number)){
			showAddDialog();
		}
		
	}

	/**
	 * 添加更多的数据
	 */
	private void fillMoreData() {
		startindex+=max;
		if(startindex>totalcount){
			Toast.makeText(this, "没有更多的数据了", 0).show();
			return ;
		}
		
		List<BlackNumber>  moreNumbers = dao.findAll(startindex, max);
		for(BlackNumber morenumber : moreNumbers){
			blacknumbers.add(morenumber);
		}
		adapter.notifyDataSetChanged();
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.call_sms_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		BlackNumber blacknumber = (BlackNumber) lv_call_sms_safe
				.getItemAtPosition((int) info.id);
		switch (item.getItemId()) {
		case R.id.item_delete:
			Log.i(TAG,
					"删除" + info.id + "位置的黑名单号码" + " :"
							+ blacknumber.getNumber());
			deleteBlackNumber(blacknumber);

			return true;
		case R.id.item_update:
			Log.i(TAG,
					"修改" + info.id + "位置的黑名单号码" + " :"
							+ blacknumber.getNumber());
			changedpositon = (int) info.id;
			updateBlackNumber(blacknumber);

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * 修改一条黑名单的号码
	 * 
	 * @param blacknumber
	 */
	private void updateBlackNumber(BlackNumber blacknumber) {
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		View view = View
				.inflate(this, R.layout.change_blacknumber_dialog, null);
		bt_change_ok = (Button) view.findViewById(R.id.bt_change_ok);
		bt_change_cancle = (Button) view.findViewById(R.id.bt_change_cancle);
		// 把要修改的电话号码的对象 设置给了 bt_ok的按钮.
		bt_change_ok.setTag(blacknumber);

		et_change_black_number = (EditText) view
				.findViewById(R.id.et_change_black_number);
		// 完成修改数据时候的回显.
		et_change_black_number.setText(blacknumber.getNumber());
		String mode = blacknumber.getMode();

		cb_block_phone = (CheckBox) view.findViewById(R.id.cb_block_phone);
		cb_block_sms = (CheckBox) view.findViewById(R.id.cb_block_sms);

		bt_change_cancle.setOnClickListener(this);
		bt_change_ok.setOnClickListener(this);
		if ("2".equals(mode)) {
			cb_block_phone.setChecked(true);
			cb_block_sms.setChecked(true);
		} else if ("1".equals(mode)) {
			cb_block_sms.setChecked(true);
		} else if ("0".equals(mode)) {
			cb_block_phone.setChecked(true);
		}
		dialog.setView(view, 0, 0, 0, 0);

		dialog.show();
	}

	/**
	 * 删除一条黑名单号码
	 * 
	 * @param blacknumber
	 */
	private void deleteBlackNumber(BlackNumber blacknumber) {
		boolean result = dao.delete(blacknumber.getNumber());
		if (result) {
			Toast.makeText(this, "删除成功", 0).show();
		} else {
			Toast.makeText(this, "删除失败", 0).show();
		}
		blacknumbers.remove(blacknumber);
		adapter.notifyDataSetChanged();// 通知数据适配器更新里面的内容
		// 不要重新 重新设置数据适配器. 会导致界面回到最顶端.
		// lv_call_sms_safe.setAdapter(adapter);
	}

	/**
	 * 数据适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class CallSmsAdapter extends BaseAdapter {

		public int getCount() {
			return blacknumbers.size();
		}

		public Object getItem(int position) {
			return blacknumbers.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// Log.i(TAG,"GETVIEW"+position);
			// 判断历史缓存的view对象是否为空
			// 如果不为空 并且类型合适 就可以服用这个历史缓存的view对象.
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				Log.i(TAG,"使用缓存view对象");
				holder = (ViewHolder) view.getTag(); //使用缓存view对象里面的 控件id的引用. 目的:减少查找控件的次数.
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.call_sms_item, null);
				Log.i(TAG,"创建新的view对象");
				holder = new ViewHolder();
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_call_sms_mode);
				holder.tv_number = (TextView) view
						.findViewById(R.id.tv_call_sms_number);
				view.setTag(holder);  //在创建新布局的时候  获取到布局里面控件的引用,存放到当前的view对象里面
			}
			
			BlackNumber blacknumber = blacknumbers.get(position);
			holder.tv_number.setText(blacknumber.getNumber());
			String mode = blacknumber.getMode();
			if ("2".equals(mode)) {
				holder.tv_mode.setText("全部拦截");
			} else if ("1".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else if ("0".equals(mode)) {
				holder.tv_mode.setText("电话拦截");
			}
			return view;
		}

	}
	
	
	static class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_call_logo_add:
			// TODO: 添加一条黑名单号码
			showAddDialog();
			break;

		case R.id.bt_change_cancle:
			dialog.dismiss();
			break;
		case R.id.bt_change_ok:
			String changednumber = et_change_black_number.getText().toString()
					.trim();
			if (TextUtils.isEmpty(changednumber)) {
				Toast.makeText(this, "黑名单号码不能为空", 0).show();
				return;
			} else {
				// TODO: 判断模式是否相同 更改模式.
				if (dao.find(changednumber)) {
					Toast.makeText(this, "要更改的号码已经存在", 0).show();
				} else {
					String mode = "0";
					if (cb_block_phone.isChecked() && cb_block_sms.isChecked()) {
						mode = "2";
					} else if (cb_block_phone.isChecked()) {
						mode = "0";
					} else if (cb_block_sms.isChecked()) {
						mode = "1";
					}
					BlackNumber blacknumber = (BlackNumber) bt_change_ok
							.getTag();

					dao.update(blacknumber.getNumber(), changednumber, mode);

					// 更新界面里面的内容.
					// 更新界面对应的数据适配器里面的数据
					blacknumbers.remove(blacknumber); // 把修改的blacknumber的对象从条目里面移除.
					BlackNumber newblacknumber = new BlackNumber();
					newblacknumber.setMode(mode);
					newblacknumber.setNumber(changednumber);
					// 在指定的位置插入新的条目
					blacknumbers.add(changedpositon, newblacknumber);
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}
			}

			break;
		case R.id.bt_add_cancle:
			dialog.dismiss();
			break;
		case R.id.bt_add_ok:
			String number = et_add_black_number.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {
				Toast.makeText(this, "黑名单号码不能为空", 0).show();
				return;
			} else {
				if (dao.find(number)) {
					Toast.makeText(this, "要添加的号码已经存在", 0).show();
				} else {
					BlackNumber addedBlacknumber = new BlackNumber();
					if (cb_block_phone.isChecked() && cb_block_sms.isChecked()) {
						dao.save(number, "2");
						addedBlacknumber.setMode("2");
					} else if (cb_block_phone.isChecked()) {
						dao.save(number, "0");
						addedBlacknumber.setMode("0");
					} else if (cb_block_sms.isChecked()) {
						dao.save(number, "1");
						addedBlacknumber.setMode("1");
					} else {
						Toast.makeText(this, "请设置拦截模式", 0).show();
						return;
					}
					// 如果添加了号码,需要通知listview的数据适配器更新了内容.
					// 在通知改变之前 需要更新listview要显示的数据集合的内容
					addedBlacknumber.setNumber(number);
					blacknumbers.add(addedBlacknumber);
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}

			}

			break;
		}

	}

	/**
	 * 显示一个添加的对话框
	 */
	private void showAddDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.add_blacknumber_dialog, null);
		et_add_black_number = (EditText) view
				.findViewById(R.id.et_add_black_number);
		if(!TextUtils.isEmpty(number)){
			et_add_black_number.setText(number);
		}
		bt_add_cancle = (Button) view.findViewById(R.id.bt_add_cancle);
		bt_add_ok = (Button) view.findViewById(R.id.bt_add_ok);
		cb_block_phone = (CheckBox) view.findViewById(R.id.cb_block_phone);
		cb_block_sms = (CheckBox) view.findViewById(R.id.cb_block_sms);
		bt_add_cancle.setOnClickListener(this);
		bt_add_ok.setOnClickListener(this);
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
	
	/**
	 * 当当前activity不存在的时候 会激活当前activity 并且执行oncreate方法
	 * 如果 activity已经在任务栈的栈顶存在
	 * OnNewIntent();
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		number = intent.getStringExtra("number");
		showAddDialog();
		super.onNewIntent(intent);
	}
}
