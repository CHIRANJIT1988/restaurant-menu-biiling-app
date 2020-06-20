package restaurent.billing.demo.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import restaurent.billing.demo.R;
import restaurent.billing.demo.WakeLocker;
import restaurent.billing.demo.adapter.SwipeListAdapter;
import restaurent.billing.demo.app.MyApplication;
import restaurent.billing.demo.helper.Helper;
import restaurent.billing.demo.helper.Order;
import restaurent.billing.demo.sqlitedb.SQLiteDatabaseHelper;


import restaurent.billing.demo.helper.MenuItem;

import static restaurent.billing.demo.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static restaurent.billing.demo.CommonUtilities.EXTRA_MESSAGE;



public class ListViewComplainFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener
{


    private String URL_TOP_250 = "http://www.wildorchidsadventures.com/restaurent_api/ItemList.php?offset=";


    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeListAdapter adapter;
    private List<Order> orderList;

    private ProgressDialog pDialog;

    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;

    private Context context = null;

    private static final int MAX_ATTEMPTS = 5;
    private int ATTEMPTS_COUNT;


	public ListViewComplainFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));


        context = this.getActivity();


        View rootView = inflater.inflate(R.layout.fragment_complain_list_view, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);


        orderList = new ArrayList<>();
        adapter = new SwipeListAdapter(getActivity(), orderList);
        listView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(this);


        if( new SQLiteDatabaseHelper(context).dbRowCount(SQLiteDatabaseHelper.TABLE_ITEM) == 0)
        {
            initProgressDialog("Sync database.. Please wait ");
            fetchItemList();
        }


        fetchSQLIteOrders();

        return rootView;
    }


    public void onClick(View v)
    {

    }


    /**
     * This method is called when swipe refresh is pulled down
     */

    @Override
    public void onRefresh()
    {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        fetchItemList();
    }


    /**
     * Fetching complains json by making http call
     */
    private void fetchItemList()
    {

        // appending offset to url
        String url = URL_TOP_250 + offSet;


        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,

                new Response.Listener<JSONArray>()
                {

                    @Override
                    public void onResponse(JSONArray response)
                    {

                        if (response.length() > 0)
                        {

                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject itemObj = response.getJSONObject(i);

                                    int id = itemObj.getInt("id");
                                    int item_code = itemObj.getInt("item_code");
                                    String type = itemObj.getString("type");
                                    String name = itemObj.getString("name");
                                    float price = (float) itemObj.getDouble("price");


                                    MenuItem category = new MenuItem(item_code, type, name, price);

                                    new SQLiteDatabaseHelper(context).insertItem(category);


                                    // updating offset value to highest value
                                    if (id >= offSet) {
                                        offSet = id;
                                    }

                                } catch (Exception e) {

                                }
                            }
                        }


                        makeToast("Database sync successful");

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                        if(pDialog.isShowing())
                        {
                            pDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error)
            {

                try
                {

                    if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
                    {

                        fetchItemList();

                        ATTEMPTS_COUNT ++;

                        Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
                        return;
                    }

                    makeToast("Failed to sync database. Swipe to sync again");

                    // stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);

                    if(pDialog.isShowing())
                    {
                        pDialog.dismiss();
                    }
                }

                catch (Exception e)
                {

                }
            }
        });


        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }


    private  void makeToast(String msg)
    {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }


    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {

                String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);

                // Waking up mobile if it is sleeping
                WakeLocker.acquire(getActivity());


                /**
                 * Take appropriate action on this message
                 * depending upon your app requirement
                 * For now i am just displaying it on the screen
                 * */


                // Showing received message
                // lblMessage.append(newMessage + "\n");


                //if(newMessage.split("<BR>").length == 7)
                {

                    Order order = Helper.orderObject(newMessage);

                    String item_name = new SQLiteDatabaseHelper(context).getItemName(order.getItemCode());

                    order.setItemName(item_name);
                    orderList.add(0, order);
                    adapter.notifyDataSetChanged();
                }

                // Releasing wake lock
                WakeLocker.release();
            }

            catch (Exception e)
            {

            }
        }
    };


    private void initProgressDialog(String message)
    {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


    private void fetchSQLIteOrders()
    {

        orderList = new SQLiteDatabaseHelper(context).getAllOrder();

        adapter = new SwipeListAdapter(getActivity(), orderList);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            }
        });
    }
}