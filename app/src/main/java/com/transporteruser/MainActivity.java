   package com.transporteruser;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.User;
import com.transporteruser.databinding.ActivityMainBinding;
import com.transporteruser.fragement.HistoryFragement;
import com.transporteruser.fragement.HomeFragement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

   public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActionBarDrawerToggle toggle;
    String currentUserId;
    SharedPreferences sp = null;
    String user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        currentUserId = FirebaseAuth.getInstance().getUid();
        sp = getSharedPreferences("user",MODE_PRIVATE);
        binding.navDrawer.setItemIconTintList(null);
        binding.bottomNav.setItemIconTintList(null);
        getFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new HomeFragement()).commit();
        drawerInitialize();
        toggle = new ActionBarDrawerToggle(this,binding.drawer,binding.toolbar,R.string.open,R.string.close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        binding.civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToUpdateProfile();
            }
        });

    }

    private void getFragment(){
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.home:
                        fragment = new HomeFragement();
                        break;
                    case R.id.history:
                        fragment = new HistoryFragement();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUserId ==null){
            sendUserToLoginActivity();
        }else{
            checkProfileCreatedOrNot();
        }

    }

    private void checkProfileCreatedOrNot(){
        String status = sp.getString("userId","not_created");
        if(status.equals("not_created")){
            UserService.UserApi userApi = UserService.getTransporterApiIntance();
            Call<User> call  = userApi.getCurrentUser(currentUserId);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.code() == 200){
                        saveDataLocally(response.body());

                    }else if(response.code() == 404){
                        sendUserToCreateProfile();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
        String image = sp.getString("imageUrl","not_found");
        String name = sp.getString("name","not_found");
        if(!image.equalsIgnoreCase("not_found")){
            Picasso.get().load(image).into(binding.civProfile);
        }
        if(!name.equalsIgnoreCase("not_found"))
            binding.tvUserName.setText(name);
    }

       private void sendUserToCreateProfile() {
            Intent in = new Intent(this, CreateProfileActivity.class);
            startActivity(in);
            finish();
       }

       private void saveDataLocally(User user){
           SharedPreferences.Editor editor = sp.edit();
           editor.putString("name",user.getName());
           editor.putString("userId",user.getUserId());
           editor.putString("imageUrl",user.getImageUrl());
           editor.putString("address",user.getAddress());
           editor.putString("contactNo",user.getContactNumber());
           editor.putString("token",user.getToken());
             editor.commit();
       }
    private void drawerInitialize(){
        binding.navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                binding.drawer.closeDrawer(GravityCompat.START);
                Fragment selected = null;
                int id=item.getItemId();
                if(id == R.id.User){
                    sendUserToUpdateProfile();

                }
                else if(id == R.id.home){
                    selected = new HomeFragement();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected).commit();
                }
                else if (id == R.id.history) {
                    selected = new HistoryFragement();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected).commit();
                }
                else if(id == R.id.TermAndCondition){
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                    i.putExtra(Intent.EXTRA_TEXT, "https://transpoter-uer.flycricket.io/privacy.html");
                    startActivity(Intent.createChooser(i, "Share URL"));

                }
                else if(id == R.id.PrivacyPoalcy){
                    Intent i = new Intent(MainActivity.this, PrivacyPolicy.class);
                    startActivity(i);
                }
                else if(id == R.id.ContectUs){
                    Intent i = new Intent(MainActivity.this, ContactUs.class);
                    startActivity(i);

                }
                else if(id == R.id.AboutUs){
                    Intent i = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(i);


                }
                else if(id == R.id.Logout){
                    if(InternetUtility.isNetworkConnected(MainActivity.this)) {
                        AuthUI.getInstance()
                                .signOut(MainActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendUserToLoginActivity();
                                    }
                                });
                    }
                }
                return false;
            }
        });

    }

       private void sendUserToUpdateProfile() {
           Intent in = new Intent(MainActivity.this, UpdateProfileActivity.class);
           startActivity(in);
       }


       private void sendUserToLoginActivity(){
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        SharedPreferences.Editor editor= sp.edit();
        editor.clear();
        editor.commit();
        finish();
    }
}