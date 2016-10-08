package fpoly.pro205.fit.adapter;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import fpoly.pro205.fit.activities.R;
import fpoly.pro205.fit.items.itemScheduled;

public class ScheduleAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<itemScheduled> list;
	
	public ScheduleAdapter(Context context, ArrayList<itemScheduled> navDrawerItems){
		this.context = context;
		this.list = navDrawerItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_scheduled, null);
		}

		itemScheduled p = list.get(position);
		TextView slot = (TextView) convertView.findViewById(R.id.txtSlotScheduled);
		ImageView imgSun = (ImageView) convertView.findViewById(R.id.imgSun);
		ImageView imgMon = (ImageView) convertView.findViewById(R.id.imgMon);
		ImageView imgTue = (ImageView) convertView.findViewById(R.id.imgTue);
		ImageView imgWed = (ImageView) convertView.findViewById(R.id.imgWed);
		ImageView imgThu = (ImageView) convertView.findViewById(R.id.imgThu);
		ImageView imgFri = (ImageView) convertView.findViewById(R.id.imgFri);
		ImageView imgSat = (ImageView) convertView.findViewById(R.id.imgSat);

		slot.setText("Slot: "+p.getSlot());

		if(p.isSun()) imgSun.setImageResource(R.drawable.green);
		else imgSun.setImageResource(R.drawable.red);

		if(p.isMon()) imgMon.setImageResource(R.drawable.green);
		else imgMon.setImageResource(R.drawable.red);

		if(p.isTue()) imgTue.setImageResource(R.drawable.green);
		else imgTue.setImageResource(R.drawable.red);

		if(p.isWed()) imgWed.setImageResource(R.drawable.green);
		else imgWed.setImageResource(R.drawable.red);

		if(p.isThu()) imgThu.setImageResource(R.drawable.green);
		else imgThu.setImageResource(R.drawable.red);

		if(p.isFri()) imgFri.setImageResource(R.drawable.green);
		else imgFri.setImageResource(R.drawable.red);

		if(p.isSat()) imgSat.setImageResource(R.drawable.green);
		else imgSat.setImageResource(R.drawable.red);
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
