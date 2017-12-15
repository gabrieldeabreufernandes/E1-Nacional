package br.com.actia.model;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.actia.e1player.R;

public class ItemBusStopAdapter2 extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    List<GpsPoint> lstGpsPoint = null;

    /*View view = null;
    ImageView imageView = null;
    TextView  textView = null;

    Image imgStartRoute = null;
    Image imgRoute = null;
    Image imgEndRoute = null;*/

    public ItemBusStopAdapter2(Context context, List<GpsPoint> lstGpsPoint) {
        this.lstGpsPoint = lstGpsPoint;
        inflater = LayoutInflater.from(context);


        /*view = inflater.inflate(R.layout.item_listview, null);
        textView = (TextView) view.findViewById(R.id.text);
        imageView = (ImageView) view.findViewById(R.id.imageView);*/
    }

    @Override
    public int getGroupCount() {
        return lstGpsPoint.size();
    }

    @Override
    public int getChildrenCount(int i) {
        int childCount = 0;

        List<Poi> lstPoi = lstGpsPoint.get(i).getPois();
        if(lstPoi != null)
            childCount = lstPoi.size();

        return childCount;
    }

    @Override
    public Object getGroup(int i) {
        return lstGpsPoint.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return lstGpsPoint.get(i).getPois().get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2; //alterar
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        int imageId;
        GpsPoint gpsPoint = lstGpsPoint.get(i);

        view = inflater.inflate(R.layout.item_listview, null);
        ((TextView) view.findViewById(R.id.text)).setText(gpsPoint.getName());

        if(i == 0) {
            //imageId = R.drawable.marker_blue;
            AnimationDrawable rocketAnimation;
            ImageView rocketImage = (ImageView)  view.findViewById(R.id.imageView);
            rocketImage.setBackgroundResource(R.drawable.marker_anim);
            rocketAnimation = (AnimationDrawable) rocketImage.getBackground();
            rocketAnimation.start();
        }
        else if(i >= lstGpsPoint.size() -1) {
            imageId = R.drawable.marker_end;
            ((ImageView) view.findViewById(R.id.imageView)).setImageResource(imageId);
        }
        else {
            imageId = R.drawable.marker_route;
            ((ImageView) view.findViewById(R.id.imageView)).setImageResource(imageId);
        }

        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        List<Poi> lstPoi = lstGpsPoint.get(i).getPois();

        view = inflater.inflate(R.layout.item_listview2, null);

        if(lstPoi != null) {
            ((TextView) view.findViewById(R.id.point1)).setText(lstPoi.get(i2).getName());
            ((ImageView) view.findViewById(R.id.poiView)).setImageResource(getPoiImage(lstPoi.get(i2).getType()));
            ((ImageView) view.findViewById(R.id.lineRoute)).setImageResource(R.drawable.marker_line);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    private int getPoiImage(int type) {
        int poiImageReturn;

        switch(type) {
            case Poi.POI_TYPE_SCHOOL:
                poiImageReturn = R.drawable.school;
                break;
            case Poi.POI_TYPE_HOSPITAL:
                poiImageReturn = R.drawable.hospital;
                break;
            case Poi.POI_TYPE_FIREMAN:
                poiImageReturn = R.drawable.fireguard;
                break;
            case Poi.POI_TYPE_POLICE:
                poiImageReturn = R.drawable.police;
                break;
            case Poi.POI_TYPE_MONUMENT:
                poiImageReturn = R.drawable.town;
                break;
            case Poi.POI_TYPE_SHOP:
                poiImageReturn = R.drawable.shop;
                break;
            default:
                poiImageReturn = R.drawable.marker;
                break;
        }
        return poiImageReturn;
    }
}
