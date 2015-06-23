package cn.itcast.douban;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.douban.MyReadActivity.ViewHolder;
import cn.itcast.douban.domain.NewBook;
import cn.itcast.douban.utils.LoadImageAsyncTask;
import cn.itcast.douban.utils.LoadImageAsyncTask.LoadImageAsynTaskCallBack;
import cn.itcast.douban.utils.NetUtil;

public class NewBookActivity extends BaseActivity {
	private ListView subjectList;
	private List<NewBook> newbooks;
	private boolean isscrolling;
	private Map<String, SoftReference<Bitmap>> bitmapCaches; // 创建一个内存缓存
																// bitmap的类型是软引用的类型

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bitmapCaches = new HashMap<String, SoftReference<Bitmap>>();
	}

	@Override
	public void findView() {
		setContentView(R.layout.subject);
		subjectList = (ListView) findViewById(R.id.subjectlist);
		subjectList.setDivider(new ColorDrawable(Color.TRANSPARENT));
		subjectList.setDividerHeight(5);

		subjectList.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:
					isscrolling = true;
					break;

				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					isscrolling = true;
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					isscrolling = false;
					int startindex = subjectList.getFirstVisiblePosition();
					int count = subjectList.getChildCount();// 获取所有孩子的个数
					for (int i = 0; i < count; i++) {
						int currentpostion = startindex + i;
						final NewBook book = (NewBook) subjectList
								.getItemAtPosition(currentpostion);
						View itemview = subjectList.getChildAt(i);
						System.out.println("i=" + i);
						System.out.println("currentpostion" + currentpostion);

						final LinearLayout ll = (LinearLayout) itemview
								.findViewById(R.id.ll_book_item_image_container);
						ll.removeAllViews();
						final ImageView iv = new ImageView(
								getApplicationContext());
						if (bitmapCaches.containsKey(book.getIconpath())
								&& (bitmapCaches.get(book.getIconpath()).get() != null)) {
							iv.setImageBitmap(bitmapCaches.get(
									book.getIconpath()).get());
							System.out.println("使用内存缓存图片");
							ll.addView(iv);
						} else {

							LoadImageAsyncTask task = new LoadImageAsyncTask(
									new LoadImageAsynTaskCallBack() {

										public void onImageLoaded(Bitmap bitmap) {
											iv.setImageBitmap(bitmap);
											ll.addView(iv);
											System.out.println("新下载图片");
											// 当图片下载成功后 ,把bitmap的对象
											// 放在软引用的容器里面,然后在放在map集合里面
											bitmapCaches.put(
													book.getIconpath(),
													new SoftReference<Bitmap>(
															bitmap));
										}

										public void beforeImageLoad() {

										}
									});
							task.execute(book.getIconpath());
						}

					}

					break;
				}

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

	}

	@Override
	public void setupView() {
		new AsyncTask<Void, Void, List<NewBook>>() {

			@Override
			protected void onPreExecute() {
				showLoading();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<NewBook> result) {
				super.onPostExecute(result);
				hideLoading();
				if (result != null) {
					newbooks = result;
					subjectList.setAdapter(new NewBookAdapter());
				} else {
					Toast.makeText(getApplicationContext(), "获取新书信息失败", 0)
							.show();
				}
			}

			@Override
			protected List<NewBook> doInBackground(Void... params) {
				try {
					return NetUtil.getNewBooks();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}
		}.execute();
	}

	private class NewBookAdapter extends BaseAdapter {

		public int getCount() {
			return newbooks.size();
		}

		public Object getItem(int position) {
			return newbooks.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// 在快速滚动的界面下 不去连接服务器 下载图片,在静止状态下,才去更新图片的内容.
			View view = View.inflate(getApplicationContext(),
					R.layout.book_item, null);
			final ViewHolder holder = new ViewHolder();
			holder.ll_icon_container = (LinearLayout) view
					.findViewById(R.id.ll_book_item_image_container);
			holder.rb = (RatingBar) view.findViewById(R.id.ratingbar);
			holder.tv_title = (TextView) view.findViewById(R.id.book_title);
			holder.tv_description = (TextView) view
					.findViewById(R.id.book_description);

			holder.rb.setRating(5);
			holder.tv_title.setText(newbooks.get(position).getTitle());
			holder.tv_description.setText(newbooks.get(position)
					.getDescription());
			final ImageView iv = new ImageView(getApplicationContext());
			String iconpath = newbooks.get(position).getIconpath();
			if (bitmapCaches.containsKey(iconpath)
					&& (bitmapCaches.get(iconpath).get() != null)) {
				iv.setImageBitmap(bitmapCaches.get(iconpath).get());
				holder.ll_icon_container.addView(iv);
				System.out.println("使用内存缓存");
			} else {

				if (isscrolling) {
					iv.setImageResource(R.drawable.book);
					System.out.println("加载假的图片");
					holder.ll_icon_container.addView(iv);
				} else {
					LoadImageAsyncTask task = new LoadImageAsyncTask(
							new LoadImageAsynTaskCallBack() {

								public void onImageLoaded(Bitmap bitmap) {
									if (bitmap != null) {
										iv.setImageBitmap(bitmap);
										holder.ll_icon_container
												.removeAllViews();
										holder.ll_icon_container.addView(iv);
										System.out.println("新下载的图片");
										// 当图片下载成功后 ,把bitmap的对象
										// 放在软引用的容器里面,然后在放在map集合里面
										bitmapCaches.put(newbooks.get(position)
												.getIconpath(),
												new SoftReference<Bitmap>(
														bitmap));
									}

								}

								public void beforeImageLoad() {

								}
							});
					task.execute(newbooks.get(position).getIconpath());
				}
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
