package restaurent.billing.demo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import restaurent.billing.demo.R;
import restaurent.billing.demo.helper.DateTimeParsing;
import restaurent.billing.demo.helper.Helper;
import restaurent.billing.demo.helper.Order;


public class SwipeListAdapter extends BaseAdapter
{

    private Activity activity;
    private LayoutInflater inflater;
    private List<Order> orderList;

    private String[] bgColors;


    public SwipeListAdapter(Activity activity, List<Order> orderList)
    {
        this.activity = activity;
        this.orderList = orderList;
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.complain_serial_bg);
    }


    @Override
    public int getCount()
    {
        return orderList.size();
    }


    @Override
    public Object getItem(int location) {
        return orderList.get(location);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        if (inflater == null)
        {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_row, null);
        }


        TextView table = (TextView) convertView.findViewById(R.id.table);
        TextView item = (TextView) convertView.findViewById(R.id.item);
        TextView waiter = (TextView) convertView.findViewById(R.id.waiter);
        TextView quantity = (TextView) convertView.findViewById(R.id.quantity);
        TextView date = (TextView) convertView.findViewById(R.id.date);


        Order order = orderList.get(position);


        table.setText("T"+ order.getTableNo());
        item.setText("" + Helper.toCamelCase(order.getItemName()));
        waiter.setText("Waiter Name : " + Helper.toCamelCase(order.getWaiterName()));
        quantity.setText("Quantity : " + order.getQuantity());
        date.setText(DateTimeParsing.dateTimeFormat(order.getDatetime()));


        ShapeDrawable background = new ShapeDrawable();
        background.setShape(new OvalShape()); // or RoundRectShape()

        String color = bgColors[position % bgColors.length];

        background.getPaint().setColor(Color.parseColor(color));

        table.setBackground(background);

        return convertView;
    }
}