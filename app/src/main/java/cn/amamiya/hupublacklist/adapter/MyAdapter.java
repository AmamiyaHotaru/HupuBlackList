package cn.amamiya.hupublacklist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.amamiya.hupublacklist.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<String> dataList;

    public MyAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String data = dataList.get(position);
        holder.bind(data);


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.black_user);
            textView.setOnLongClickListener(this);
        }

        public void bind(String data) {
            textView.setText(data);
        }


        @Override
        public boolean onLongClick(View v) {
            if (iOnItemClickListener!=null){
                iOnItemClickListener.onItemLongClick(getAdapterPosition());
            }
            return true;
        }
    }


    private OnItemClickListener iOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.iOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemLongClick(int pos);
    }
}
