package com.example.cmms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<Notification> notifications;

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvDate.setText(notification.getDate());
        holder.tvMessage.setText(notification.getMessage());

        MaterialCardView card = (MaterialCardView) holder.itemView;
        if (notification.getIsRead()) {
            card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.notification_read_bg));
            holder.viewUnreadIndicator.setVisibility(View.GONE);
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.notification_unread_bg));
            holder.viewUnreadIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDate;
        TextView tvMessage;
        View viewUnreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvDate = itemView.findViewById(R.id.tv_notification_date);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            viewUnreadIndicator = itemView.findViewById(R.id.view_unread_indicator);
        }
    }
}
