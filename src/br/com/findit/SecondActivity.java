package br.com.findit;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.database.sqlite.SQLiteDatabase;

public class SecondActivity extends Activity {
	 
	private SimpleCursorAdapter dataAdapter;
	 
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.activity_second);
		  displayListView();
	}
	
	private void displayListView() {
		// The desired columns to be bound
		  String[] columns = new String[] {"description","price","id_category","id_prod_cond" };
		  
		// the XML defined views which the data will be bound to
		  int[] to = new int[] { R.id.description, R.id.price,R.id.category,R.id.condition };
		  // create the adapter using the cursor pointing to the desired data 
		  //as well as the layout information
		  dataAdapter = new SimpleCursorAdapter(this, R.layout.product_layout, MainActivity.result, columns,to,0);
		  ListView listView = (ListView) findViewById(R.id.listView1);
		  // Assign adapter to ListView
		  listView.setAdapter(dataAdapter);
		  
		  
	}
	

}
