package com.cs639.newyorktrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class InventoryFragment extends Fragment {

    private Button btnAddItem;
    private FirestoreRecyclerAdapter<Item, InventoryViewHolder> adapter;
    private String selectedItem;

    public InventoryFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnAddItem.setOnClickListener(v ->
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemFragment()).commit());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_list, container, false);

        btnAddItem = view.findViewById(R.id.add_item);
        RecyclerView recyclerView = view.findViewById(R.id.inventory_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Query db to find docs matching current user and order by time added
        Query query = FirebaseFirestore.getInstance().collection("items")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>().setQuery(query, Item.class).build();

        adapter = new FirestoreRecyclerAdapter<Item, InventoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull InventoryViewHolder holder, int position, @NonNull Item inventory) {
                holder.setTitle(inventory.getName());
                //TODO: Fix 2 lines below to be responsive
                holder.setCount(1);
                holder.setThumbnail(R.drawable.office);
                holder.itemView.setOnClickListener(v -> startActivity(new Intent(getContext(), InventoryItemActivity.class)));
                holder.overflow.setOnClickListener(v -> {
                    //Get document ID
                    DocumentSnapshot document = getSnapshots().getSnapshot(position);
                    selectedItem = document.getId();
                    showPopupMenu(v);
                });
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    String docId = getSnapshots().getSnapshot(position).getId();
                    FirebaseStorage.getInstance().getReference().child("Images").child("items").child(docId).getDownloadUrl().addOnFailureListener(task -> {
                    });
                }
            }
            @NonNull
            @Override
            public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_inventory, parent, false);
                return new InventoryFragment.InventoryViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_inventory_item, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    private class InventoryViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView overflow;

        InventoryViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            overflow = view.findViewById(R.id.overflow);
        }
        void setTitle(String productTitle) {
            TextView title = view.findViewById(R.id.title);
            title.setText(productTitle);
        }
        void setCount(int productCount) {
            TextView count = view.findViewById(R.id.count);
            count.setText(Integer.toString(productCount));
        }
        void setThumbnail(int imgRes) {
            ImageView thumbnail = view.findViewById(R.id.thumbnail);
            thumbnail.setImageResource(imgRes);
        }
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    FirebaseFirestore.getInstance().collection("items").document(selectedItem).delete();
                    Map<String, Object> userItemsUpdate = new HashMap<>();
                    userItemsUpdate.put("items", FieldValue.increment(-1));
                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userItemsUpdate);
                    return true;
                case R.id.action_edit:
                new AddItemFragment();
                return true;
                case R.id.action_share:
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);

                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent,"Share"));
                    return true;
                case R.id.action_sold:
                    FirebaseFirestore.getInstance().collection("sales").document(selectedItem).delete();
                    Map<String, Object> userItemsUpdateAgain = new HashMap<>();
                    userItemsUpdateAgain.put("sales", FieldValue.increment(+1));
                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userItemsUpdateAgain);
                    return true;
                default:
            }
            return false;
        }
    }
}
