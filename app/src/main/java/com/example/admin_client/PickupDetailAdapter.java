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


public class PickupDetailAdapter extends RecyclerView.Adapter<PickupDetailAdapter.ViewHolder> {
    private ArrayList<String> mData = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private PickupDetailAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(PickupDetailAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView prodImg;
        TextView prodName, storeName, confirm, puTime, dDay, text_pu;

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
            prodImg = (ImageView) itemView.findViewById(R.id.homeProdImg_item2); //제품 사진
            prodImg.setClipToOutline(true);
            confirm=(TextView) itemView.findViewById(R.id.homeProdCon_item2); //진행상황
            prodName = (TextView) itemView.findViewById(R.id.homeProdEx_item2); //제품명
            storeName = (TextView) itemView.findViewById(R.id.homeProdName_item2); //스토어명
            puTime = (TextView) itemView.findViewById(R.id.StoreProdDate2); //픽업 예정일
            //픽업 예정일 안내 문구
        }
    }

    private ArrayList<MdDetailInfo> mList = null;

    public PickupDetailAdapter(ArrayList<MdDetailInfo> mList) {
        this.mList = mList;
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.activity_order_item, parent, false);
        PickupDetailAdapter.ViewHolder vh = new PickupDetailAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PickupDetailAdapter.ViewHolder holder, int position) {
        MdDetailInfo item = mList.get(position);

        Glide.with(holder.itemView).load(item.getProdImg()).into(holder.prodImg);
        holder.confirm.setText(item.getConfirm());
        holder.prodName.setText(item.getProdName());
        holder.storeName.setText(item.getStoreName());

        // null로 넣는 경우는 content에서 오는 경우 밖에 없어서 puTime 아예 안뜨게 지정
        if (item.getPuTime().equals("null")){
            holder.text_pu.setVisibility(View.GONE);
            holder.puTime.setVisibility(View.GONE);
        }
        else holder.puTime.setText(item.getPuTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

