package kadai.application.memopad;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Selection;
import android.widget.EditText;
import java.text.DateFormat;
import java.util.Date;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.view.MenuInflater;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        EditText et = (EditText) findViewById(R.id.editText1);
        SharedPreferences pref = getSharedPreferences ("MemoPrefs", MODE_PRIVATE);
        et.setText(pref.getString("memo", ""));
        et.setSelection(pref.getInt("cursor", 0));
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, menu);
        return true;
        }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EditText et = (EditText) findViewById(R.id.editText1);
		switch(item.getItemId()){
			case R.id.menu_save:
				saveMemo();
				break;
			case R.id.menu_open:
				Intent i = new Intent(this,MemoList.class);
				startActivityForResult(i,0);
				break;
			case R.id.menu_new:
				et.setText("");
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			EditText et = (EditText) findViewById(R.id.editText1);
			
			switch(requestCode){
			case 0:
				et.setText(data.getStringExtra("text"));
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStop() {
		EditText et = (EditText) findViewById(R.id.editText1);
		SharedPreferences pref = getSharedPreferences ("MemoPrefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("memo", et.getText().toString());
		editor.putInt("cursor", Selection.getSelectionStart(et.getText()));
		editor.commit();
		super.onStop();
	}
    
    void saveMemo(){
    	EditText et = (EditText)this.findViewById(R.id.editText1);
    	String title;
    	String memo = et.getText().toString();
    	
    	if(memo.trim().length()>0){
    		if(memo.indexOf("\n")==-1){
    			title = memo.substring(0, Math.min(memo.length(),20));
    		}
    		else{
    			title = memo.substring(0, Math.min(memo.indexOf("\n"),20));
    		}
    		String ts = DateFormat.getDateTimeInstance().format(new Date());
    		MemoDBHelper memos = new MemoDBHelper(this);
    		SQLiteDatabase db = memos.getWritableDatabase();
    		ContentValues values = new ContentValues();
    		values.put("title", title+"\n"+ts);
    		values.put("memo", memo);
    		db.insertOrThrow("memoDB", null,values);
    		memos.close();
    		}
    	}
    			
}
