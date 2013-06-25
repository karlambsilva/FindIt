package br.com.findit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {
	
	EditText title;
	String titleSt;
	String databaseName = "registers";
	public static SQLiteDatabase FindItDatabase = null;
	static Cursor result = null;
	SimpleCursorAdapter dataAdapter;
	
	private static final String[] distance = {"Choose One", "within 5 kilometers", "within 10 kilometers", "within 15 kilometers", "within 20 kilometers", "within 20 or more kilometers"};
	ArrayAdapter<String> aDistance;
	Spinner sp_distance;
	
	private static final String[] category = {"Choose One","books", "dvds", "cds"};
	ArrayAdapter<String> aCategory;
	Spinner sp_category;
	
	private static final String[] price = {"Choose One","between $0.00 - 4.99", "between $5.00 - 9.99", "between $10.00 - 14.99", "between $15.00 - 19.99", "$20.00 or more"};
	ArrayAdapter<String> aPrice;
	Spinner sp_price;
	
	private static final String[] condition = {"Choose One","very bad", "bad", "OK", "good", "very good"};
	ArrayAdapter<String> aCondition;
	Spinner sp_condition;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
       title = (EditText) findViewById(R.id.et_title);
        	
        aDistance = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, distance);
        	sp_distance = (Spinner) findViewById(R.id.sp_distance);
        	sp_distance.setAdapter(aDistance);
        	
        aCategory = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, category);
        	sp_category = (Spinner) findViewById(R.id.sp_category);
        	sp_category.setAdapter(aCategory);
        	
        	
        aPrice = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, price);
        	sp_price = (Spinner) findViewById(R.id.sp_price);
        	sp_price.setAdapter(aPrice);
        	
        	
        aCondition = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, condition);
        	sp_condition = (Spinner) findViewById(R.id.sp_condition);
        	sp_condition.setAdapter(aCondition);
        	
        	                    
        // Button Search
        Button bt_search = (Button) findViewById(R.id.bt_search);
        
        //creation of the database and population
        
       createDatabase();
       populateDatabase();
        
        bt_search.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View arg0) {
        		
        		String description = title.getText().toString(); // get the string of the editText
        		int distance = sp_distance.getSelectedItemPosition();
        		int category = sp_category.getSelectedItemPosition();
        		int price = sp_price.getSelectedItemPosition();
        		int condition = sp_condition.getSelectedItemPosition();
        		
        		if((description==null)||(description.trim().equals(""))){
        			alertMessage("Search Error","The Title fild is mandatory!");
        		}else{
        		result = searchProducts(description.trim(),price,category,condition);
        		if(result.getCount()==0){
        			alertMessage("Search Error","We could not find a product with those criterias. Try to FindIt again!");
        		}else{
        		loadSecondSreen();
        		}
        		}
            	
        	}

			private void loadSecondSreen() {
							
				setContentView(R.layout.activity_second);
				 displayListView();
				 
				 Button bt_searchAgain = (Button) findViewById(R.id.bt_searchagain);
			        
				 bt_searchAgain.setOnClickListener(new View.OnClickListener(){
			        	public void onClick(View arg0) {
			        		Intent mainscreen = new Intent(MainActivity.this,MainActivity.class);
			        		MainActivity.this.startActivity(mainscreen);
			        		MainActivity.this.finish();
			      
			        	}
			        });
				 	
			}
        });        
        
  
        Button bt_cancel = (Button) findViewById(R.id.bt_cancel);
        
        bt_cancel.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View arg0) {
        		System.exit(0);
        	}
        }); 
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
       
    }
    
    public void displayListView() {
		// The desired columns to be bound
		  String[] columns = new String[] {"description","price","cat_desc","prod_cond_desc" };
		  
		// the XML defined views which the data will be bound to
		  int[] to = new int[] { R.id.description, R.id.price,R.id.category,R.id.condition };
		  // create the adapter using the cursor pointing to the desired data 
		  //as well as the layout information
		  dataAdapter = new SimpleCursorAdapter(this, R.layout.product_layout, result, columns,to,0);
		  ListView listView = (ListView) findViewById(R.id.listView1);
		  // Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		  
		  
	}
    
    public void createDatabase(){
    	try{
    		
    		FindItDatabase = openOrCreateDatabase(databaseName, MODE_WORLD_WRITEABLE, null);
    		DataBase.createDatabase();
    		  		
     	}catch(Exception error){
     		alertMessage("DataBase Error","The DataBase was not created: "+ error);
     	}
    }
    
    private void populateDatabase() {
    	
    	FindItDatabase = openOrCreateDatabase(databaseName, MODE_WORLD_WRITEABLE, null);
    	DataBase.populateDatabase();
    }


    public void alertMessage(String title, String message){
    	AlertDialog.Builder Message = new AlertDialog.Builder(MainActivity.this);
    	Message.setTitle(title);
    	Message.setMessage(message);
    	Message.setNeutralButton("OK",null);
    	Message.show();
    }
    
    public Cursor searchProducts(String title, int price,int category, int condition){
    	Cursor resultQuery = null;
    	FindItDatabase = openOrCreateDatabase(databaseName, MODE_WORLD_WRITEABLE, null);
        //search title
    	if((title!=null)&&(price==0)&&(category==0)&&(condition==0)){
    		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond ORDER BY p.description",null);
    		}
     	//search title and price = 1 - between $0.00 - 4.99
     	if((title!=null)&&(price==1)&&(category==0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.price between 0.0 AND 4.99 ORDER BY p.description, p.price",null);
     	 }
     	//search title and price = 2 - between $5.00 - 9.99
     	if((title!=null)&&(price==2)&&(category==0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.price between 5.00 AND 9.99 ORDER BY p.description, p.price",null);
     	 }
     	//search title and price = 3 - between $10.00 - 14.99
     	if((title!=null)&&(price==3)&&(category==0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.price between 10.00 AND 14.99 ORDER BY p.description, p.price",null);
     	 }
     	//search title and price = 4 - between $15.00 - 19.99
     	if((title!=null)&&(price==4)&&(category==0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.price between 15.00 AND 19.99 ORDER BY p.description, p.price",null);
     	 }
     	//search title and price = 5 - $20.00 or more
     	if((title!=null)&&(price==5)&&(category==0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.price >= 20.00 ORDER BY p.description, p.price",null);
     	 }
     	//search title and category = 1 - books
     	if((title!=null)&&(price==0)&&(category==1)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.id_category = 1 ORDER BY p.description, c.cat_desc",null);
     	 }
     	//search title and category = 2 - dvds
     	if((title!=null)&&(price==0)&&(category==2)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.id_category = 2 ORDER BY p.description, c.cat_desc",null);
     	 }
     	//search title and category = 3 - cds
     	if((title!=null)&&(price==0)&&(category==3)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.id_category = 3 ORDER BY p.description, c.cat_desc",null);
     	 }
     	//search title and condition = 1 - very bad
     	if((title!=null)&&(price==0)&&(category==0)&&(condition==1)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.id_prod_cond = 1 ORDER BY p.description, pc.prod_cond_desc",null);
     	}
     	//search title and condition = 2 - bad
     	if((title!=null)&&(price==0)&&(category==0)&&(condition==2)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.id_prod_cond= 2 ORDER BY p.description, pc.prod_cond_desc",null);
     	}
     	//search title and condition = 3 - OK
     	if((title!=null)&&(price==0)&&(category==0)&&(condition==3)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.id_prod_cond = 3 ORDER BY p.description, pc.prod_cond_desc",null);
     	}
     	//search title and condition = 4 - good
     	if((title!=null)&&(price==0)&&(category==0)&&(condition==4)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.id_prod_cond = 4 ORDER BY p.description, pc.prod_cond_desc",null);
     	}
     	//search title and condition = 5 - very good
     	if((title!=null)&&(price==0)&&(category==0)&&(condition==5)){
     		resultQuery = FindItDatabase.rawQuery("SELECT p._id, p.description, p.price, c.cat_desc, pc.prod_cond_desc FROM tabProduct p, tabCategory c, tabProduct_condition pc WHERE description LIKE '"+title+"%' AND c.id_category = p.id_category AND p.id_prod_cond = pc.id_prod_cond AND p.id_prod_cond = 5 ORDER BY p.description, pc.prod_cond_desc",null);
     	}
     	//search title, order distance,price
     	/*if((title!=null)&&(price!=0)&&(category==0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY "+distance+","+price, null);
     	}
     	  //search title,distance,category
     	if((title!=null)&&(price==0)&&(category!=0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY distance, catg", null);
        }
     	  //search title,distance,condition
     	if((title!=null)&&(price==0)&&(category==0)&&(condition!=0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY "+ distance +","+ condition, null);
     	}   
     	  //search title,price,category
     	if((title!=null)&&(price!=0)&&(category!=0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY price, catg", null);
     	}   
     	  //search title,price,condition
     	if((title!=null)&&(price!=0)&&(category==0)&&(condition!=0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY "+ price +","+ condition, null);
     	} 
     	  //search title,categoty,condition
     	if((title!=null)&&(price==0)&&(category!=0)&&(condition!=0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY atg, condition", null);
     	}  
     	  //search title,distance,price,category
     	if((title!=null)&&(price!=0)&&(category!=0)&&(condition==0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY distance, catg, price", null);
     	}     
     	  //search title,distance,price,condition
     	if((title!=null)&&(price!=0)&&(category==0)&&(condition!=0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY "+ distance +","+ price +","+ condition, null);
     	}     
     	  //search title,distance,category,condition
     	if((title!=null)&&(price==0)&&(category!=0)&&(condition!=0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY distance, catg, condition", null);
     	}   
     	  //search title,price,category,condition
     	if((title!=null)&&(price!=0)&&(category!=0)&&(condition!=0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY catg, price, condition", null);
     	}     
     	  //search title,price,distance,condition
     	if((title!=null)&&(price!=0)&&(category==0)&&(condition!=0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description="+title+"ORDER BY "+ distance +","+ price +","+ condition, null);
     	}   
     	//search title,distance,price,category,condition
     	if((title!=null)&&(price!=0)&&(category!=0)&&(condition!=0)){
     		resultQuery = FindItDatabase.rawQuery("SELECT * FROM tabProduct WHERE description = '"+title+"'"+"ORDER BY description, id_category, price, id_prod_cond", null);
     	}*/
     	return resultQuery;     	
    } 

    
   

}
