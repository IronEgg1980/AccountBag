package yzw.ahaqth.accountbag.allrecords;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.modules.RecordGroup;
import yzw.ahaqth.accountbag.operators.GroupOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;

public class RecordGroupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MaterialEditText inputET;
    private Button addBT;
    private RecyclerView recyclerview;
    private List<RecordGroup> mList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_group);
        mList = new ArrayList<>();
        mList.addAll(GroupOperator.findAll(false));
        adapter = new Adapter();
        initialView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addBT.requestFocus();
    }

    private void initialView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        inputET = findViewById(R.id.input_ET);
        addBT = findViewById(R.id.add_BT);
        addBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerview.setAdapter(adapter);
    }

    private void add(){
        if(TextUtils.isEmpty(inputET.getText())){
            inputET.setError("名称不能为空！");
            inputET.requestFocus();
            return;
        }
        String name = inputET.getText().toString();
        if (GroupOperator.isExist(name)){
            inputET.setError("名称重复，请修改！");
            inputET.selectAll();
            inputET.requestFocus();
            return;
        }
        int index = mList.size();
        RecordGroup recordGroup = new RecordGroup();
        recordGroup.setGroupName(name);
        recordGroup.setSortIndex(index);
        GroupOperator.save(recordGroup);
        mList.add(recordGroup);
        adapter.notifyDataSetChanged();
//        adapter.notifyItemInserted(index);
        recyclerview.smoothScrollToPosition(index);
        inputET.setText("");
        inputET.setError(null);
        inputET.requestFocus();
    }

    private void del(final int position){
        final RecordGroup recordGroup = mList.get(position);
        String s = "是否删除【"+recordGroup.getGroupName()+"】？";
        new DialogFactory(RecordGroupActivity.this)
                .showDefaultConfirmDialog(s, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GroupOperator.dele(recordGroup);
                        mList.remove(position);
                        adapter.notifyDataSetChanged();
//                        adapter.notifyItemRemoved(position);
//                        adapter.notifyItemRangeChanged(position,adapter.getItemCount() - position);
                        for(int i = position;i<mList.size();i++){
                            RecordGroup recordGroup = mList.get(i);
                            recordGroup.setSortIndex(i);
                            GroupOperator.save(recordGroup);
                        }
                    }
                });
    }

    private void edit(int position){

    }

    private void up(int position){

    }

    private void down(int position){

    }

    private class Adapter extends RecyclerView.Adapter<Adapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recordgroup_edit_item,viewGroup,false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            final int position = vh.getAdapterPosition();
            RecordGroup recordGroup = mList.get(position);
            vh.recordGroupNameTV.setText(recordGroup.getGroupName());
            vh.up.setEnabled(position!=0);
//            vh.up.setVisibility(position == 0?View.INVISIBLE:View.VISIBLE);
            vh.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    up(position);
                }
            });
            vh.down.setEnabled(position < getItemCount() - 1);
//            vh.down.setVisibility(position == getItemCount() - 1?View.INVISIBLE:View.VISIBLE);
            vh.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    down(position);
                }
            });
            vh.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    del(position);
                }
            });
            vh.rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private class VH extends RecyclerView.ViewHolder{
            private TextView recordGroupNameTV;
            private TextView up;
            private TextView down;
            private TextView del;
            private TextView rename;
            private VH(@NonNull View itemView) {
                super(itemView);
                recordGroupNameTV = itemView.findViewById(R.id.record_group_name_TV);
                up = itemView.findViewById(R.id.up);
                down = itemView.findViewById(R.id.down);
                del = itemView.findViewById(R.id.del);
                rename = itemView.findViewById(R.id.rename);
            }
        }
    }
}
