package cn.itcast.douban;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.google.gdata.data.douban.NoteEntry;
import com.google.gdata.data.douban.NoteFeed;
import com.google.gdata.util.ServiceException;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyNoteActivity extends BaseActivity {
	private ListView notelistview;
	private int startindex;
	private int maxresult;
	private TextView tv_current_page;
	private List<NoteEntry> noteEntries;
	private NoteAdapter adapter;
	private EditText et_page_number;
	private boolean isloading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startindex = 1;
		maxresult = 10;
	}

	@Override
	public void findView() {
		setContentView(R.layout.my_note);
		notelistview = (ListView) findViewById(R.id.notelistview);
		tv_current_page = (TextView) findViewById(R.id.tv_current_page);
		et_page_number = (EditText) findViewById(R.id.et_page_number);
	}

	@Override
	public void setupView() {
		notelistview.setDivider(new ColorDrawable(Color.TRANSPARENT));
		notelistview.setDividerHeight(5);
		// 注册上下文菜单
		registerForContextMenu(notelistview);

		fillData();

		notelistview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NoteEntry ne = (NoteEntry) notelistview
						.getItemAtPosition(position);
				MyApplication myapp = (MyApplication) getApplication();
				myapp.ne = ne;
				Intent intent = new Intent(getApplicationContext(),
						NoteDetailActiviy.class);
				startActivity(intent);

			}
		});

	}

	private void fillData() {
		if (isloading)
			return;
		new AsyncTask<Void, Void, NoteFeed>() {

			@Override
			protected void onPreExecute() {
				isloading = true;
				showLoading();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(NoteFeed result) {
				if (result != null) {
					noteEntries = result.getEntries();
					
					adapter = new NoteAdapter();
					notelistview.setAdapter(adapter);
					tv_current_page.setText(getPageNumber());
					isloading = false;
				}else{
					Toast.makeText(getApplicationContext(), "获取数据失败", 0).show();
				}
				hideLoading();
				super.onPostExecute(result);

			}

			@Override
			protected NoteFeed doInBackground(Void... params) {
				try {
					String uid = myService.getAuthorizedUser().getUid();
					NoteFeed nf = myService.getUserNotes(uid, startindex,
							maxresult);
					return nf;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}

		}.execute();
	}

	private class NoteAdapter extends BaseAdapter {

		public void removeNoteEntry(NoteEntry ne) {
			noteEntries.remove(ne);
		}

		public int getCount() {
			return noteEntries.size();
		}

		public Object getItem(int position) {
			return noteEntries.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(), R.layout.fav_item,
						null);
				holder = new ViewHolder();
				holder.note_title = (TextView) view
						.findViewById(R.id.fav_title);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.note_title.setText(noteEntries.get(position).getTitle()
					.getPlainText());

			return view;
		}

	}

	static class ViewHolder {
		TextView note_title;
	}

	private String getPageNumber() {
		int page = startindex / maxresult + 1;
		return "当前页码:" + page;
	}

	public void prePage(View view) {
		startindex -= maxresult;
		if (startindex < 1) {
			startindex = 1;
			Toast.makeText(this, "已经是第一页了", 0).show();
			return;
		}
		fillData();

	}

	public void nextPage(View view) {
		startindex += maxresult;
		fillData();
	}

	public void jump(View view) {
		String number = et_page_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "页码不能为空", 0).show();
			return;
		}
		int page = Integer.parseInt(number);
		if (page <= 0) {
			Toast.makeText(this, "页码不合法", 0).show();
			return;
		}
		startindex = (page - 1) * maxresult + 1;
		fillData();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = (int) info.id;
		NoteEntry ne = (NoteEntry) notelistview.getItemAtPosition(position);
		switch (item.getItemId()) {
		case R.id.item_addnote:
			addNote(ne);
			return true;
		case R.id.item_delete:
			deleteNote(ne);
			return true;
		case R.id.item_update:
			updateNote(ne);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * 添加一条指定的日记
	 * 
	 * @param ne
	 */

	private void addNote(NoteEntry ne) {
		Intent intent = new Intent(this, NoteEditActivity.class);
		intent.putExtra("type", "add");
		// startActivity(intent);
		startActivityForResult(intent, 0);
	}

	/**
	 * 更新一条指定的日记
	 * 
	 * @param ne
	 */
	private void updateNote(NoteEntry ne) {
		Intent intent = new Intent(this, NoteEditActivity.class);
		intent.putExtra("type", "update");
		MyApplication myapp = (MyApplication) getApplication();
		myapp.ne = ne;
		// startActivity(intent);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			boolean result = data.getBooleanExtra("result", false);
			if (result) {
				fillData();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 删除一条指定的日记
	 * 
	 * @param ne
	 */
	private void deleteNote(final NoteEntry ne) {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoading();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				hideLoading();
				/*
				 * if(result){ fillData(); }
				 */
				if (result) {
					adapter.removeNoteEntry(ne);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					myService.deleteNote(ne);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			}
		}.execute();
	}
}
