package fpoly.pro205.fit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.items.itemBook;

public class BooksAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<itemBook> list;

	public BooksAdapter(Context context, ArrayList<itemBook> navDrawerItems){
		this.context = context;
		this.list = navDrawerItems;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_book, null);
		}

		itemBook p = list.get(position);

		TextView tt1 = (TextView) convertView.findViewById(R.id.txtBookingDate);
		TextView tt2 = (TextView) convertView.findViewById(R.id.txtSlot);
		TextView tt3 = (TextView) convertView.findViewById(R.id.txtStatus);
		if(p.getDate()!=null)
		tt1.setText(p.getDate());
		tt2.setText("Slot: "+String.valueOf(p.getSlot()));
		tt3.setText("Status: "+String.valueOf(p.getStatus()));
		return convertView;
	}

	
}
