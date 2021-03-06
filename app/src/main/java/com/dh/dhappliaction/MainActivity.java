package com.dh.dhappliaction;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dh.dhappliaction.activity.AboutActivity;
import com.dh.dhappliaction.activity.InfoActivity;
import com.dh.dhappliaction.activity.LoginActivity;
import com.dh.dhappliaction.bean.BaseBean;
import com.dh.dhappliaction.fragment.CallLogFragment;
import com.dh.dhappliaction.fragment.ContactsFragment;
import com.dh.dhappliaction.fragment.SMSFragment;
import com.dh.dhappliaction.other.ContactInfo;
import com.dh.dhappliaction.util.HttpUtil;
import com.dh.dhappliaction.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.navigation_main)
    NavigationView mNavigation;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.et_phone_number)
    EditText mEditText;
    @Bind(R.id.cardview_keyboard)
    CardView mCardView;

    @Bind(R.id.dial_num_1)
    ImageButton dialNum1;
    @Bind(R.id.dial_num_2)
    ImageButton dialNum2;
    @Bind(R.id.dial_num_3)
    ImageButton dialNum3;
    @Bind(R.id.dial_num_4)
    ImageButton dialNum4;
    @Bind(R.id.dial_num_5)
    ImageButton dialNum5;
    @Bind(R.id.dial_num_6)
    ImageButton dialNum6;
    @Bind(R.id.dial_num_7)
    ImageButton dialNum7;
    @Bind(R.id.dial_num_8)
    ImageButton dialNum8;
    @Bind(R.id.dial_num_9)
    ImageButton dialNum9;
    @Bind(R.id.dial_num_star)
    ImageButton dialNumStar;
    @Bind(R.id.dial_num_0)
    ImageButton dialNum0;
    @Bind(R.id.dial_num_hash)
    ImageButton dialNumHash;
    @Bind(R.id.dial_call)
    ImageButton dialCall;
    @Bind(R.id.dial_delete)
    ImageButton dialDelete;


    private static String[] titles = {"通话", "联系人", "短信"};
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private int mCurrentIndex;
    private float tempStart = 1.0f;


    private int cardViewHeight;

    public static final int RESULT_UPDATE_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initEditText();
        initPager();
        initDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("我的通讯录");

    }

    private void initEditText() {
        mEditText.setInputType(InputType.TYPE_NULL);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((CallLogFragment) fragmentList.get(0)).reloadAdapter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!TextUtils.isEmpty(mEditText.getText())) {
                    mEditText.setText("");
                }
                return true;
            }
        });
    }

    private void setAnimator(float tempEnd) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator a = ObjectAnimator.ofFloat(mFab, "alpha", tempStart, tempEnd);
        ObjectAnimator sx = ObjectAnimator.ofFloat(mFab, "scaleX", tempStart, tempEnd);
        ObjectAnimator sy = ObjectAnimator.ofFloat(mFab, "scaleY", tempStart, tempEnd);
        animatorSet.playTogether(a, sx, sy);
        animatorSet.start();
    }

    private void initPager() {
        for (int i = 0; i < titles.length; i++) {
            mTabs.addTab(mTabs.newTab().setText("通话"));
        }

        fragmentList.add(new CallLogFragment());
        fragmentList.add(new ContactsFragment());
        fragmentList.add(new SMSFragment());
        setAdapter();
        mViewpager.setOffscreenPageLimit(2);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                float tempEnd = 1;
                if ((mCurrentIndex == 0 && position == 0) || (mCurrentIndex == 1 && position == 1)) {
                    tempEnd = 1.0f - positionOffset;
                    setAnimator(tempEnd);

                } else if ((mCurrentIndex == 1 && position == 0) || (mCurrentIndex == 2 && position == 1)) {
                    tempEnd = positionOffset;
                    setAnimator(tempEnd);
                }
                tempStart = tempEnd;
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
                int id = -1;
                switch (position) {
                    case 0:
                        id = R.drawable.ic_keyboard_white_24dp;
                        break;
                    case 1:
                        id = R.drawable.ic_add_white_36dp;
                        break;
                    case 2:
                        id = R.drawable.ic_border_color_white_24dp;
                        break;
                }
                mFab.setImageResource(id);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (isT) {
                    hiddenKeyboard(cardViewHeight);
                    isT = false;
                }
                setGoneAndVisible(false);
            }
        });
    }

    private void setAdapter() {
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };
        mViewpager.setAdapter(mAdapter);
        mTabs.setupWithViewPager(mViewpager);
    }

    ContactInfo.ContactHandler infoHandler = ContactInfo.ContactHandler.getInstance();

    private List<ContactInfo> infoList = new ArrayList<>();

    ActionBarDrawerToggle toggle;
    private void initDrawer() {
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.action_settings);
        toggle.syncState();
        MenuItem menuItem = mNavigation.getMenu().findItem(R.id.nav_login);
        if (!SharedPreferencesUtil.getUser(this).equals("")) {
            menuItem.setTitle(SharedPreferencesUtil.getUser(this));
        }
        if (mNavigation != null) {
            mNavigation.getMenu().add(null);
            mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    mDrawerLayout.closeDrawers();
                    switch (menuItem.getItemId()) {
                        case R.id.nav_about:
                            startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            break;
                        case R.id.nav_login:
                            if (SharedPreferencesUtil.getUser_id(MainActivity.this).equals("")) {
                                //TODO 跳转到登录界面
                                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class),2);
                            } else {
                                //TODO 跳转到个人中心
                                startActivityForResult(new Intent(MainActivity.this, InfoActivity.class),2);
                            }
                            break;
                        case R.id.nav_synchronize:
                            if(SharedPreferencesUtil.getUser_id(MainActivity.this).equals("")){
                                Toast.makeText(MainActivity.this,"先登录后执行此操作",Toast.LENGTH_SHORT).show();
                                //TODO 跳转到登录界面
                                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class),1);
                                return true;
                            }
                            new SyncTask().execute();
                            break;
                        case R.id.nav_reload:
                            if(SharedPreferencesUtil.getUser_id(MainActivity.this).equals("")){
                                Toast.makeText(MainActivity.this,"先登录后执行此操作",Toast.LENGTH_SHORT).show();
                                //TODO 跳转到登录界面
                                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class),1);
                                return true;
                            }
                            new ReloadTask().execute();
                            break;
                    }
                    return true;
                }
            });
        }
    }

    class ReloadTask extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "同步中", "请稍等...", true, false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String vCardStr = HttpUtil.reload(SharedPreferencesUtil.getUser_id(MainActivity.this));
            try {
                infoList = infoHandler.restoreContacts(vCardStr);
                if(infoList.size()<=0){
                    return false;
                }
                for (ContactInfo c:infoList){
                    infoHandler.addContacts(MainActivity.this,c);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                ContactsFragment cf = (ContactsFragment) (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":1"));
                if(cf!=null){
                    cf.updateData();
                }
                Toast.makeText(MainActivity.this, "同步成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }


    class SyncTask extends AsyncTask<Void, Void, BaseBean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "备份中", "请稍等...", true, false);
        }

        @Override
        protected BaseBean doInBackground(Void... params) {
            String syncStr = infoHandler.backupContacts(MainActivity.this, infoHandler.getContactInfo(MainActivity.this));
            BaseBean bb = HttpUtil.sync(SharedPreferencesUtil.getUser_id(MainActivity.this), syncStr);
            return bb;
        }

        @Override
        protected void onPostExecute(BaseBean bb) {
            super.onPostExecute(bb);
            if (bb.getCode() == 1) {
                Toast.makeText(MainActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isT = false;

    private void fabToggle(int h) {
        if (!isT) {
            showKeyboard(h);
            isT = true;
        } else {
            hiddenKeyboard(h);
            isT = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定退出吗？");
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showKeyboard(int h) {
        ObjectAnimator.ofFloat(mFab, "translationY", 0, -h).setDuration(300).start();
        mCardView.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(mCardView, "translationY", h, 0).setDuration(300).start();
        mFab.setImageResource(R.drawable.ic_keyboard_hide_white_24dp);
    }

    private void hiddenKeyboard(int h) {
        ObjectAnimator.ofFloat(mFab, "translationY", -h, 0).setDuration(300).start();
        ObjectAnimator.ofFloat(mCardView, "translationY", 0, h).setDuration(300).start();
        mFab.setImageResource(R.drawable.ic_keyboard_white_24dp);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        int item = mViewpager.getCurrentItem();
        Intent intent = null;
        switch (item) {
            case 0:
                cardViewHeight = mCardView.getHeight();
                fabToggle(cardViewHeight);
                break;
            case 1:
                intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.dir/person");
                intent.setType("vnd.android.cursor.dir/contact");
                intent.setType("vnd.android.cursor.dir/raw_contact");
                startActivityForResult(intent,1);
                break;
            case 2:
                Uri uri = Uri.parse("smsto:");
                intent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==2) {
            switch (resultCode) {
                case 1:
                    mNavigation.getMenu().findItem(R.id.nav_login).setTitle(SharedPreferencesUtil.getUser(this));
                    break;
                case 2:
                    mNavigation.getMenu().findItem(R.id.nav_login).setTitle("登录");
                    break;
            }
            mNavigation.getMenu().add(null);
        }else if(requestCode==1){

            ContactsFragment cf = (ContactsFragment) (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":1"));
            if(cf!=null){
                cf.updateData();
            }
        }
    }

    @OnClick({R.id.dial_num_1, R.id.dial_num_2, R.id.dial_num_3, R.id.dial_num_4, R.id.dial_num_5, R.id.dial_num_6, R.id.dial_num_7, R.id.dial_num_8, R.id.dial_num_9, R.id.dial_num_star, R.id.dial_num_0, R.id.dial_num_hash, R.id.dial_call, R.id.dial_delete})
    public void onClick(View view) {
        setGoneAndVisible(true);
        String num = "";
        switch (view.getId()) {
            case R.id.dial_num_1:
                num = "1";
                break;
            case R.id.dial_num_2:
                num = "2";
                break;
            case R.id.dial_num_3:
                num = "3";
                break;
            case R.id.dial_num_4:
                num = "4";
                break;
            case R.id.dial_num_5:
                num = "5";
                break;
            case R.id.dial_num_6:
                num = "6";
                break;
            case R.id.dial_num_7:
                num = "7";
                break;
            case R.id.dial_num_8:
                num = "8";
                break;
            case R.id.dial_num_9:
                num = "9";
                break;
            case R.id.dial_num_star:
                num = "*";
                break;
            case R.id.dial_num_0:
                num = "0";
                break;
            case R.id.dial_num_hash:
                num = "#";
                break;
            case R.id.dial_call:
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri uri = Uri.parse("tel:" + mEditText.getText());
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.dial_delete:
                String etStr = mEditText.getText().toString();
                if (!TextUtils.isEmpty(etStr)) {
                    mEditText.setText(etStr.substring(0, etStr.length() - 1));
                    etStr = mEditText.getText().toString();
                }
                if (TextUtils.isEmpty(etStr)) {
                    setGoneAndVisible(false);
                }
                break;
        }
        mEditText.setText(mEditText.getText() + num);
    }

    private void setGoneAndVisible(boolean b) {
        if (b) {
            mToolbar.setVisibility(View.GONE);
            mTabs.setVisibility(View.GONE);

            mEditText.setVisibility(View.VISIBLE);
        } else {
            mToolbar.setVisibility(View.VISIBLE);
            mTabs.setVisibility(View.VISIBLE);
            mEditText.setText("");
            mEditText.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.editor(this,"","");
    }
}
