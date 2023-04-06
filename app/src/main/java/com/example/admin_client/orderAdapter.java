package com.example.admin_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class orderAdapter extends RecyclerView.Adapter<orderAdapter.ViewHolder> {
    private ArrayList<String> mData = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private orderAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(orderAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date,time,name,mdName,done,qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
            //제품명
            time = (TextView) itemView.findViewById(R.id.pTime); //제품명
            name = (TextView) itemView.findViewById(R.id.pName); //제품명
            done = (TextView) itemView.findViewById(R.id.pDone); //제품명
            mdName = (TextView) itemView.findViewById(R.id.pMd); //스토어명
            qty = (TextView) itemView.findViewById(R.id.pCount); //픽업 예정일
            //픽업 예정일 안내 문구
        }
    }

    private ArrayList<orderInfo> mList = null;

    public orderAdapter(ArrayList<orderInfo> mList) {
        this.mList = mList;
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public orderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_pickup, parent, false);
        orderAdapter.ViewHolder vh = new orderAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull orderAdapter.ViewHolder holder, int position) {
        orderInfo item = mList.get(position);


        holder.time.setText(item.getTime());
        holder.name.setText(item.getName());
        holder.mdName.setText(item.getMdName());
        holder.qty.setText(item.getQty());
        holder.done.setText(item.getDone());


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
