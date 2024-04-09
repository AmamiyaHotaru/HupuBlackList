package cn.amamiya.hupublacklist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.amamiya.hupublacklist.adapter.MyAdapter;
import cn.amamiya.hupublacklist.utils.MultiprocessSharedPreferences;

public class SettingActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener  {

    private List<String> blacklist;
    private List<String> filterList;
    private MyAdapter adapter;
    private SharedPreferences pref;
    private TextInputEditText textInput;
    private MaterialButton searchButton;
    private MaterialButton addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        MultiprocessSharedPreferences.setAuthority("cn.amamiya.hupublacklist.provider");
        pref = MultiprocessSharedPreferences.getSharedPreferences(this, "blacklist", MODE_PRIVATE);
        String listString = pref.getString("blacklist", "");
        filterList = new ArrayList<>();
        if (listString.isEmpty()) {
            blacklist = new ArrayList<>();
        } else {
            blacklist = new ArrayList<>(Arrays.asList(listString.split(",")));
            filterList.addAll(blacklist);
        }

        Log.d("[HPBlack]",listString);

        RecyclerView recyclerView = findViewById(R.id.blacklist);
        textInput = findViewById(R.id.textInput);
        searchButton = findViewById(R.id.searchBotton);
        addButton = findViewById(R.id.addBotton);

        // 设置查询按钮的点击事件监听器
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = textInput.getText().toString();
                performSearch(searchText);

            }
        });

        // 设置新增按钮的点击事件监听器
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = textInput.getText().toString().trim();

                if (!newEntry.isEmpty()) {
                    if (!blacklist.contains(newEntry)) {
                        blacklist.add(newEntry);
                        filterList.add(newEntry);
                        adapter.notifyDataSetChanged();
                        pref.edit().putString("blacklist", String.join(",", blacklist)).apply();
                        // 清空输入框内容
                        textInput.setText("");
                    } else {
                        Toast.makeText(SettingActivity.this, "该用户已在黑名单中", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "请输入有效内容", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(filterList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void performSearch(String keyword) {
        filterList.clear();
        if (keyword.isEmpty()) {
            filterList.addAll(blacklist);
        } else {
            for (String item : blacklist) {
                if (item.toLowerCase().contains(keyword.toLowerCase())) {
                    filterList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemLongClick(int pos) {
        String user = blacklist.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否将用户 " + user + " 移出黑名单？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userName = blacklist.get(pos);
                        Toast.makeText(SettingActivity.this,"成功将"+userName+"移出黑名单",Toast.LENGTH_SHORT).show();
                        blacklist.remove(userName);
                        filterList.remove(userName);
                        adapter.notifyItemRemoved(pos);
                        pref.edit().putString("blacklist",String.join(",", blacklist)).apply();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}