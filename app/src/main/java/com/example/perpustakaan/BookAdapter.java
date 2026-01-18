package com.example.perpustakaan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> itemList; // Contains String (Header) or Book (Item)
    private List<Object> itemListFull;

    public BookAdapter(List<Object> itemList) {
        this.itemList = itemList;
        this.itemListFull = new ArrayList<>(itemList);
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position) instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            return new BookViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            String headerTitle = (String) itemList.get(position);
            ((HeaderViewHolder) holder).tvHeader.setText(headerTitle);
        } else {
            Book book = (Book) itemList.get(position);
            ((BookViewHolder) holder).tvBookInfo.setText(book.getJudul() + " - " + book.getPengarang());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void filter(String text) {
        itemList.clear();
        if (text.isEmpty()) {
            itemList.addAll(itemListFull);
        } else {
            text = text.toLowerCase();
            // Simple filter logic: keep headers? Or just search items?
            // User request implies structure. If search, maybe just show items or keep
            // structure?
            // Mockup search bar is "Nama Buku". Let's search books.
            // If books found, maybe we should just list them or try to group?
            // For simplicity in this dummy: List matching books. Headers might be confusing
            // if empty.
            // Let's just list matching books for now.
            for (Object item : itemListFull) {
                if (item instanceof Book) {
                    Book book = (Book) item;
                    if (book.getJudul().toLowerCase().contains(text)
                            || book.getPengarang().toLowerCase().contains(text)) {
                        itemList.add(book);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_header_title);
        }
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookInfo;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookInfo = itemView.findViewById(R.id.tv_book_info);
        }
    }
}
