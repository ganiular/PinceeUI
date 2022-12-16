package com.gnid.social.pincee.utils.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.SortedList;

import com.gnid.social.pinceeui.R;
import com.gnid.social.pincee.utils.helper.Info;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.murgupluoglu.flagkit.FlagKit;

import java.util.Locale;
import java.util.Set;

public class PhoneCountriesAdapter implements SpinnerAdapter {
    private static final String TAG = PhoneCountriesAdapter.class.getSimpleName();
    private final Context context;
    private final LayoutInflater inflater;
    private final SortedList<CountryData> dataSet;
    private CountryData defaultItem;

    public PhoneCountriesAdapter(Context context){
        this.context = context;
        String countryCode = Info.getCountryRegion(context);
        inflater = LayoutInflater.from(context);
        dataSet = new SortedList<>(CountryData.class, new SortedListCallback());
        Set<String> regions = PhoneNumberUtil.getInstance().getSupportedRegions();
        for(String region: regions){
            Locale locale = new Locale("en", region);
            CountryData countryData = new CountryData(region, locale.getDisplayCountry());
            if(region.equals(countryCode)){
                defaultItem = countryData;
            }
            dataSet.add(new CountryData(region, locale.getDisplayCountry()));
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, R.layout.item_country_spinner);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) { }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) { }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getDefaultItemId() {
        return dataSet.indexOf(defaultItem);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, R.layout.item_country_spinner_select);
    }

    private View createView(int position, View convertView, ViewGroup parent, int layout){
        View view;
        ImageView imageView;
        TextView textView;

        if(convertView == null){
            view = inflater.inflate(layout, parent, false);
        } else {
            view = convertView;
        }

        imageView = view.findViewById(R.id.image_view);
        textView = view.findViewById(R.id.text_view);
        CountryData dataItem = (CountryData) getItem(position);
        imageView.setImageResource(FlagKit.getResId(context, dataItem.countryRegion));
        textView.setText((CharSequence) dataItem.countryName);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return dataSet.size() == 0;
    }

    public static class CountryData{
        public String countryRegion;    // e.g NG
        public String countryName;      // e.g Nigeria
        CountryData(String code, String name){
            this.countryRegion = code;
            this.countryName = name;
        }

        @Override
        public String toString() {
            return "CountryData{" +
                    "countryCode='" + countryRegion + '\'' +
                    ", countryName='" + countryName + '\'' +
                    '}';
        }
    }

    private static class SortedListCallback extends SortedList.Callback<PhoneCountriesAdapter.CountryData> {
        private static final String TAG = PhoneCountriesAdapter.class.getSimpleName();

        @Override
        public int compare(PhoneCountriesAdapter.CountryData o1, PhoneCountriesAdapter.CountryData o2) {
            String k1 = o1.countryName;
            String k2 = o2.countryName;
            int n1 = k1.length();
            int n2 = k2.length();
            for(int i=0; i<Math.min(n1, n2); i++){
                if(k1.charAt(i) > k2.charAt(i)) return 1;
                if(k1.charAt(i) < k2.charAt(i)) return -1;
            }
            return Integer.compare(n1, n2);
        }

        @Override
        public void onChanged(int position, int count) {
            Log.i(TAG, "onChanged: pos:"+position+" count:"+count);
        }

        @Override
        public boolean areContentsTheSame(PhoneCountriesAdapter.CountryData oldItem, PhoneCountriesAdapter.CountryData newItem) {
            return oldItem.countryRegion.equals(oldItem.countryRegion);
        }

        @Override
        public boolean areItemsTheSame(PhoneCountriesAdapter.CountryData item1, PhoneCountriesAdapter.CountryData item2) {
            return item1.countryRegion == item2.countryRegion;
        }

        @Override
        public void onInserted(int position, int count) { }

        @Override
        public void onRemoved(int position, int count) {
            Log.i(TAG, "onRemoved: pos:"+position+" count:"+count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            Log.i(TAG, "onMoved: from:"+fromPosition+ " to:"+toPosition);
        }
    }
}