package cn.itcast.douban;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.NoteEntry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NoteDetailActiviy extends Activity {
	private TextView tv_title;
	private TextView tv_content;
	private TextView tv_date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_view);
		tv_content = (TextView) findViewById(R.id.content);
		tv_title = (TextView) findViewById(R.id.title);
		tv_date =(TextView)findViewById(R.id.date);
		MyApplication myapp = (MyApplication) getApplication();
		NoteEntry ne = myapp.ne;

		if (ne.getContent() != null) {
			
		String content =	((TextContent) ne.getContent()).getContent().getPlainText();
		tv_content.setText(content);
		}
		
		String title = ne.getTitle().getPlainText();
		tv_title.setText(title);
		DateTime dateTime = ne.getPublished();
		long date = dateTime.getValue();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
	
		
		tv_date.setText(df.format(new Date(date)));
	}

}
