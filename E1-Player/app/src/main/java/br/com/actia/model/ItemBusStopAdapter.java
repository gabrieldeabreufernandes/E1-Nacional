package br.com.actia.model;

import java.util.ArrayList;

import br.com.actia.e1player.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemBusStopAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ItemBusStopView> listItens;

    public ItemBusStopAdapter(Context context, ArrayList<ItemBusStopView> listItens) {
        this.listItens = listItens;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listItens.size();
    }

    @Override
    public Object getItem(int position) {
        return listItens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemBusStopView item = listItens.get(position);

        view = inflater.inflate(R.layout.item_listview, null);

        ((TextView) view.findViewById(R.id.text)).setText(item.getBusStop());
        ((ImageView) view.findViewById(R.id.imageView)).setImageResource(item.getIconRid());

        return view;
    }
}
