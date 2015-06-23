package cn.itcast.douban;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.CollectionEntry;
import com.google.gdata.data.douban.CollectionFeed;
import com.google.gdata.data.douban.Subject;
import com.google.gdata.util.ServiceException;

import cn.itcast.douban.domain.Book;
import cn.itcast.douban.utils.LoadImageAsyncTask;
import cn.itcast.douban.utils.LoadImageAsyncTask.LoadImageAsynTaskCallBack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyReadActivity extends BaseActivity {
	private ListView subjectlist;
	private BookAdapter adapter;
	private int startIndex;
	private int maxcount;
	private List<Book> books;
	
	private boolean isloading;
	private boolean nomoredata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startIndex = 1; // 从第一条数据开始获取
		maxcount = 5; // 默认一次获取5条数据
		nomoredata = false;
	}
	

	@Override
	public void findView() {
		setContentView(R.layout.subject);
		subjectlist = (ListView) findViewById(R.id.subjectlist);
		subjectlist.setDivider(new ColorDrawable(Color.TRANSPARENT));
		subjectlist.setDividerHeight(5);

		subjectlist.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if(isloading){
						return;
					}
					if(nomoredata){
						Toast.makeText(getApplicationContext(), "没有更多的数据了", 0).show();
						return ;
					}
					
					// 获取listview最后一个用户可见条目的位置
					int lastVisiblePosition = subjectlist
							.getLastVisiblePosition();
					int size = adapter.getCount();
					if ((lastVisiblePosition + 1) == size) {
						System.out.println("拖动了界面的最后一个条目,加载更多的数据");
						startIndex += maxcount;
						fillData();
					}
					break;
				}

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void setupView() {
		fillData();
		subjectlist.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Book book = (Book) subjectlist.getItemAtPosition(position);
				Intent intent = new Intent(getApplicationContext(),BookDetailActivity.class);
				MyApplication myapp = (MyApplication) getApplication();
				myapp.book =book;
				startActivity(intent);
			}
		});
	}

	private void fillData() {
		new AsyncTask<Void, Void, List<Book>>() {

			@Override
			protected List<Book> doInBackground(Void... params) {
				

					try {
						String uid = myService.getAuthorizedUser().getUid();

						CollectionFeed cf = myService.getUserCollections(uid,
								"book", null, null, startIndex, maxcount);
						System.out.println("从"+startIndex+"开始获取"+maxcount+"条数据");
						if (books == null) {
							books = new ArrayList<Book>();
						}
						if(cf.getEntries().size()==0){
							nomoredata = true;
						}
						for (CollectionEntry ce : cf.getEntries()) {
							Book book = new Book();
							Subject se = ce.getSubjectEntry();
							String author = se.getAuthors().get(0).getName();
							book.setAuthor(author);
							String title = se.getTitle().getPlainText();
							book.setTitle(title);

							for (Attribute attr : se.getAttributes()) {
								// System.out.println(attr.getName() + " : " +
								// attr.getContent());
								if ("isbn13".equals(attr.getName())) {
									String isbn = attr.getContent();
									book.setIsbn(isbn);
								} else if ("price".equals(attr.getName())) {
									String price = attr.getContent();
									book.setPrice(price);
								} else if ("publisher".equals(attr.getName())) {
									String publisher = attr.getContent();
									book.setPublisher(publisher);
								} else if ("pubdate".equals(attr.getName())) {
									String pubdate = attr.getContent();
									book.setPubdate(pubdate);
								}
							}
							String iconpath = se.getLink("image", null).getHref();
							book.setIconpath(iconpath);
							books.add(book);
							book = null;

						}
						return books;
					} catch (IOException e) {
						e.printStackTrace();
						//抛出消息
						Intent intent = new Intent();
						//弹出土司. io异常 对应一个状态码 208
					} catch (ServiceException e) {
						e.printStackTrace();
						//弹出土司                                       状态码 229
					}
					return null;
			

			}

			@Override
			protected void onPreExecute() {
				isloading = true;
				showLoading();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<Book> result) {
				hideLoading();
				// 设置数据适配器
				if (adapter == null) {
					adapter = new BookAdapter(result);
					subjectlist.setAdapter(adapter);
				} else {
					adapter.setResult(result);
					adapter.notifyDataSetChanged();
				}
				super.onPostExecute(result);
				isloading =false;
			}

		}.execute();
	}

	public class BookAdapter extends BaseAdapter {
		private List<Book> result;

		public void setResult(List<Book> result) {
			this.result = result;
		}

		public BookAdapter(List<Book> result) {
			this.result = result;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return result.size();
		}

		public Object getItem(int position) {
			return result.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			Book book = result.get(position);
			final ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.book_item, null);
				holder = new ViewHolder();
				holder.ll_icon_container = (LinearLayout) view
						.findViewById(R.id.ll_book_item_image_container);
				holder.rb = (RatingBar) view.findViewById(R.id.ratingbar);
				holder.tv_title = (TextView) view.findViewById(R.id.book_title);
				holder.tv_description = (TextView) view
						.findViewById(R.id.book_description);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.ll_icon_container.setTag(position);
			holder.ll_icon_container.removeAllViews();
			holder.rb.setRating(5);
			holder.tv_description.setText(book.getAuthor() + "/"
					+ book.getIsbn() + "/" + book.getPrice() + "/"
					+ book.getPubdate());
			holder.tv_title.setText(book.getTitle());
			String iconpath = book.getIconpath();
			final String filename = iconpath.substring(
					iconpath.lastIndexOf("/") + 1, iconpath.length());
			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			if (file.exists() && file.length() > 0) {
				// holder.iv_icon.setImageURI(Uri.fromFile(file));
				ImageView iv = new ImageView(getApplicationContext());
				iv.setScaleType(ScaleType.FIT_XY);
				iv.setImageURI(Uri.fromFile(file));
				holder.ll_icon_container.addView(iv);
			} else {
				LoadImageAsyncTask task = new LoadImageAsyncTask(// 需要让每一个异步任务 跟
																	// 他要更改的imageview
																	// 建立对应关系.
						new LoadImageAsynTaskCallBack() {
							int mypostion = position;

							public void onImageLoaded(Bitmap bitmap) {
								int ll_postion = (Integer) holder.ll_icon_container
										.getTag();
								if (mypostion != ll_postion) {// 避免已经回收掉的线程 更新
																// 不是他组件的ui;
									System.out.println("错误的对应关系");
									return;
								}

								if (bitmap != null) {
									ImageView iv = new ImageView(
											getApplicationContext());
									iv.setScaleType(ScaleType.FIT_XY);
									iv.setImageBitmap(bitmap);
									holder.ll_icon_container.removeAllViews();
									holder.ll_icon_container.addView(iv);
									try {
										File file = new File(Environment
												.getExternalStorageDirectory(),
												filename);
										FileOutputStream fos = new FileOutputStream(
												file);
										bitmap.compress(CompressFormat.JPEG,
												100, fos);
										fos.close();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

							public void beforeImageLoad() {
								ImageView iv = new ImageView(
										getApplicationContext());
								iv.setScaleType(ScaleType.FIT_XY);
								iv.setImageResource(R.drawable.book);
								holder.ll_icon_container.addView(iv);
							}
						});
				task.execute(iconpath);
			}
			return view;
		}

	}

	static class ViewHolder {
		LinearLayout ll_icon_container;
		RatingBar rb;
		TextView tv_title;
		TextView tv_description;
	}
}
