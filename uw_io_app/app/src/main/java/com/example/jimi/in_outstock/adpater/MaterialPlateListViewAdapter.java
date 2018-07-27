package com.example.jimi.in_outstock.adpater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jimi.in_outstock.entity.MaterialPlateInfo;
import com.example.jimi.in_outstock.activity.R;

import java.util.List;

/**
 * ListView适配器
 */
public class MaterialPlateListViewAdapter extends ArrayAdapter<MaterialPlateInfo>{
    // Item资源文件
    private int resourceId;

    public MaterialPlateListViewAdapter(Context context, int itemViewID, List<MaterialPlateInfo> objects){
        super(context,itemViewID,objects);
        resourceId = itemViewID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MaterialPlateInfo materialPlateInfo = getItem(position);
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }else{
            view = convertView;
        }
        TextView text_materialId = (TextView) view.findViewById(R.id.text_materialId);
        TextView text_quantity = (TextView) view.findViewById(R.id.text_quantity);
        text_materialId.setText(materialPlateInfo.getMaterialId());
        text_quantity.setText(materialPlateInfo.getQuantity()+"");
        return view;
    }
}
