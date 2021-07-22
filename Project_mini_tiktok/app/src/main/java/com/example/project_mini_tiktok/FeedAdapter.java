package com.example.project_mini_tiktok;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project_mini_tiktok.model.VideoMessage;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.VideoViewHolder>{
    private List<VideoMessage> data;
    private FeedAdapter.IOnItemClickListener mItemClickListener;

    public void setData(List<VideoMessage> messageList){
        data = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item,parent,false);
        return new VideoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(data.get(position), position);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, data.get(position));
                }
            }
        });
        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemLongCLick(position, data.get(position));
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public void setOnItemClickListener(FeedAdapter.IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface IOnItemClickListener {
        void onItemCLick(int position, VideoMessage data);
        void onItemLongCLick(int position, VideoMessage data);
    }


    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView coverSD;
        private TextView titleTV;
        private TextView authorTV;
        private ImageView touxiangTV;
        public View contentView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView;
            titleTV = itemView.findViewById(R.id.tv_title);
            authorTV = itemView.findViewById(R.id.tv_author);
            coverSD = itemView.findViewById(R.id.sd_cover);
            touxiangTV = itemView.findViewById(R.id.touxiang);
        }

        public void bind(VideoMessage message, int position){
            coverSD.setImageURI(Uri.parse(message.getImageUrl()));
            titleTV.setText(String.format("vlog：%d", position));
            authorTV.setText("作者：" + message.getUserName());
            RequestOptions cropOptions = new RequestOptions();
            cropOptions.centerCrop().circleCrop();
            Glide.with(contentView)
                    .load(R.mipmap.tang)
                    .apply(cropOptions)
                    .transition(withCrossFade())
                    .into(touxiangTV);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }

        public void setOnLongClickListener(View.OnLongClickListener listener) {
            if (listener != null) {
                contentView.setOnLongClickListener(listener);
            }
        }
    }
}
