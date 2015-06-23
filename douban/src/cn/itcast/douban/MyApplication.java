package cn.itcast.douban;

import com.google.gdata.data.douban.NoteEntry;

import cn.itcast.douban.domain.Book;
import android.app.Application;

public class MyApplication extends Application {

	/**
	 * 在清单文件里面配置后 ,MyApplication 代表的就是当前的应用程序 
	 * 所有的系统组件都是运行在 MyApplication里面的.
	 */
 public Book book;
 public NoteEntry ne;
}
